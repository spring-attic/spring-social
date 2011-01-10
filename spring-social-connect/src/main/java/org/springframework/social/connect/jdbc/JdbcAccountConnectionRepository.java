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
package org.springframework.social.connect.jdbc;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.security.encrypt.StringEncryptor;
import org.springframework.social.connect.AccountConnection;
import org.springframework.social.connect.AccountConnectionRepository;
import org.springframework.social.connect.ConnectionAlreadyExistsException;
import org.springframework.social.connect.OAuthToken;
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

	public void addConnection(Serializable accountId, String provider, OAuthToken accessToken,
			String providerAccountId, String providerProfileUrl) {
		addConnection(accountId, provider, accessToken, null, providerAccountId, providerProfileUrl);
	}

	public void addConnection(Serializable accountId, String provider, OAuthToken accessToken,
			String refreshToken, String providerAccountId, String providerProfileUrl) {
		try {
			jdbcTemplate.update(CREATE_CONNECTION_QUERY, accountId, provider,
					encryptor.encrypt(accessToken.getValue()), encryptIfPresent(accessToken.getSecret()),
					encryptIfPresent(refreshToken), providerAccountId, providerProfileUrl);
		} catch (DuplicateKeyException e) {
			throw new ConnectionAlreadyExistsException("A connection already exists between account (" + accountId
					+ ") and the " + provider + " service provider.", e);
		}
	}

	public boolean isConnected(Serializable accountId, String provider) {
		return jdbcTemplate.queryForInt(CONNECTION_EXISTS_QUERY, accountId, provider) == 1;
	}

	public void disconnect(Serializable accountId, String provider) {
		jdbcTemplate.update(REMOVE_ALL_CONNECTIONS_QUERY, accountId, provider);
	}

	public void disconnect(Serializable accountId, String provider, String providerAccountId) {
		jdbcTemplate.update(REMOVE_CONNECTION_QUERY, accountId, provider, providerAccountId);
	}

	public OAuthToken getAccessToken(Serializable accountId, String provider) {
		List<OAuthToken> tokens = jdbcTemplate.query(ACCESS_TOKEN_QUERY, new RowMapper<OAuthToken>() {
			public OAuthToken mapRow(ResultSet rs, int rowNum) throws SQLException {
				return new OAuthToken(encryptor.decrypt(rs.getString("accessToken")), decryptIfPresent(rs
						.getString("secret")));
			}
		}, accountId, provider);

		return tokens.size() > 0 ? tokens.get(0) : null;
	}
	
	public OAuthToken getAccessToken(Serializable accountId, String provider, String providerAccountId) {
		return jdbcTemplate.queryForObject(ACCESS_TOKEN_BY_ACCOUNT_ID_QUERY, new RowMapper<OAuthToken>() {
			public OAuthToken mapRow(ResultSet rs, int rowNum) throws SQLException {
				return new OAuthToken(rs.getString("accessToken"), rs.getString("secret"));
			}
		}, accountId, provider, providerAccountId);
	}

	public String getRefreshToken(Serializable accountId, String provider, String providerAccountId) {
		return jdbcTemplate.queryForObject(REFRESH_TOKEN_BY_ACCOUNT_ID_QUERY, new RowMapper<String>() {
			public String mapRow(ResultSet rs, int rowNum) throws SQLException {
				return rs.getString("refreshToken");
			}
		}, accountId, provider, providerAccountId);
	}

	public String getProviderAccountId(Serializable accountId, String provider) {
		List<String> accountIds = jdbcTemplate.queryForList(PROVIDER_ACCOUNT_ID_QUERY, String.class, accountId,
				provider);
		return accountIds.size() > 0 ? accountIds.get(0) : null;
	}

	public Collection<AccountConnection> getAccountConnections(Serializable accountId, String provider) {
		return jdbcTemplate.query(ACCOUNT_CONNECTIONS_QUERY, new RowMapper<AccountConnection>() {
			public AccountConnection mapRow(ResultSet rs, int rowNum) throws SQLException {
				AccountConnection accountConnection = new AccountConnection((Serializable) rs.getObject("member"), rs
						.getString("provider"), new OAuthToken(rs.getString("accessToken"), rs.getString("secret")), rs
						.getString("refreshToken"), rs.getString("accountId"), rs.getString("profileUrl"));
				return accountConnection;
			}
		}, accountId, provider);
	}

	// internal helpers

	private String encryptIfPresent(String string) {
		return string != null ? encryptor.encrypt(string) : null;
	}

	private String decryptIfPresent(String string) {
		return string != null ? encryptor.decrypt(string) : null;
	}

	static final String PROVIDER_ACCOUNT_ID_QUERY = "select accountId from AccountConnection where member = ? and provider = ?";
	static final String CONNECTION_EXISTS_QUERY = "select exists(select 1 from AccountConnection where member = ? and provider = ?)";
	static final String CREATE_CONNECTION_QUERY = "insert into AccountConnection (member, provider, accessToken, secret, refreshToken, accountId, profileUrl) values (?, ?, ?, ?, ?, ?, ?)";
	static final String REMOVE_CONNECTION_QUERY = "delete from AccountConnection where member = ? and provider = ? and accountId = ?";
	static final String REMOVE_ALL_CONNECTIONS_QUERY = "delete from AccountConnection where member = ? and provider = ?";
	static final String ACCESS_TOKEN_QUERY = "select accessToken, secret from AccountConnection where member = ? and provider = ?";
	static final String ACCESS_TOKEN_BY_ACCOUNT_ID_QUERY = "select accessToken, secret from AccountConnection where member = ? and provider = ? and accountId = ?";
	static final String REFRESH_TOKEN_BY_ACCOUNT_ID_QUERY = "select refreshToken from AccountConnection where member = ? and provider = ? and accountId = ?";
	static final String ACCOUNT_CONNECTIONS_QUERY = "select member, provider, accessToken, secret, refreshToken, accountId, profileUrl from AccountConnection where member = ? and provider = ?";

}