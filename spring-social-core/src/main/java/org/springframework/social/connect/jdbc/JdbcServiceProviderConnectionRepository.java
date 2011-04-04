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
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.social.connect.ServiceProviderConnection;
import org.springframework.social.connect.ServiceProviderConnectionFactory;
import org.springframework.social.connect.ServiceProviderConnectionFactoryLocator;
import org.springframework.social.connect.ServiceProviderConnectionKey;
import org.springframework.social.connect.ServiceProviderConnectionMemento;
import org.springframework.social.connect.ServiceProviderConnectionRepository;
import org.springframework.social.connect.support.LocalUserIdLocator;

public class JdbcServiceProviderConnectionRepository implements ServiceProviderConnectionRepository {

	private final JdbcTemplate jdbcTemplate;
	
	private final ServiceProviderConnectionFactoryLocator connectionFactoryLocator;

	private final LocalUserIdLocator localUserIdLocator;
	
	private final TextEncryptor textEncryptor;

	public JdbcServiceProviderConnectionRepository(DataSource dataSource, ServiceProviderConnectionFactoryLocator connectionFactoryLocator, LocalUserIdLocator localUserIdLocator,  TextEncryptor textEncryptor) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
		this.connectionFactoryLocator = connectionFactoryLocator;
		this.localUserIdLocator = localUserIdLocator;
		this.textEncryptor = textEncryptor;
	}
	
	public List<ServiceProviderConnection<?>> findConnections() {
		return jdbcTemplate.query(SELECT_FROM_SERVICE_PROVIDER_CONNECTION + " where localUserId = ? order by providerId, rank", connectionMapper, getLocalUserId());
	}

	public List<ServiceProviderConnection<?>> findConnectionsToProvider(String providerId) {
		return jdbcTemplate.query(SELECT_FROM_SERVICE_PROVIDER_CONNECTION + " where localUserId = ? and providerId = ? order by rank", connectionMapper, getLocalUserId(), providerId);
	}

	public List<ServiceProviderConnection<?>> findConnectionsForUsers(Map<String, List<String>> providerUsers) {
		if (providerUsers.isEmpty()) {
			throw new IllegalArgumentException("Unable to execute find: no providerUsers provided");
		}
		StringBuilder providerUsersCriteriaSql = new StringBuilder();
		List<Object> args = new ArrayList<Object>(1 + providerUsers.size() * 2);
		args.add(getLocalUserId());
		for (Iterator<Entry<String, List<String>>> it = providerUsers.entrySet().iterator(); it.hasNext();) {
			Entry<String, List<String>> entry = it.next();
			providerUsersCriteriaSql.append("providerId = ? and providerUserId in (?)");
			args.add(entry.getKey());
			args.add(entry.getValue());
			if (it.hasNext()) {
				providerUsersCriteriaSql.append(" or " );
			}
		}
		return jdbcTemplate.query(SELECT_FROM_SERVICE_PROVIDER_CONNECTION + " where localUserId = ? and " + providerUsersCriteriaSql, args.toArray(), connectionMapper);
	}

	public ServiceProviderConnection<?> findConnectionByKey(ServiceProviderConnectionKey connectionKey) {
		return jdbcTemplate.queryForObject(SELECT_FROM_SERVICE_PROVIDER_CONNECTION + " where localUserId = ? and providerId = ? and providerUserId = ? order by rank", connectionMapper, getLocalUserId(), connectionKey.getProviderId(), connectionKey.getProviderUserId());
	}

	@SuppressWarnings("unchecked")
	public <S> ServiceProviderConnection<S> findConnectionByServiceApi(Class<S> serviceApiType) {
		return (ServiceProviderConnection<S>) jdbcTemplate.queryForObject(SELECT_FROM_SERVICE_PROVIDER_CONNECTION + " where localUserId = ? and providerId = ? and rank = 1", connectionMapper,
				getLocalUserId(), getProviderId(serviceApiType));
	}

	@SuppressWarnings("unchecked")
	public <S> ServiceProviderConnection<S> findConnectionByServiceApiForUser(Class<S> serviceApiType, String providerUserId) {
		return (ServiceProviderConnection<S>) jdbcTemplate.queryForObject(SELECT_FROM_SERVICE_PROVIDER_CONNECTION + " where localUserId = ? and providerId = ? and providerUserId = ?", connectionMapper,
				getLocalUserId(), getProviderId(serviceApiType), providerUserId);
	}

	public void addConnection(ServiceProviderConnection<?> connection) {
		ServiceProviderConnectionMemento memento = connection.createMemento();
		Serializable localUserId = getLocalUserId();
		jdbcTemplate.update("insert into ServiceProviderConnection (localUserId, providerId, providerUserId, rank, profileName, profileUrl, profilePictureUrl, accessToken, secret, refreshToken, expireTime) values (?, ?, ?, (select ifnull(max(rank) + 1, 1) from ServiceProviderConnection where localUserId = ? and providerId = ?), ?, ?, ?, ?, ?, ?, ?)",
				localUserId, memento.getProviderId(), memento.getProviderUserId(), localUserId, memento.getProviderId(),
				memento.getProfileName(), memento.getProfileUrl(), memento.getProfilePictureUrl(),
				encrypt(memento.getAccessToken()), encrypt(memento.getSecret()), encrypt(memento.getRefreshToken()), memento.getExpireTime());
	}

	public void removeConnectionsToProvider(String providerId) {
		jdbcTemplate.update("delete from ServiceProviderConnection where localUserId = ? and providerId = ?", getLocalUserId(), providerId);
	}

	public void removeConnectionWithKey(ServiceProviderConnectionKey connectionKey) {
		jdbcTemplate.update("delete from ServiceProviderConnection where localUserId = ? and providerId = ? and providerUserId = ?", getLocalUserId(), connectionKey.getProviderId(), connectionKey.getProviderUserId());		
	}

	// internal helpers
	
	private final static String SELECT_FROM_SERVICE_PROVIDER_CONNECTION = "select localUserId, providerId, providerUserId, profileName, profileUrl, profilePictureUrl, accessToken, secret, refreshToken, expireTime from ServiceProviderConnection";
	
	private Serializable getLocalUserId() {
		return localUserIdLocator.getLocalUserId();
	}
	
	private final ServiceProviderConnectionMapper connectionMapper = new ServiceProviderConnectionMapper();
	
	private final class ServiceProviderConnectionMapper implements RowMapper<ServiceProviderConnection<?>> {
		
		public ServiceProviderConnection<?> mapRow(ResultSet rs, int rowNum) throws SQLException {
			ServiceProviderConnectionMemento connectionMemento = mapConnectionMemento(rs);
			ServiceProviderConnectionFactory<?> connectionFactory = connectionFactoryLocator.getConnectionFactory(connectionMemento.getProviderId());
			return connectionFactory.createConnection(connectionMemento);
		}
		
		private ServiceProviderConnectionMemento mapConnectionMemento(ResultSet rs) throws SQLException {
			return new ServiceProviderConnectionMemento(rs.getString("providerId"), rs.getString("providerUserId"),
					rs.getString("profileName"), rs.getString("profileUrl"), rs.getString("profilePictureUrl"),
					decrypt(rs.getString("accessToken")), decrypt(rs.getString("secret")), decrypt(rs.getString("refreshToken")), rs.getLong("expireTime"));
		}
		
		private String decrypt(String encryptedText) {
			return encryptedText != null ? textEncryptor.decrypt(encryptedText) : encryptedText;
		}
		
	}

	private <S> String getProviderId(Class<S> serviceApiType) {
		return connectionFactoryLocator.getConnectionFactory(serviceApiType).getProviderId();
	}
	
	private String encrypt(String text) {
		return text != null ? textEncryptor.encrypt(text) : text;
	}

}