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
package org.springframework.social.connect;

import java.util.HashMap;
import java.util.Map;

import org.springframework.core.GenericTypeResolver;

public class MapServiceProviderConnectionFactoryLocator implements ServiceProviderConnectionFactoryLocator {

	private final Map<String, ServiceProviderConnectionFactory<?>> connectionFactories = new HashMap<String, ServiceProviderConnectionFactory<?>>();

	private final Map<Class<?>, String> serviceApiTypeIndex = new HashMap<Class<?>, String>();

	public void addConnectionFactory(ServiceProviderConnectionFactory<?> connectionFactory) {
		connectionFactories.put(connectionFactory.getProviderId(), connectionFactory);
		Class<?> serviceApiType = GenericTypeResolver.resolveTypeArgument(connectionFactory.getClass(), ServiceProviderConnectionFactory.class);
		serviceApiTypeIndex.put(serviceApiType, connectionFactory.getProviderId());
	}
	
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
	

}
