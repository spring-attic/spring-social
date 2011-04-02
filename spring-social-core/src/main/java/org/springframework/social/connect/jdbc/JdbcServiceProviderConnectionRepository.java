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
import java.util.List;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.social.connect.ServiceProviderConnection;
import org.springframework.social.connect.ServiceProviderConnectionFactoryLocator;
import org.springframework.social.connect.ServiceProviderConnectionKey;
import org.springframework.social.connect.ServiceProviderConnectionRecord;
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
		return jdbcTemplate.query(SELECT_FROM_SERVICE_PROVIDER_CONNECTION + " where localUserId = ? order by providerId, id", connectionMapper, getLocalUserId());
	}

	public List<ServiceProviderConnection<?>> findConnectionsToProvider(String providerId) {
		return null;
	}

	public List<ServiceProviderConnection<?>> findConnectionsByKeys(List<ServiceProviderConnectionKey> connectionKeys) {
		return null;
	}

	public ServiceProviderConnection<?> findConnectionByKey(ServiceProviderConnectionKey connectionKey) {
		return null;
	}

	public <S> ServiceProviderConnection<S> findConnectionByServiceApi(Class<S> serviceApiType) {
		return null;
	}

	public <S> ServiceProviderConnection<S> findConnectionByServiceApiForUser(Class<S> serviceApiType, String providerUserId) {
		return null;
	}

	public <S> ServiceProviderConnection<S> saveConnection(ServiceProviderConnection<S> connection) {
		return null;
	}

	public void removeConnectionsToProvider(String providerId) {
		
	}

	public void removeConnectionWithKey(ServiceProviderConnectionKey connectionKey) {
		
	}

	// internal helpers
	
	private final static String SELECT_FROM_SERVICE_PROVIDER_CONNECTION = "select localUserId, providerId, id, providerUserId, profileName, profileUrl, profilePictureUrl, allowSignIn, accessToken, secret, refreshToken from ServiceProviderConnection";
	
	private Serializable getLocalUserId() {
		return localUserIdLocator.getLocalUserId();
	}
	
	private final ServiceProviderConnectionMapper connectionMapper = new ServiceProviderConnectionMapper();
	
	private final class ServiceProviderConnectionMapper implements RowMapper<ServiceProviderConnection<?>> {
		
		public ServiceProviderConnection<?> mapRow(ResultSet rs, int rowNum) throws SQLException {
			ServiceProviderConnectionRecord connectionRecord = mapConnectionRecord(rs);
			return connectionFactoryLocator.getConnectionFactory(connectionRecord.getProviderId()).createConnection(connectionRecord);
		}
		
		private ServiceProviderConnectionRecord mapConnectionRecord(ResultSet rs) throws SQLException {
			return new ServiceProviderConnectionRecord(rs.getString("providerId"), rs.getString("providerUserId"),
					rs.getString("profileName"), rs.getString("profileUrl"), rs.getString("profilePictureUrl"), rs.getBoolean("allowSignin"),
					decrypt(rs.getString("accessToken")), decrypt(rs.getString("secret")), decrypt(rs.getString("refreshToken")), rs.getLong("expireTime"));
		}
		
		private String decrypt(String encryptedText) {
			return encryptedText != null ? textEncryptor.decrypt(encryptedText) : encryptedText;
		}
		
	}
	
}