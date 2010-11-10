/*
 * Copyright 2010 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.social.connect;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.security.encrypt.StringEncryptor;
import org.springframework.stereotype.Repository;

/**
 * Stores Account connection information in a relational database using the JDBC API.
 * @author Keith Donald
 */
@Repository
public class JdbcAccountConnectionRepository implements AccountConnectionRepository {

	private final JdbcTemplate jdbcTemplate;

	private final StringEncryptor encryptor;
	
	public JdbcAccountConnectionRepository(JdbcTemplate jdbcTemplate, StringEncryptor encryptor) {
		this.jdbcTemplate = jdbcTemplate;
		this.encryptor = encryptor;
	}

	public void addConnection(Object accountId, String provider, OAuthToken accessToken, String providerAccountId,
			String providerProfileUrl) {
		jdbcTemplate.update(INSERT_ACCOUNT_CONNECTION, accountId, provider, encryptor.encrypt(accessToken.getValue()), encryptIfPresent(accessToken.getSecret()), providerAccountId, providerProfileUrl);
	}

	public boolean isConnected(Object accountId, String provider) {
		return jdbcTemplate.queryForInt(SELECT_ACCOUNT_CONNECTION_COUNT, accountId, provider) == 1;
	}

	public void disconnect(Object accountId, String provider) {
		jdbcTemplate.update(DELETE_ACCOUNT_CONNECTION, accountId, provider);
	}

	public OAuthToken getAccessToken(Object accountId, String provider) {
		return jdbcTemplate.queryForObject(SELECT_ACCESS_TOKEN, new RowMapper<OAuthToken>() {
			public OAuthToken mapRow(ResultSet rs, int rowNum) throws SQLException {
				return new OAuthToken(encryptor.decrypt(rs.getString("accessToken")), decryptIfPresent(rs.getString("secret")));
			}
		}, accountId, provider);
	}

	public String getProviderAccountId(Object accountId, String provider) {
		try {
			return jdbcTemplate.queryForObject(SELECT_PROVIDER_ACCOUNT_ID, String.class, accountId, provider);
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	// internal helpers

	private String encryptIfPresent(String string) {
		return string != null ? encryptor.encrypt(string) : null;
	}

	private String decryptIfPresent(String string) {
		return string != null ? encryptor.decrypt(string) : null;
	}
	
	private static final String SELECT_PROVIDER_ACCOUNT_ID = "select accountId from AccountConnection where member = ? and provider = ?";

	private static final String SELECT_ACCOUNT_CONNECTION_COUNT = "select exists(select 1 from AccountConnection where member = ? and provider = ?)";

	private static final String INSERT_ACCOUNT_CONNECTION = "insert into AccountConnection (member, provider, accessToken, secret, accountId, profileUrl) values (?, ?, ?, ?, ?, ?)";

	private static final String DELETE_ACCOUNT_CONNECTION = "delete from AccountConnection where member = ? and provider = ?";

	private static final String SELECT_ACCESS_TOKEN = "select accessToken, secret from AccountConnection where member = ? and provider = ?";

}