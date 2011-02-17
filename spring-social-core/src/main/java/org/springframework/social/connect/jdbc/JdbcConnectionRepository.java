/*
 * Copyright 2011 the original author or authors.
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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.social.connect.support.Connection;
import org.springframework.social.connect.support.ConnectionRepository;

/**
 * JDBC-based connection repository implementation.
 * @author Keith Donald
 */
public class JdbcConnectionRepository implements ConnectionRepository {

	private final JdbcTemplate jdbcTemplate;

	private final TextEncryptor textEncryptor;

	private final SimpleJdbcInsert connectionInsert;
	
	/**
	 * Creates a JDBC-based connection repository.
	 * @param dataSource the data source
	 * @param textEncryptor the encryptor to use when storing oauth keys
	 */
	public JdbcConnectionRepository(DataSource dataSource, TextEncryptor textEncryptor) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
		this.textEncryptor = textEncryptor;
		this.connectionInsert = createConnectionInsert();
	}

	public boolean isConnected(Serializable accountId, String providerId) {
		return jdbcTemplate.queryForObject("select exists(select 1 from Connection where accountId = ? and providerId = ?)", Boolean.class, accountId, providerId);
	}

	public List<Connection> findConnections(Serializable accountId, String providerId) {
		return jdbcTemplate.query("select id, accessToken, secret, refreshToken, providerAccountId from Connection where accountId = ? and providerId = ? order by id", new RowMapper<Connection>() {
			public Connection mapRow(ResultSet rs, int rowNum) throws SQLException {
				return new Connection(rs.getLong("id"), decrypt(rs.getString("accessToken")), decrypt(rs.getString("secret")), decrypt(rs.getString("refreshToken")), rs.getString("providerAccountId"));
			}
		}, accountId, providerId);
	}

	public void removeConnection(Serializable accountId, String providerId, Long connectionId) {
		jdbcTemplate.update("delete from Connection where accountId = ? and providerId = ? and id = ?", accountId, providerId, connectionId);
	}

	public Connection saveConnection(Serializable accountId, String providerId, Connection connection) {
		try {
			Map<String, Object> args = new HashMap<String, Object>();
			args.put("accountId", accountId);
			args.put("providerId", providerId);
			args.put("accessToken", encrypt(connection.getAccessToken()));
			args.put("secret", encrypt(connection.getSecret()));
			args.put("refreshToken", encrypt(connection.getRefreshToken()));
			args.put("providerAccountId", connection.getProviderAccountId());
			Number connectionId = connectionInsert.executeAndReturnKey(args);
			return new Connection((Long) connectionId, connection.getAccessToken(), connection.getSecret(), connection.getRefreshToken(), connection.getProviderAccountId());
		} catch (DuplicateKeyException e) {
			throw new IllegalArgumentException("Access token is not unique: a connection already exists!", e);
		}
	}

	public Serializable findAccountIdByConnectionAccessToken(String provider, String accessToken) {
		List<Serializable> matches = jdbcTemplate.query("select accountId from Connection where providerId = ? and accessToken = ?", new RowMapper<Serializable>() {
			public Serializable mapRow(ResultSet rs, int rowNum) throws SQLException {
						return (Serializable) rs.getObject("accountId");
			}
		}, provider, encrypt(accessToken));

		return !matches.isEmpty() ? matches.get(0) : null;
	}

	public List<Serializable> findAccountIdsForProviderAccountIds(String providerId, List<String> providerAccountIds) {
		NamedParameterJdbcTemplate namedTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
		Map<String, Object> params = new HashMap<String, Object>(2, 1);
		params.put("providerId", providerId);
		params.put("providerAccountIds", providerAccountIds);
		return namedTemplate.query("select accountId from Connection where providerId = :providerId and providerAccountId in ( :providerAccountIds )", params, new RowMapper<Serializable>() {
			public Serializable mapRow(ResultSet rs, int rowNum) throws SQLException {
				return (Serializable) rs.getString("accountId");
			}
		});
	}

	// internal helpers
	
	private String encrypt(String text) {
		return text != null ? textEncryptor.encrypt(text) : text;
	}
	
	private String decrypt(String encryptedText) {
		return encryptedText != null ? textEncryptor.decrypt(encryptedText) : encryptedText;
	}
	
	private SimpleJdbcInsert createConnectionInsert() {
		SimpleJdbcInsert insert = new SimpleJdbcInsert(jdbcTemplate);
		insert.setTableName("Connection");
		insert.setColumnNames(Arrays.asList("accountId", "providerId", "accessToken", "secret", "refreshToken", "providerAccountId"));
		insert.setGeneratedKeyName("id");
		return insert;
	}
}