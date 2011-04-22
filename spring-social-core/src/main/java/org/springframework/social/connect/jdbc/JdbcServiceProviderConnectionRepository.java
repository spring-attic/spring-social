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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.social.connect.DuplicateServiceProviderConnectionException;
import org.springframework.social.connect.NoSuchServiceProviderConnectionException;
import org.springframework.social.connect.ServiceProviderConnection;
import org.springframework.social.connect.ServiceProviderConnectionData;
import org.springframework.social.connect.ServiceProviderConnectionFactory;
import org.springframework.social.connect.ServiceProviderConnectionFactoryLocator;
import org.springframework.social.connect.ServiceProviderConnectionKey;
import org.springframework.social.connect.ServiceProviderConnectionRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

class JdbcServiceProviderConnectionRepository implements ServiceProviderConnectionRepository {

	private final String localUserId;
	
	private final JdbcTemplate jdbcTemplate;
	
	private final ServiceProviderConnectionFactoryLocator connectionFactoryLocator;

	private final TextEncryptor textEncryptor;

	public JdbcServiceProviderConnectionRepository(String localUserId, JdbcTemplate jdbcTemplate, ServiceProviderConnectionFactoryLocator connectionFactoryLocator, TextEncryptor textEncryptor) {
		this.localUserId = localUserId;
		this.jdbcTemplate = jdbcTemplate;
		this.connectionFactoryLocator = connectionFactoryLocator;
		this.textEncryptor = textEncryptor;
	}
	
	public MultiValueMap<String, ServiceProviderConnection<?>> findConnections() {
		List<ServiceProviderConnection<?>> resultList = jdbcTemplate.query(SELECT_FROM_SERVICE_PROVIDER_CONNECTION + " where localUserId = ? order by providerId, rank", connectionMapper, localUserId);
		MultiValueMap<String, ServiceProviderConnection<?>> connections = new LinkedMultiValueMap<String, ServiceProviderConnection<?>>();
		Set<String> registeredProviderIds = connectionFactoryLocator.registeredProviderIds();
		for (String registeredProviderId : registeredProviderIds) {
			connections.put(registeredProviderId, Collections.<ServiceProviderConnection<?>>emptyList());
		}
		for (ServiceProviderConnection<?> connection : resultList) {
			String providerId = connection.getKey().getProviderId();
			if (connections.get(providerId).size() == 0) {
				connections.put(providerId, new LinkedList<ServiceProviderConnection<?>>());
			}
			connections.add(providerId, connection);
		}
		return connections;
	}

	public List<ServiceProviderConnection<?>> findConnectionsToProvider(String providerId) {
		return jdbcTemplate.query(SELECT_FROM_SERVICE_PROVIDER_CONNECTION + " where localUserId = ? and providerId = ? order by rank", connectionMapper, localUserId, providerId);
	}

	public MultiValueMap<String, ServiceProviderConnection<?>> findConnectionsForUsers(MultiValueMap<String, String> providerUsers) {
		if (providerUsers.isEmpty()) {
			throw new IllegalArgumentException("Unable to execute find: no providerUsers provided");
		}
		StringBuilder providerUsersCriteriaSql = new StringBuilder();
		MapSqlParameterSource parameters = new MapSqlParameterSource();
		parameters.addValue("localUserId", localUserId);
		for (Iterator<Entry<String, List<String>>> it = providerUsers.entrySet().iterator(); it.hasNext();) {
			Entry<String, List<String>> entry = it.next();
			String providerId = entry.getKey();
			providerUsersCriteriaSql.append("providerId = :providerId_").append(providerId).append(" and providerUserId in (:providerUserIds_").append(providerId).append(")");
			parameters.addValue("providerId_" + providerId, providerId);
			parameters.addValue("providerUserIds_" + providerId, entry.getValue());
			if (it.hasNext()) {
				providerUsersCriteriaSql.append(" or " );
			}
		}
		List<ServiceProviderConnection<?>> resultList = new NamedParameterJdbcTemplate(jdbcTemplate).query(SELECT_FROM_SERVICE_PROVIDER_CONNECTION + " where localUserId = :localUserId and " + providerUsersCriteriaSql + " order by providerId, rank", parameters, connectionMapper);
		MultiValueMap<String, ServiceProviderConnection<?>> connectionsForUsers = new LinkedMultiValueMap<String, ServiceProviderConnection<?>>();
		for (ServiceProviderConnection<?> connection : resultList) {
			String providerId = connection.getKey().getProviderId();
			List<String> userIds = providerUsers.get(providerId);
			List<ServiceProviderConnection<?>> connections = connectionsForUsers.get(providerId);
			if (connections == null) {
				connections = new ArrayList<ServiceProviderConnection<?>>(userIds.size());
				for (int i = 0; i < userIds.size(); i++) {
					connections.add(null);
				}
				connectionsForUsers.put(providerId, connections);
			}
			String providerUserId = connection.getKey().getProviderUserId();
			int connectionIndex = userIds.indexOf(providerUserId);
			connections.set(connectionIndex, connection);
		}
		return connectionsForUsers;
	}

	public ServiceProviderConnection<?> findConnection(ServiceProviderConnectionKey connectionKey) {
		try {
			return jdbcTemplate.queryForObject(SELECT_FROM_SERVICE_PROVIDER_CONNECTION + " where localUserId = ? and providerId = ? and providerUserId = ?", connectionMapper, localUserId, connectionKey.getProviderId(), connectionKey.getProviderUserId());
		} catch (EmptyResultDataAccessException e) {
			throw new NoSuchServiceProviderConnectionException(connectionKey);
		}
	}

