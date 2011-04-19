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

import org.springframework.social.ServiceProvider;

/**
 * Base abstraction for factories that construct ServiceProviderConnection instances.
 * Encapsulates the differences and knowledge of specific connection implementations, for example, the difference between OAuth1 and OAuth2 based connections. 
 * @author Keith Donald
 * @param <S> the connection service API type
 */
public abstract class ServiceProviderConnectionFactory<S> {

	private final String providerId;
	
	private final ServiceProvider<S> serviceProvider;

	private final ServiceApiAdapter<S> serviceApiAdapter;
	
	/**
	 * Creates a new ServiceProviderConnectionFactory.
	 * @param providerId the assigned, unique id of the provider this factory creates connections to (used when indexing this factory in a registry)
	 * @param serviceProvider the model for the ServiceProvider used to conduct the connection authorization/refresh flow and obtain a native service API instance
	 * @param serviceApiAdapter the adapter that maps common operations exposed by the ServiceProvider's API to the uniform ServiceProviderConnection model
	 */
	public ServiceProviderConnectionFactory(String providerId, ServiceProvider<S> serviceProvider, ServiceApiAdapter<S> serviceApiAdapter) {
		this.providerId = providerId;
		this.serviceProvider = serviceProvider;
		this.serviceApiAdapter = nullSafeServiceApiAdapter(serviceApiAdapter);
	}

	// subclassing hooks
	
	/**
	 * The unique id of the provider this factory creates connections to.
	 * Used to index this ServiceProviderConnetionFactory in a registry to support dynamic lookup operations.
	 * @see ServiceProviderConnectionFactoryLocator#getConnectionFactory(String).
	 */
	public String getProviderId() {
		return providerId;
	}

	/**
	 * Exposes the ServiceProvider instance to subclasses.
	 */
	protected ServiceProvider<S> getServiceProvider() {
		return serviceProvider;
	}

	/**
	 * Exposes the ServiceApiAdapter to subclasses.
	 */
	protected ServiceApiAdapter<S> getServiceApiAdapter() {
		return serviceApiAdapter;
	}

	// subclassing hooks
	
	public abstract ServiceProviderConnection<S> createConnection(ServiceProviderConnectionData data);
	
	// internal helpers
	
	@SuppressWarnings("unchecked")
	private ServiceApiAdapter<S> nullSafeServiceApiAdapter(ServiceApiAdapter<S> serviceApiAdapter) {
		if (serviceApiAdapter != null) {
			return serviceApiAdapter;
		}
		return (ServiceApiAdapter<S>) NullServiceApiAdapter.INSTANCE;
	}

}
