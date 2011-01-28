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
package org.springframework.social.config;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.social.connect.ServiceProvider;
import org.springframework.social.connect.ServiceProviderFactory;

/**
 * Implementation of ServiceProviderFactory that retrieves service providers, by name, from the application context.
 * @author Craig Walls
 */
public class BeanServiceProviderFactory implements ServiceProviderFactory {

	private final Map<String, ServiceProvider<?>> serviceProviders;

	public BeanServiceProviderFactory(ListableBeanFactory beanFactory) {
		Map<String, ServiceProvider> providers = beanFactory.getBeansOfType(ServiceProvider.class);
		serviceProviders = new HashMap<String, ServiceProvider<?>>(providers.size(), 1);
		for (ServiceProvider<?> provider : providers.values()) {
			serviceProviders.put(provider.getId(), provider);
		}
	}
	public ServiceProvider<?> getServiceProvider(String id) {
		return serviceProviders.get(id);
	}

	public <S> ServiceProvider<S> getServiceProvider(String id, Class<S> serviceApiType) {
		ServiceProvider<?> provider = serviceProviders.get(id);
		if (provider == null) {
			throw new IllegalArgumentException("No such provider with id '" + id + "'");
		}
		if (serviceApiType.isAssignableFrom(provider.getClass())) {
			return (ServiceProvider<S>) provider;			
		} else {
			throw new IllegalArgumentException("ServiceProvider '" + id + "' not for serviceApiType " + serviceApiType);			
		}
	}

}
