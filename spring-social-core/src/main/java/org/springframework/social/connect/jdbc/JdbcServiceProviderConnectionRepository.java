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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.social.connect.ServiceProviderConnection;
import org.springframework.social.connect.ServiceProviderConnectionFactory;
import org.springframework.social.connect.ServiceProviderConnectionFactoryLocator;
import org.springframework.social.connect.ServiceProviderConnectionLocator;
import org.springframework.social.connect.ServiceProviderConnectionMemento;
import org.springframework.social.connect.ServiceProviderConnectionRepository;

public class JdbcServiceProviderConnectionRepository implements ServiceProviderConnectionRepository, ServiceProviderConnectionLocator {

	private final JdbcTemplate jdbcTemplate;
	
	private final TextEncryptor textEncryptor;

	private final ServiceProviderConnectionFactoryLocator connectionFactoryLocator;
	
	public JdbcServiceProviderConnectionRepository(DataSource dataSource, TextEncryptor textEncryptor, ServiceProviderConnectionFactoryLocator connectionFactoryLocator) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
		this.textEncryptor = textEncryptor;
		this.connectionFactoryLocator = connectionFactoryLocator;
		this.connectionInsert = createConnectionInsertStatement();
	}

	// implementing ServiceProviderConnectionRepository
	
	public Map<String, List<ServiceProviderConnection<?>>> findConnections(Serializable accountId) {
		List<ServiceProviderConnection<?>> connections = jdbcTemplate.query(SELECT_FROM_SERVICE_PROVIDER_CONNECTION + " where accountId = ? order by providerId, id", connectionMapper, accountId);
		Map<String, List<ServiceProviderConnection<?>>> providerConnectionMap = new HashMap<String, List<ServiceProviderConnection<?>>>();
		for (ServiceProviderConnection<?> connection : connections) {
			List<ServiceProviderConnection<?>> providerConnections = providerConnectionMap.get(connection.getProviderId());
			if (providerConnections == null) {
				providerConnections = new ArrayList<ServiceProviderConnection<?>>();
				providerConnectionMap.put(connection.getProviderId(), providerConnections);
			}
			providerConnections.add(connection);
		}
		return providerConnectionMap;
	}

	public List<ServiceProviderConnection<?>> findConnectionsToProvider(Serializable accountId, String providerId) {
		return jdbcTemplate.query(SELECT_FROM_SERVICE_PROVIDER_CONNECTION + " where accountId = ? and providerId = ? order by providerId, id", connectionMapper, accountId, providerId);
	}

	public List<ServiceProviderConnection<?>> findConnectionsById(Serializable accountId, List<Long> connectionIds) {
		if (connectionIds.isEmpty()) {
			throw new IllegalArgumentException("Unable to execute find: no connectionIds provided");
		}
		return jdbcTemplate.query(SELECT_FROM_SERVICE_PROVIDER_CONNECTION + " where id in (?) and accountId = ? order by providerId, id", connectionMapper, connectionIds, accountId);
	}

	public ServiceProviderConnection<?> findConnectionById(Serializable accountId, Long connectionId) {
		return jdbcTemplate.queryForObject(SELECT_FROM_SERVICE_PROVIDER_CONNECTION + " where id = ? and accountId", connectionMapper, connectionId, accountId);
	}

	public List<ServiceProviderConnection<?>> findConnectionsToProviderAccount(String providerId, String providerAccountId) {
		return jdbcTemplate.query(SELECT_FROM_SERVICE_PROVIDER_CONNECTION + " where providerId = ? and providerAccountId = ?", connectionMapper, providerId, providerAccountId);
	}

	public <S> ServiceProviderConnection<S> saveConnection(ServiceProviderConnection<S> connection) {
		if (connection.getId() == null) {
			if (connection.getAccountId() == null) {
				throw new IllegalArgumentException("Unable to save new connection because it has not been assigned to a local account; call ServiceProviderConnection#assignAccountId(Serializable) before saving");
			}
			Long connectionId = insertConnection(connection.createMemento());
			return connection.assignId(connectionId);
		} else {
			updateConnection(connection.createMemento());
			return connection;
		}
	}

	public void removeConnectionsToProvider(Serializable accountId, String providerId) {
		jdbcTemplate.update("delete from ServiceProviderConnection where accountId = ? and providerId = ?", accountId, providerId);
	}

	public void removeConnection(Serializable accountId, Long connectionId) {
		jdbcTemplate.update("delete from ServiceProviderConnection where id = ? and accountId = ?", connectionId, accountId);
	}

	// implementing ServiceProviderConnectionLocator

	@SuppressWarnings("unchecked")
	public <S> ServiceProviderConnection<S> getPrimaryConnection(Serializable accountId, Class<S> serviceApiType) {
		ServiceProviderConnectionFactory<S> connectionFactory = connectionFactoryLocator.getConnectionFactory(serviceApiType);
		List<ServiceProviderConnection<?>> connections = findConnectionsToProvider(accountId, connectionFactory.getProviderId());
		if (connections.size() > 0) {
			return (ServiceProviderConnection<S>) connections.get(0);
		} else {
			throw new IllegalStateException("No connection between account " + accountId + " and ServiceProvider API [" + serviceApiType.getName() +  "] exists");			
		}
	}
	
	@SuppressWarnings("unchecked")
	public <S> ServiceProviderConnection<S> getConnection(Serializable accountId, Long connectionId, Class<S> serviceApiType) {
		return (ServiceProviderConnection<S>) findConnectionById(accountId, connectionId);
	}

	private final static String SELECT_FROM_SERVICE_PROVIDER_CONNECTION = "select accountId, providerId, id, providerAccountId, profileName, profileUrl, profilePictureUrl, allowSignIn, accessToken, secret, refreshToken from ServiceProviderConnection";
		
	private final ServiceProviderConnectionRowMapper connectionMapper = new ServiceProviderConnectionRowMapper();
	
	private class ServiceProviderConnectionRowMapper implements RowMapper<ServiceProviderConnection<?>> {
		
		public ServiceProviderConnection<?> mapRow(ResultSet rs, int rowNum) throws SQLException {
			ServiceProviderConnectionMemento connectionMemento = mapConnectionMemento(rs);
			return connectionFactoryLocator.getConnectionFactory(connectionMemento.getProviderId()).createConnection(connectionMemento);
		}
		
		private ServiceProviderConnectionMemento mapConnectionMemento(ResultSet rs) throws SQLException {
			return new ServiceProviderConnectionMemento(rs.getLong("id"), (Serializable) rs.getObject("accountId"), rs.getString("providerId"), rs.getString("providerAccountId"),
					rs.getString("profileName"), rs.getString("profileUrl"), rs.getString("profilePictureUrl"),
					rs.getBoolean("allowSignin"),
					decrypt(rs.getString("accessToken")), decrypt(rs.getString("secret")), decrypt(rs.getString("refreshToken")));							
		}
		
		private String decrypt(String encryptedText) {
			return encryptedText != null ? textEncryptor.decrypt(encryptedText) : encryptedText;
		}
		
	}

	private final SimpleJdbcInsert connectionInsert;
	
	private SimpleJdbcInsert createConnectionInsertStatement() {
		SimpleJdbcInsert insert = new SimpleJdbcInsert(jdbcTemplate);
		insert.setTableName("ServiceProviderConnection");
		insert.setColumnNames(Arrays.asList("accountId", "providerId", "providerAccountId", "profileName", "profileUrl", "profilePictureUrl", "allowSignIn", "accessToken", "secret", "refreshToken"));
		insert.setGeneratedKeyName("id");
		return insert;
	}

	private Long insertConnection(ServiceProviderConnectionMemento memento) {
		Map<String, Object> args = new HashMap<String, Object>();
		args.put("accountId", memento.getAccountId());
		args.put("providerId", memento.getProviderId());
		args.put("providerAccountId", memento.getProviderAccountId());
		args.put("profileName", memento.getProfileName());
		args.put("profileUrl", memento.getProfileUrl());
		args.put("profilePictureUrl", memento.getProfilePictureUrl());
		args.put("allowSignIn", memento.isAllowSignIn());
		args.put("accessToken", encrypt(memento.getAccessToken()));
		args.put("secret", encrypt(memento.getSecret()));
		args.put("refreshToken", encrypt(memento.getRefreshToken()));
		return (Long) connectionInsert.executeAndReturnKey(args);
	}

	private void updateConnection(ServiceProviderConnectionMemento memento) {
		jdbcTemplate.update("update ServiceProviderConnection set accountId = ?, providerId = ?, providerAccountId = ?, profileName = ?, profileUrl = ?, profilePictureUrl = ?, allowSignIn = ?, accessToken = ?, secret = ?, refreshToken = ? where id = ?", 
				memento.getAccountId(), memento.getProviderId(), memento.getProviderAccountId(), memento.getProfileName(), memento.getProfileUrl(), memento.getProfilePictureUrl(), memento.isAllowSignIn(),
				encrypt(memento.getAccessToken()), encrypt(memento.getSecret()), encrypt(memento.getRefreshToken()), memento.getId());
	}

	private String encrypt(String text) {
		return text != null ? textEncryptor.encrypt(text) : text;
	}	
	
}