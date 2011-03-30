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

import org.springframework.core.GenericTypeResolver;
import org.springframework.social.ServiceProvider;
import org.springframework.social.connect.ServiceProviderRegistry;

public class MapServiceProviderRegistry implements ServiceProviderRegistry {

	private Map<String, ServiceProvider<?>> serviceProviders = new HashMap<String, ServiceProvider<?>>();

	private Map<Class<?>, String> providerClassIndex = new HashMap<Class<?>, String>();

	private Map<Class<?>, String> providerApiTypeIndex = new HashMap<Class<?>, String>();

	public void addServiceProvider(String providerId, ServiceProvider<?> serviceProvider) {
		if (serviceProviders.containsKey(providerId)) {
			throw new IllegalArgumentException("A ServiceProvider with id '" + providerId + "' is already registered");
		}
		if (providerClassIndex.containsKey(serviceProvider.getClass())) {
			throw new IllegalArgumentException("A ServiceProvider of class '" + providerClassIndex + "' is already registered");
		}
		Class<?> serviceApiType = GenericTypeResolver.resolveTypeArgument(serviceProvider.getClass(), ServiceProvider.class);
		if (providerApiTypeIndex.containsKey(serviceApiType)) {
			throw new IllegalArgumentException("A ServiceProvider for API type '" + serviceApiType + "' is already registered");
		}		
		serviceProviders.put(providerId, serviceProvider);
		providerClassIndex.put(serviceProvider.getClass(), providerId);
		providerApiTypeIndex.put(serviceApiType, providerId);
	}
	
	public ServiceProvider<?> getServiceProviderById(String providerId) {
		ServiceProvider<?> provider = serviceProviders.get(providerId);
		if (provider == null) {
			throw new IllegalArgumentException("No ServiceProvider with id '" + providerId + "' is registered");
		}		
		return provider;
	}

	@SuppressWarnings("unchecked")
	public <P extends ServiceProvider<?>> P getServiceProviderById(String providerId, Class<P> providerType) {
		ServiceProvider<?> provider = getServiceProviderById(providerId, providerType);
		if (!providerType.isAssignableFrom(provider.getClass())) {
			throw new IllegalArgumentException("ServiceProvider '" + providerId + "' not instance of [" + providerType.getName() + "]");
		}
		return (P) provider;
	}

	@SuppressWarnings("unchecked")
	public <P extends ServiceProvider<?>> P getServiceProviderByClass(Class<P> providerClass) {
		return (P) getServiceProviderById(providerId(providerClass));
	}

	@SuppressWarnings("unchecked")
	public <S> ServiceProvider<S> getServiceProviderByApi(Class<S> serviceApiType) {
		String providerId = providerApiTypeIndex.get(serviceApiType);		
		if (providerId == null) {
			throw new IllegalArgumentException("No ServiceProvider for API type [" + serviceApiType + "] is registered");
		}		
		return (ServiceProvider<S>) getServiceProviderById(providerId);
	}

	public String providerId(ServiceProvider<?> provider) {
		return providerId(provider.getClass());
	}
	
	// internal helpers
	
	private String providerId(Class<?> providerClass) {
		String providerId = providerClassIndex.get(providerClass);		
		if (providerId == null) {
			throw new IllegalArgumentException("No ServiceProvider of class [" + providerClass.getName() + "] is registered");
		}
		return providerId;		
	}
}