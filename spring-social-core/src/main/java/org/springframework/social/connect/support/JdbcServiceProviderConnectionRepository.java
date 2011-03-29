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
package org.springframework.social.connect.support;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.social.connect.ServiceProviderConnection;
import org.springframework.social.connect.ServiceProviderConnectionFactory;
import org.springframework.social.connect.ServiceProviderConnectionRepository;

public class JdbcServiceProviderConnectionRepository implements ServiceProviderConnectionRepository {

	private final JdbcTemplate jdbcTemplate;
	
	private final TextEncryptor textEncryptor;

	private final ServiceProviderConnectionFactory connectionFactory;
	
	public JdbcServiceProviderConnectionRepository(JdbcTemplate jdbcTemplate, TextEncryptor textEncryptor, ServiceProviderConnectionFactory connectionFactory) {
		this.jdbcTemplate = jdbcTemplate;
		this.textEncryptor = textEncryptor;
		this.connectionFactory = connectionFactory;
	}

	public Map<String, List<ServiceProviderConnection<?>>> findConnections(Serializable accountId) {
		List<ServiceProviderConnection<?>> connections = jdbcTemplate.query(SELECT_FROM_SERVICE_PROVIDER_CONNECTION + " where accountId = ? by providerId, id", connectionMapper, accountId);
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

	public List<ServiceProviderConnection<?>> findConnectionsById(List<Long> connectionIds) {
		return jdbcTemplate.query(SELECT_FROM_SERVICE_PROVIDER_CONNECTION + " where id in (?) order by providerId, id", connectionMapper, connectionIds);
	}

	public ServiceProviderConnection<?> findConnectionById(Long connectionId) {
		return jdbcTemplate.queryForObject(SELECT_FROM_SERVICE_PROVIDER_CONNECTION + " where id = ?", connectionMapper, connectionId);
	}

	public List<ServiceProviderConnection<?>> findConnectionsToProviderAccount(String providerId, String providerAccountId) {
		return jdbcTemplate.query(SELECT_FROM_SERVICE_PROVIDER_CONNECTION + " where providerId = ? and providerAccountId = ?", connectionMapper, providerId, providerAccountId);
	}

	public ServiceProviderConnection<?> saveConnection(Serializable accountId, ServiceProviderConnection<?> connection) {
		// TODO
		return null;
	}

	public void removeConnections(Serializable accountId, String providerId) {
		jdbcTemplate.update("delete from ServiceProviderConnection where accountId = ? and providerId = ?");
	}

	public void removeConnection(Long connectionId) {
		jdbcTemplate.update("delete from ServiceProviderConnection where id = ?");
	}

	private final static String SELECT_FROM_SERVICE_PROVIDER_CONNECTION = "select accountId, providerId, id, providerAccountId, profileName, profileUrl, profilePictureUrl, allowSignIn, accessToken, secret, refreshToken from ServiceProviderConnection";
		
	private final ServiceProviderConnectionRowMapper connectionMapper = new ServiceProviderConnectionRowMapper();
	
	private class ServiceProviderConnectionRowMapper implements RowMapper<ServiceProviderConnection<?>> {
		
		public ServiceProviderConnection<?> mapRow(ResultSet rs, int rowNum) throws SQLException {
			return connectionFactory.createConnection(connectionMemento(rs));
		}
		
		private ServiceProviderConnectionMemento connectionMemento(ResultSet rs) throws SQLException {
			return new ServiceProviderConnectionMemento(rs.getLong("id"), (Serializable) rs.getObject("accountId"), rs.getString("providerId"), rs.getString("providerAccountId"),
					rs.getString("profileName"), rs.getString("profileUrl"), rs.getString("profilePictureUrl"),
					rs.getBoolean("allowSignin"),
					decrypt(rs.getString("accessToken")), decrypt(rs.getString("secret")), decrypt(rs.getString("refreshToken")));							
		}
		
		private String decrypt(String encryptedText) {
			return textEncryptor.decrypt(encryptedText);
		}
		
	}
}