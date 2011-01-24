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
package org.springframework.social.provider.support;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.springframework.social.provider.AuthorizationProtocol;
import org.springframework.social.provider.ServiceProvider;
import org.springframework.social.provider.ServiceProviderConnection;

/**
 * General-purpose base class for ServiceProvider implementations.
 * Assumes provider connections are persisted in a {@link ConnectionRepository}.
 * Subclasses must implement {@link #getAuthorizationProtocol()} and {@link #getApi(Connection)}.
 * @author Keith Donald
 * @param <S> The service API exposed by this service provider.
 */
public abstract class AbstractServiceProvider<S> implements ServiceProvider<S> {

	private final String id;
	
	private final ConnectionRepository connectionRepository;
	
	/**
	 * Creates a service provider.
	 * @param id the id of the provider as it is identified in the connection repository.
	 * @param displayName a display name for the provider, suitable for display on a user interface.
	 * @param connectionRepository the store for service provider connections
	 */
	public AbstractServiceProvider(String id, ConnectionRepository connectionRepository) {
		this.id = id;
		this.connectionRepository = connectionRepository;
	}
	
	public boolean isConnected(Serializable accountId) {
		return connectionRepository.isConnected(accountId, id);
	}

	public List<ServiceProviderConnection<S>> getConnections(Serializable accountId) {
		List<Connection> connections = connectionRepository.findConnections(accountId, id);
		List<ServiceProviderConnection<S>> serviceProviderConnections = new ArrayList<ServiceProviderConnection<S>>(connections.size());
		for (Connection connection : connections) {
			serviceProviderConnections.add(createConnection(accountId, connection));
		}
		return serviceProviderConnections;
	}

	// subclassing hooks

	public abstract AuthorizationProtocol getAuthorizationProtocol();

	/**
	 * Construct the ServiceProvider's API to be invoked by the client application on behalf of a user.
	 * @param connection the user connection details
	 * @return the service API
	 */
	protected abstract S getApi(Connection connection);

	/**
	 * Hook method for creating a persisted {@link ServiceProviderConnection} from a Connection record.
	 * Designed for use by subclasses in their authorization-protocol-specific connection operations.
	 */
	protected ServiceProviderConnection<S> connect(Serializable accountId, Connection connection) {
		return createConnection(accountId, connectionRepository.saveConnection(accountId, id, connection));
	}
	
	// internal helpers
	
	private ServiceProviderConnection<S> createConnection(Serializable accountId, Connection connection) {
		return new ServiceProviderConnectionImpl<S>(connection.getId(), getApi(connection), accountId, id, connectionRepository);
	}
	
}