	@SuppressWarnings("unchecked")
	public <S> ServiceProviderConnection<S> findConnectionByServiceApi(Class<S> serviceApiType) {
		try {
			String providerId = getProviderId(serviceApiType);
			return (ServiceProviderConnection<S>) jdbcTemplate.queryForObject(SELECT_FROM_SERVICE_PROVIDER_CONNECTION + " where localUserId = ? and providerId = ? and rank = 1", connectionMapper, localUserId, providerId);
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	public <S> List<ServiceProviderConnection<S>> findConnectionsByServiceApi(Class<S> serviceApiType) {
		List<?> connections = findConnectionsToProvider(getProviderId(serviceApiType));
		return (List<ServiceProviderConnection<S>>) connections;
	}
	
	@SuppressWarnings("unchecked")
	public <S> ServiceProviderConnection<S> findConnectionByServiceApiForUser(Class<S> serviceApiType, String providerUserId) {
		String providerId = getProviderId(serviceApiType);
		return (ServiceProviderConnection<S>) findConnection(new ServiceProviderConnectionKey(providerId, providerUserId));
	}

	@Transactional
	public void addConnection(ServiceProviderConnection<?> connection) {
		try {
			ServiceProviderConnectionData data = connection.createData();
			int rank = jdbcTemplate.queryForInt("(select ifnull(max(rank) + 1, 1) as rank from ServiceProviderConnection where localUserId = ? and providerId = ?)", localUserId, data.getProviderId());
			jdbcTemplate.update("insert into ServiceProviderConnection (localUserId, providerId, providerUserId, rank, displayName, profileUrl, imageUrl, accessToken, secret, refreshToken, expireTime) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
					localUserId, data.getProviderId(), data.getProviderUserId(), rank, data.getDisplayName(), data.getProfileUrl(), data.getImageUrl(), encrypt(data.getAccessToken()), encrypt(data.getSecret()), encrypt(data.getRefreshToken()), data.getExpireTime());
		} catch (DuplicateKeyException e) {
			throw new DuplicateServiceProviderConnectionException(connection.getKey());
		}
	}
	
	public void updateConnection(ServiceProviderConnection<?> connection) {
		ServiceProviderConnectionData data = connection.createData();
		jdbcTemplate.update("update ServiceProviderConnection set displayName = ?, profileUrl = ?, imageUrl = ?, accessToken = ?, secret = ?, refreshToken = ?, expireTime = ? where localUserId = ? and providerId = ? and providerUserId = ?",
				data.getDisplayName(), data.getProfileUrl(), data.getImageUrl(), encrypt(data.getAccessToken()), encrypt(data.getSecret()), encrypt(data.getRefreshToken()), data.getExpireTime(), localUserId, data.getProviderId(), data.getProviderUserId());
	}

	public void removeConnectionsToProvider(String providerId) {
		jdbcTemplate.update("delete from ServiceProviderConnection where localUserId = ? and providerId = ?", localUserId, providerId);
	}

	public void removeConnection(ServiceProviderConnectionKey connectionKey) {
		jdbcTemplate.update("delete from ServiceProviderConnection where localUserId = ? and providerId = ? and providerUserId = ?", localUserId, connectionKey.getProviderId(), connectionKey.getProviderUserId());		
	}

	// internal helpers
	
	private final static String SELECT_FROM_SERVICE_PROVIDER_CONNECTION = "select localUserId, providerId, providerUserId, displayName, profileUrl, imageUrl, accessToken, secret, refreshToken, expireTime from ServiceProviderConnection";
	
	
	private final ServiceProviderConnectionMapper connectionMapper = new ServiceProviderConnectionMapper();
	
	private final class ServiceProviderConnectionMapper implements RowMapper<ServiceProviderConnection<?>> {
		
		public ServiceProviderConnection<?> mapRow(ResultSet rs, int rowNum) throws SQLException {
			ServiceProviderConnectionData connectionData = mapConnectionData(rs);
			ServiceProviderConnectionFactory<?> connectionFactory = connectionFactoryLocator.getConnectionFactory(connectionData.getProviderId());
			return connectionFactory.createConnection(connectionData);
		}
		
		private ServiceProviderConnectionData mapConnectionData(ResultSet rs) throws SQLException {
			return new ServiceProviderConnectionData(rs.getString("providerId"), rs.getString("providerUserId"), rs.getString("displayName"), rs.getString("profileUrl"), rs.getString("imageUrl"),
					decrypt(rs.getString("accessToken")), decrypt(rs.getString("secret")), decrypt(rs.getString("refreshToken")), expireTime(rs.getLong("expireTime")));
		}
		
		private String decrypt(String encryptedText) {
			return encryptedText != null ? textEncryptor.decrypt(encryptedText) : encryptedText;
		}
		
		private Long expireTime(long expireTime) {
			return expireTime == 0 ? null : expireTime;
		}
		
	}

	private <S> String getProviderId(Class<S> serviceApiType) {
		return connectionFactoryLocator.getConnectionFactory(serviceApiType).getProviderId();
	}
	
	private String encrypt(String text) {
		return text != null ? textEncryptor.encrypt(text) : text;
	}

}