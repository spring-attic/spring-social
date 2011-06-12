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

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

import javax.sql.DataSource;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionFactoryLocator;
import org.springframework.social.connect.ConnectionKey;
import org.springframework.social.connect.ConnectionRepository;
import org.springframework.social.connect.ConnectionSignUp;
import org.springframework.social.connect.NoOpConnectionSignUp;
import org.springframework.social.connect.UsersConnectionRepository;

/**
 * {@link UsersConnectionRepository} that uses the JDBC API to persist connection data to a relational database.
 * The supporting schema is defined in JdbcMultiUserConnectionRepository.sql.
 * @author Keith Donald
 */
public class JdbcUsersConnectionRepository implements UsersConnectionRepository {

	private final JdbcTemplate jdbcTemplate;
	
	private final ConnectionFactoryLocator connectionFactoryLocator;

	private final TextEncryptor textEncryptor;
	
	private String tablePrefix = DEFAULT_TABLE_PREFIX;

	private ConnectionSignUp connectionSignUp = NoOpConnectionSignUp.INSTANCE;
	
	public JdbcUsersConnectionRepository(DataSource dataSource, ConnectionFactoryLocator connectionFactoryLocator, TextEncryptor textEncryptor) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
		this.connectionFactoryLocator = connectionFactoryLocator;
		this.textEncryptor = textEncryptor;
	}
	
	/**
	 * Sets a table name prefix. This will be prefixed to all the table names before queries are executed. Defaults to "".
	 * This is can be used to qualify the table name with a schema or to distinguish Spring Social tables from other application tables. 
	 * @param tablePrefix the tablePrefix to set
	 */
	public void setTablePrefix(String tablePrefix) {
		this.tablePrefix = tablePrefix;
	}
	
	/**
	 * The command to execute to create a new local user account in the event no user id could be mapped to a connection.
	 * Allows for implicitly creating a local user account from connection data during a provider sign-in attempt.
	 * Defaults to an implementation that simply returns null, indicating explicit sign-up will be required to complete the provider sign-in attempt.
	 * @see #findUserIdWithConnection(Connection)
	 */
	public void setConnectionSignUp(ConnectionSignUp connectionSignUp) {
		this.connectionSignUp = connectionSignUp;
	}

	public String findUserIdWithConnection(Connection<?> connection) {
		try {
			ConnectionKey key = connection.getKey();
			return jdbcTemplate.queryForObject("select userId from " + tablePrefix + "UserConnection where providerId = ? and providerUserId = ?", String.class, key.getProviderId(), key.getProviderUserId());
		} catch (IncorrectResultSizeDataAccessException e) {
			if (e.getActualSize() == 0) {
				String userId = connectionSignUp.execute(connection);
				if (userId != null) {
					createConnectionRepository(userId).addConnection(connection);
				}
				return userId;
			} else {
				return null;				
			}
		}
	}

	public Set<String> findUserIdsConnectedTo(String providerId, Set<String> providerUserIds) {
		MapSqlParameterSource parameters = new MapSqlParameterSource();
		parameters.addValue("providerId", providerId);
		parameters.addValue("providerUserIds", providerUserIds);
		final Set<String> localUserIds = new HashSet<String>();
		return new NamedParameterJdbcTemplate(jdbcTemplate).query("select userId from " + tablePrefix + "UserConnection where providerId = :providerId and providerUserId in (:providerUserIds)", parameters,
			new ResultSetExtractor<Set<String>>() {
				public Set<String> extractData(ResultSet rs) throws SQLException, DataAccessException {
					while (rs.next()) {
						localUserIds.add(rs.getString("userId"));
					}
					return localUserIds;
				}
			});
	}

	public ConnectionRepository createConnectionRepository(String userId) {
		return new JdbcConnectionRepository(userId, jdbcTemplate, connectionFactoryLocator, textEncryptor, tablePrefix);
	}

	private static final String DEFAULT_TABLE_PREFIX = "";
}