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

import java.io.Serializable;
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
		this.providerAccountIdQuery = DEFAULT_PROVIDER_ACCOUNT_ID_QUERY;
		this.connectionExistsQuery = DEFAULT_CONNECTION_EXISTS_QUERY;
		this.createConnectionQuery = DEFAULT_CREATE_CONNECTION_QUERY;
		this.removeConnectionQuery = DEFAULT_REMOVE_CONNECTION_QUERY;
		this.accessTokenQuery = DEFAULT_ACCESS_TOKEN_QUERY;
	}

	public String getProviderAccountIdByMemberAndProviderQuery() {
		return providerAccountIdQuery;
	}

	/**
	 * <p>
	 * Overrides the default query for selecting a user's provider account ID
	 * given their local member ID and the provider ID.
	 * </p>
	 * 
	 * <p>
	 * The default query is:
	 * </p>
	 * 
	 * <code>
	 * select accountId from AccountConnection where member = ? and provider = ?
	 * </code>
	 * 
	 * <p>
	 * An overriding query should follow a similar form, taking a local member
	 * ID and a provider ID as parameters and returning the provider account ID
	 * as a single column result.
	 * 
	 * @param providerAccountIdQuery
	 */
	public void setProviderAccountIdQuery(String providerAccountIdQuery) {
		this.providerAccountIdQuery = providerAccountIdQuery;
	}

	public String getConnectionExistsQuery() {
		return connectionExistsQuery;
	}

	/**
	 * <p>
	 * Overrides the default query for establishing the existence of one or more
	 * connections between the application and a provider.
	 * </p>
	 * 
	 * <p>
	 * The default query is:
	 * </p>
	 * 
	 * <code>
	 * select exists(select 1 from AccountConnection where member = ? and provider = ?)
	 * </code>
	 * 
	 * <p>
	 * An overriding query should follow a similar form, taking a local member
	 * ID and a provider ID as parameters and returning true if one or more
	 * connections exist.
	 * 
	 * @param connectionExistsQuery
	 */
	public void setConnectionExistsQuery(String connectionExistsQuery) {
		this.connectionExistsQuery = connectionExistsQuery;
	}

	public String getCreateConnectionQuery() {
		return createConnectionQuery;
	}

	/**
	 * <p>
	 * Overrides the default query for inserting a new connection.
	 * </p>
	 * 
	 * <p>
	 * The default query is:
	 * </p>
	 * 
	 * <code>
	 * insert into AccountConnection (member, provider, accessToken, secret, accountId, profileUrl) values (?, ?, ?, ?, ?, ?)
	 * </code>
	 * 
	 * <p>
	 * An overriding query should follow a similar form, taking a local member
	 * ID, a provider ID, an access token, an access token secret, a provider
	 * account ID, and a provider profile URL as parameters.
	 * 
	 * @param createConnectionQuery
	 */
	public void setCreateConnectionQuery(String createConnectionQuery) {
		this.createConnectionQuery = createConnectionQuery;
	}

	public String getRemoveConnectionQuery() {
		return removeConnectionQuery;
	}

	/**
	 * <p>
	 * Overrides the default query for deleting a connection.
	 * </p>
	 * 
	 * <p>
	 * The default query is:
	 * </p>
	 * 
	 * <code>
	 * delete from AccountConnection where member = ? and provider = ?
	 * </code>
	 * 
	 * <p>
	 * An overriding query should follow a similar form, taking a local member
	 * ID and a provider ID.
	 * 
	 * @param removeConnectionQuery
	 */
	public void setRemoveConnectionQuery(String removeConnectionQuery) {
		this.removeConnectionQuery = removeConnectionQuery;
	}

	public String getAccessTokenQuery() {
		return accessTokenQuery;
	}

	/**
	 * <p>
	 * Overrides the default query for selecting an access token
	 * </p>
	 * 
	 * <p>
	 * The default query is:
	 * </p>
	 * 
	 * <code>
	 * select accessToken, secret from AccountConnection where member = ? and provider = ?
	 * </code>
	 * 
	 * <p>
	 * An overriding query should follow a similar form, taking a local member
	 * ID and a provider ID and returning the access token and access token
	 * secret.
	 * 
	 * @param accessTokenQuery
	 */
	public void setAccessTokenQuery(String accessTokenQuery) {
		this.accessTokenQuery = accessTokenQuery;
	}

	public void addConnection(Serializable accountId, String provider, OAuthToken accessToken,
			String providerAccountId,
			String providerProfileUrl) {
		jdbcTemplate.update(DEFAULT_CREATE_CONNECTION_QUERY, accountId, provider,
				encryptor.encrypt(accessToken.getValue()), encryptIfPresent(accessToken.getSecret()),
				providerAccountId, providerProfileUrl);
	}

	public boolean isConnected(Serializable accountId, String provider) {
		return jdbcTemplate.queryForInt(connectionExistsQuery, accountId, provider) == 1;
	}

	public void disconnect(Serializable accountId, String provider) {
		jdbcTemplate.update(removeConnectionQuery, accountId, provider);
	}

	public OAuthToken getAccessToken(Serializable accountId, String provider) {
		return jdbcTemplate.queryForObject(accessTokenQuery, new RowMapper<OAuthToken>() {
			public OAuthToken mapRow(ResultSet rs, int rowNum) throws SQLException {
				return new OAuthToken(encryptor.decrypt(rs.getString("accessToken")), decryptIfPresent(rs.getString("secret")));
			}
		}, accountId, provider);
	}

	public String getProviderAccountId(Serializable accountId, String provider) {
		try {
			return jdbcTemplate.queryForObject(providerAccountIdQuery, String.class, accountId,
					provider);
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
	
	private String providerAccountIdQuery;
	private String connectionExistsQuery;
	private String createConnectionQuery;
	private String removeConnectionQuery;
	private String accessTokenQuery;

	static final String DEFAULT_PROVIDER_ACCOUNT_ID_QUERY = "select accountId from AccountConnection where member = ? and provider = ?";
	static final String DEFAULT_CONNECTION_EXISTS_QUERY = "select exists(select 1 from AccountConnection where member = ? and provider = ?)";
	static final String DEFAULT_CREATE_CONNECTION_QUERY = "insert into AccountConnection (member, provider, accessToken, secret, accountId, profileUrl) values (?, ?, ?, ?, ?, ?)";
	static final String DEFAULT_REMOVE_CONNECTION_QUERY = "delete from AccountConnection where member = ? and provider = ?";
	static final String DEFAULT_ACCESS_TOKEN_QUERY = "select accessToken, secret from AccountConnection where member = ? and provider = ?";
}