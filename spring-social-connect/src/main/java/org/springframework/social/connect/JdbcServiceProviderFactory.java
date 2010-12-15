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

import java.lang.reflect.Constructor;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.beans.BeanUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.security.encrypt.StringEncryptor;
import org.springframework.stereotype.Repository;
import org.springframework.util.ClassUtils;

/**
 * Loads ServiceProvider records from a relational database using the JDBC API.
 * @author Keith Donald
 */
@Repository
public class JdbcServiceProviderFactory implements ServiceProviderFactory {
	
	private final JdbcTemplate jdbcTemplate;

	private final StringEncryptor encryptor;
	
	private final JdbcAccountConnectionRepository connectionRepository;

	private final AccountIdResolver accountIdResolver;
	
	private String serviceProviderQuery;

	public JdbcServiceProviderFactory(JdbcTemplate jdbcTemplate, StringEncryptor encryptor,
			AccountIdResolver accountIdResolver) {
		this.jdbcTemplate = jdbcTemplate;
		this.encryptor = encryptor;
		this.accountIdResolver = accountIdResolver;
		this.connectionRepository = new JdbcAccountConnectionRepository(jdbcTemplate, encryptor);
		this.connectionRepository.setAccessTokenQuery(JdbcAccountConnectionRepository.DEFAULT_ACCESS_TOKEN_QUERY);
		this.connectionRepository.setConnectionExistsQuery(JdbcAccountConnectionRepository.DEFAULT_CONNECTION_EXISTS_QUERY);
		this.connectionRepository.setCreateConnectionQuery(JdbcAccountConnectionRepository.DEFAULT_CREATE_CONNECTION_QUERY);
		this.connectionRepository.setProviderAccountIdQuery(JdbcAccountConnectionRepository.DEFAULT_PROVIDER_ACCOUNT_ID_QUERY);
		this.connectionRepository.setRemoveConnectionQuery(JdbcAccountConnectionRepository.DEFAULT_REMOVE_CONNECTION_QUERY);
		this.connectionRepository
				.setRemoveAllConnectionsQuery(JdbcAccountConnectionRepository.DEFAULT_REMOVE_ALL_CONNECTIONS_QUERY);
		this.connectionRepository
				.setAccountConnectionsQuery(JdbcAccountConnectionRepository.DEFAULT_ACCOUNT_CONNECTIONS_QUERY);
		this.serviceProviderQuery = DEFAULT_SERVICE_PROVIDER_QUERY;
	}

