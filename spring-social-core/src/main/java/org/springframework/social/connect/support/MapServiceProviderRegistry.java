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
import java.util.Map;

import org.springframework.social.ServiceProvider;
import org.springframework.social.connect.ServiceProviderRegistry;

public class MapServiceProviderRegistry implements ServiceProviderRegistry {

	private Map<String, ServiceProvider<?>> serviceProviders = new HashMap<String, ServiceProvider<?>>();

	private Map<Class<?>, ServiceProvider<?>> providerClassIndex = new HashMap<Class<?>, ServiceProvider<?>>();
	
	public void addServiceProvider(String providerId, ServiceProvider<?> serviceProvider) {
		if (serviceProviders.containsKey(providerId)) {
			throw new IllegalArgumentException("A ServiceProvider with id '" + providerId + "' is already registered");
		}
		if (providerClassIndex.containsKey(serviceProvider.getClass())) {
			throw new IllegalArgumentException("A ServiceProvider of class '" + providerClassIndex + "' is already registered");
		}
		serviceProviders.put(providerId, serviceProvider);
		providerClassIndex.put(serviceProvider.getClass(), serviceProvider);
	}
	
	public ServiceProvider<?> getServiceProvider(String providerId) {
		ServiceProvider<?> provider = serviceProviders.get(providerId);
		if (provider == null) {
			throw new IllegalArgumentException("No ServiceProvider with id '" + providerId + "' is registered");
		}		
		return provider;
	}

	@SuppressWarnings("unchecked")
	public <P extends ServiceProvider<S>, S> P getServiceProvider(String providerId, Class<P> providerType) {
		ServiceProvider<?> provider = getServiceProvider(providerId, providerType);
		if (!providerType.isAssignableFrom(provider.getClass())) {
			throw new IllegalArgumentException("ServiceProvider '" + providerId + "' not instance of [" + providerType.getName() + "]");
		}
		return (P) provider;
	}

	@SuppressWarnings("unchecked")
	public <P extends ServiceProvider<S>, S> P getServiceProvider(Class<P> providerClass) {
		ServiceProvider<?> provider = providerClassIndex.get(providerClass);
		if (provider == null) {
			throw new IllegalArgumentException("No ServiceProvider of class [" + providerClass.getName() + "] is registered");
		}
		return (P) provider;
	}

}