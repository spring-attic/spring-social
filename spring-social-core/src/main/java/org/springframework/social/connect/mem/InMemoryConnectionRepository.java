/*
 * Copyright 2015 the original author or authors.
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
package org.springframework.social.connect.mem;

import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;

import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionFactoryLocator;
import org.springframework.social.connect.ConnectionKey;
import org.springframework.social.connect.ConnectionRepository;
import org.springframework.social.connect.DuplicateConnectionException;
import org.springframework.social.connect.NoSuchConnectionException;
import org.springframework.social.connect.NotConnectedException;
import org.springframework.util.Assert;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

public class InMemoryConnectionRepository implements ConnectionRepository {

	// <providerId, Connection<provider API>>
	private MultiValueMap<String, Connection<?>> connections;
		
	private ConnectionFactoryLocator connectionFactoryLocator;

	public InMemoryConnectionRepository(ConnectionFactoryLocator connectionFactoryLocator) {
		this.connectionFactoryLocator = connectionFactoryLocator;
		this.connections = new LinkedMultiValueMap<String, Connection<?>>();
	}
	
	public MultiValueMap<String, Connection<?>> findAllConnections() {
		return connections;
	}

	public List<Connection<?>> findConnections(String providerId) {
		List<Connection<?>> emptyConnectionList = Collections.emptyList();
		return connections.containsKey(providerId) ? connections.get(providerId) : emptyConnectionList;
	}

	@SuppressWarnings("unchecked")
	public <A> List<Connection<A>> findConnections(Class<A> apiType) {
		List<?> providerConnections = findConnections(getProviderId(apiType));
		return (List<Connection<A>>) providerConnections;
	}

	public MultiValueMap<String, Connection<?>> findConnectionsToUsers(MultiValueMap<String, String> providerUserIds) {
		Assert.notEmpty(providerUserIds);
		MultiValueMap<String, Connection<?>> connectionsToUsers = new LinkedMultiValueMap<String, Connection<?>>(providerUserIds.size());
		for (Entry<String, List<String>> providerConnectionEntry : providerUserIds.entrySet()) {
			String providerId = providerConnectionEntry.getKey();
			List<String> userIds = providerConnectionEntry.getValue();
			if (connections.containsKey(providerId)) {
				List<Connection<?>> providerConnections = connections.get(providerId);
				
				for (Connection<?> connection : providerConnections) {
					if (userIds.contains(connection.getKey().getProviderUserId())) {
						connectionsToUsers.add(providerId, connection);
					}
				}
			}
		}
		return connectionsToUsers;
	}

	public Connection<?> getConnection(ConnectionKey connectionKey) {
		if (connections.containsKey(connectionKey.getProviderId())) {
			List<Connection<?>> providerConnections = connections.get(connectionKey.getProviderId());
			for (Connection<?> connection : providerConnections) {
				if (connection.getKey().equals(connectionKey)) {
					return connection;
				}
			}
		}
		throw new NoSuchConnectionException(connectionKey);
	}

	@SuppressWarnings("unchecked")
	public <A> Connection<A> getConnection(Class<A> apiType, String providerUserId) {
		return (Connection<A>) getConnection(new ConnectionKey(getProviderId(apiType), providerUserId));
	}

	public <A> Connection<A> getPrimaryConnection(Class<A> apiType) {
		Connection<A> primaryConnection = findPrimaryConnection(apiType);
		if (primaryConnection == null) {
			throw new NotConnectedException(getProviderId(apiType));
		}
		return primaryConnection;
	}

	@SuppressWarnings("unchecked")
	public <A> Connection<A> findPrimaryConnection(Class<A> apiType) {
		String providerId = getProviderId(apiType);
		if (connections.containsKey(providerId)) {
			return (Connection<A>) connections.get(providerId).get(0);
		}
		return null;
	}

	public void addConnection(Connection<?> connection) {
		try {
			ConnectionKey connectionKey = connection.getKey();
			getConnection(connectionKey);
			throw new DuplicateConnectionException(connectionKey);
		} catch (NoSuchConnectionException e) {
			connections.add(connection.createData().getProviderId(), connection);
		}
	}

	public void updateConnection(Connection<?> connection) {
		connections.add(connection.createData().getProviderId(), connection);
	}

	public void removeConnections(String providerId) {
		connections.remove(providerId);
	}

	public void removeConnection(ConnectionKey connectionKey) {
		String providerId = connectionKey.getProviderId();
		if (connections.containsKey(providerId)) {
			List<Connection<?>> providerConnections = connections.get(providerId);
			for (Connection<?> connection : providerConnections) {
				if (connection.getKey().equals(connectionKey)) {
					providerConnections.remove(connection);
				}
			}
		}
	}

	private <A> String getProviderId(Class<A> apiType) {
		return connectionFactoryLocator.getConnectionFactory(apiType).getProviderId();
	}

}
