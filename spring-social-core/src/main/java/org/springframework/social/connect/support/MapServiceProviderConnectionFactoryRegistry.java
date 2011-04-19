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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.core.GenericTypeResolver;
import org.springframework.social.connect.ServiceProviderConnectionFactory;
import org.springframework.social.connect.ServiceProviderConnectionFactoryLocator;

/**
 * A Map-based registry for {@link ServiceProviderConnectionFactory service provider connection factories}.
 * Implements {@link ServiceProviderConnectionFactoryLocator} for locating registered factory instances.
 * @author Keith Donald
 */
public class MapServiceProviderConnectionFactoryRegistry implements ServiceProviderConnectionFactoryLocator {

	private final Map<String, ServiceProviderConnectionFactory<?>> connectionFactories = new HashMap<String, ServiceProviderConnectionFactory<?>>();

	private final Map<Class<?>, String> serviceApiTypeIndex = new HashMap<Class<?>, String>();

	/**
	 * Set the group of service provider connection factories registered in this registry.
	 * JavaBean setter that allows for this object to be more easily configured by tools.
	 * For programmatic configuration, prefer {@link #addConnectionFactory(ServiceProviderConnectionFactory)}.
	 * @param connectionFactories the set of connection factories to register
	 */
	public void setConnectionFactories(List<ServiceProviderConnectionFactory<?>> connectionFactories) {
		for (ServiceProviderConnectionFactory<?> connectionFactory : connectionFactories) {
			addConnectionFactory(connectionFactory);
		}
	}
	
	/**
	 * Add a {@link ServiceProviderConnectionFactory} to this registry.
	 * @param connectionFactory the connection factory
	 */
	public void addConnectionFactory(ServiceProviderConnectionFactory<?> connectionFactory) {
		if (connectionFactories.containsKey(connectionFactory.getProviderId())) {
			throw new IllegalArgumentException("A ConnectionFactory for provider '" + connectionFactory.getProviderId() + "' has already been registered");
		}
		Class<?> serviceApiType = GenericTypeResolver.resolveTypeArgument(connectionFactory.getClass(), ServiceProviderConnectionFactory.class);
		if (serviceApiTypeIndex.containsKey(serviceApiType)) {
			throw new IllegalArgumentException("A ConnectionFactory for service API [" + serviceApiType.getName() + "] has already been registered");
		}
		connectionFactories.put(connectionFactory.getProviderId(), connectionFactory);
		serviceApiTypeIndex.put(serviceApiType, connectionFactory.getProviderId());
	}

	// implementing ServiceProviderConnectionFactoryLocator
	
	public ServiceProviderConnectionFactory<?> getConnectionFactory(String providerId) {
		ServiceProviderConnectionFactory<?> connectionFactory = connectionFactories.get(providerId);
		if (connectionFactory == null) {
			throw new IllegalArgumentException("No connection factory for ServiceProvider '" + providerId + "' is registered");
		}
		return connectionFactory;
	}

	@SuppressWarnings("unchecked")
	public <S> ServiceProviderConnectionFactory<S> getConnectionFactory(Class<S> serviceApiType) {
		String providerId = serviceApiTypeIndex.get(serviceApiType);
		if (providerId == null) {
			throw new IllegalArgumentException("No connection factory for ServiceProvider API [" + serviceApiType.getName() + "] is registered");
		}
		return (ServiceProviderConnectionFactory<S>) getConnectionFactory(providerId);
	}

	public Set<String> registeredProviderIds() {
		return connectionFactories.keySet();
	}
	
}