	public String getProviderAccountIdQuery() {
		return connectionRepository.getProviderAccountIdByMemberAndProviderQuery();
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
	 * @param providerAccountIdByMemberAndProviderQuery
	 */
	public void setProviderAccountIdQuery(String providerAccountIdQuery) {
		connectionRepository.setProviderAccountIdQuery(providerAccountIdQuery);
	}

	public String getConnectionExistsQuery() {
		return connectionRepository.getConnectionExistsQuery();
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
	 * @param providerAccountIdByMemberAndProviderQuery
	 */
	public void setConnectionExistsQuery(String connectionExistsQuery) {
		connectionRepository.setConnectionExistsQuery(connectionExistsQuery);
	}

	public String getCreateConnectionQuery() {
		return connectionRepository.getCreateConnectionQuery();
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
	 * @param providerAccountIdByMemberAndProviderQuery
	 */
	public void setCreateConnectionQuery(String createConnectionQuery) {
		connectionRepository.setCreateConnectionQuery(createConnectionQuery);
	}

	public String getRemoveConnectionQuery() {
		return connectionRepository.getRemoveConnectionQuery();
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
	 * @param providerAccountIdByMemberAndProviderQuery
	 */
	public void setRemoveConnectionQuery(String removeConnectionQuery) {
		connectionRepository.setRemoveConnectionQuery(removeConnectionQuery);
	}

	public String getAccessTokenQuery() {
		return connectionRepository.getAccessTokenQuery();
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
	 * @param providerAccountIdByMemberAndProviderQuery
	 */
	public void setAccessTokenQuery(String accessTokenQuery) {
		connectionRepository.setAccessTokenQuery(accessTokenQuery);
	}

	public String getRemoveAllConnectionsQuery() {
		return connectionRepository.getRemoveAllConnectionsQuery();
	}

	/**
	 * <p>
	 * Overrides the default query for deleting all connections for a provider.
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
	public void setRemoveAllConnectionsQuery(String removeAllConnectionsQuery) {
		connectionRepository.setRemoveAllConnectionsQuery(removeAllConnectionsQuery);
	}

	public String getAccountConnectionsQuery() {
		return connectionRepository.getAccountConnectionsQuery();
	}

	/**
	 * <p>
	 * Overrides the default query for selecting all account connections for a
	 * provider.
	 * </p>
	 * 
	 * <p>
	 * The default query is:
	 * </p>
	 * 
	 * <code>
	 * select member, provider, accessToken, secret, accountId, profileUrl from AccountConnection where member = ? and provider = ?
	 * </code>
	 * 
	 * <p>
	 * An overriding query should follow a similar form, taking a local member
	 * ID and a provider ID and returning the connection details, including the
	 * member ID, provider ID, access token, access token secret, provider
	 * account ID, and provider profile URL.
	 * 
	 * @param accessTokenQuery
	 */
	public void setAccountConnectionsQuery(String accountConnectionsQuery) {
		connectionRepository.setAccountConnectionsQuery(accountConnectionsQuery);
	}
	public ServiceProvider<?> getServiceProvider(String name) {
		return jdbcTemplate.queryForObject(serviceProviderQuery, new RowMapper<ServiceProvider<?>>() {
			public ServiceProvider<?> mapRow(ResultSet rs, int rowNum) throws SQLException {
				ServiceProviderParameters parameters = parametersMapper.mapRow(rs, rowNum);
				Class<? extends ServiceProvider<?>> implementation = getImplementationClass(rs.getString(3));
				Constructor<? extends ServiceProvider<?>> constructor = ClassUtils.getConstructorIfAvailable(
						implementation, ServiceProviderParameters.class, AccountConnectionRepository.class,
						AccountIdResolver.class);
				return BeanUtils.instantiateClass(constructor, parameters, connectionRepository, accountIdResolver);
			}
		}, name);
	}

	@SuppressWarnings("unchecked")
	public <S> ServiceProvider<S> getServiceProvider(String name, Class<S> serviceType) {
		ServiceProvider<?> provider = getServiceProvider(name);
		return (ServiceProvider<S>) provider;
	}

	/**
	 * <p>
	 * Overrides the default query for selecting a service provider
	 * </p>
	 * 
	 * <p>
	 * The default query is:
	 * </p>
	 * 
	 * <code>
	 * select name, displayName, implementation, apiKey, secret, appId, requestTokenUrl, authorizeUrl, accessTokenUrl from ServiceProvider where name = ?
	 * </code>
	 * 
	 * <p>
	 * An overriding query should follow a similar form, taking a name as a
	 * parameter and returning the name, displayName, fully-qualified classname
	 * of the provider implementation, API key, API secret, application ID,
	 * request token URL, authorization URL, and access token URL for the
	 * provider.
	 * 
	 * @param serviceProviderQuery
	 */
	public void setServiceProviderQuery(String serviceProviderQuery) {
		this.serviceProviderQuery = serviceProviderQuery;
	}

	public String getServiceProviderQuery() {
		return serviceProviderQuery;
	}

	// internal helpers
	
	@SuppressWarnings("unchecked")
	private Class<? extends ServiceProvider<?>> getImplementationClass(String implementation) {
		try {
			Class<?> clazz = ClassUtils.forName(implementation, JdbcServiceProviderFactory.class.getClassLoader());
			if (!ServiceProvider.class.isAssignableFrom(clazz)) {
				throw new IllegalStateException("Implementation '" + implementation + "' does not implement the ServiceProvider interface");
			}
			return (Class<? extends ServiceProvider<?>>)clazz; 
		} catch (ClassNotFoundException e) {
			throw new IllegalStateException("The ServiceProvider implementation was not found in the classpath", e);
		}
	}
	
	private RowMapper<ServiceProviderParameters> parametersMapper = new RowMapper<ServiceProviderParameters>() {
		public ServiceProviderParameters mapRow(ResultSet rs, int rowNum) throws SQLException {
			return new ServiceProviderParameters(rs.getString(1), rs.getString(2), encryptor.decrypt(rs.getString(4)),
					encryptor.decrypt(rs.getString(5)), rs.getLong(6), rs.getString(7), rs.getString(8),
					rs.getString(9));
		}
	};

	private static final String DEFAULT_SERVICE_PROVIDER_QUERY = "select name, displayName, implementation, apiKey, secret, appId, requestTokenUrl, authorizeUrl, accessTokenUrl from ServiceProvider where name = ?";
}