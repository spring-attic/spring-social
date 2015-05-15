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
package org.springframework.social.connect.support;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.core.GenericTypeResolver;
import org.springframework.social.connect.ConnectionFactory;
import org.springframework.social.connect.ConnectionFactoryLocator;

/**
 * A registry for {@link ConnectionFactory service provider connection factories}.
 * Implements {@link ConnectionFactoryLocator} for locating registered factory instances.
 * Call {@link #addConnectionFactory(ConnectionFactory)} to add to this registry.
 * @author Keith Donald
 */
public class ConnectionFactoryRegistry implements ConnectionFactoryLocator {

	private final Map<String, ConnectionFactory<?>> connectionFactories = new HashMap<String, ConnectionFactory<?>>();

	private final Map<Class<?>, String> apiTypeIndex = new HashMap<Class<?>, String>();
	
	/**
	 * Add a {@link ConnectionFactory} to this registry.
	 * @param connectionFactory the connection factory
	 */
	public void addConnectionFactory(ConnectionFactory<?> connectionFactory) {
		if (connectionFactories.containsKey(connectionFactory.getProviderId())) {
			throw new IllegalArgumentException("A ConnectionFactory for provider '" + connectionFactory.getProviderId() + "' has already been registered");
		}
		Class<?> apiType = GenericTypeResolver.resolveTypeArgument(connectionFactory.getClass(), ConnectionFactory.class);
		if (apiTypeIndex.containsKey(apiType)) {
			throw new IllegalArgumentException("A ConnectionFactory for API [" + apiType.getName() + "] has already been registered");
		}
		connectionFactories.put(connectionFactory.getProviderId(), connectionFactory);
		apiTypeIndex.put(apiType, connectionFactory.getProviderId());
	}

	/**
	 * Set the group of service provider connection factories registered in this registry.
	 * JavaBean setter that allows for this object to be more easily configured by tools.
	 * For programmatic configuration, prefer {@link #addConnectionFactory(ConnectionFactory)}.
	 * @param connectionFactories the set of connection factories to register
	 */
	public void setConnectionFactories(List<ConnectionFactory<?>> connectionFactories) {
		for (ConnectionFactory<?> connectionFactory : connectionFactories) {
			addConnectionFactory(connectionFactory);
		}
	}

	// implementing ConnectionFactoryLocator
	
	public ConnectionFactory<?> getConnectionFactory(String providerId) {
		ConnectionFactory<?> connectionFactory = connectionFactories.get(providerId);
		if (connectionFactory == null) {
			throw new IllegalArgumentException("No connection factory for service provider '" + providerId + "' is registered");
		}
		return connectionFactory;
	}

	@SuppressWarnings("unchecked")
	public <A> ConnectionFactory<A> getConnectionFactory(Class<A> apiType) {
		String providerId = apiTypeIndex.get(apiType);
		if (providerId == null) {
			throw new IllegalArgumentException("No connection factory for API [" + apiType.getName() + "] is registered");
		}
		return (ConnectionFactory<A>) getConnectionFactory(providerId);
	}

	public Set<String> registeredProviderIds() {
		return connectionFactories.keySet();
	}
	
}
