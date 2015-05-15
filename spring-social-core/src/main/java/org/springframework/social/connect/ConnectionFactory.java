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
package org.springframework.social.connect;

import org.springframework.social.ServiceProvider;

/**
 * Base abstraction for factories that construct service provider {@link Connection} instances.
 * Encapsulates the differences and knowledge of specific connection implementations, for example, the difference between OAuth1 and OAuth2 based connections. 
 * @author Keith Donald
 * @param <A> the connection service API type
 */
public abstract class ConnectionFactory<A> {

	private final String providerId;
	
	private final ServiceProvider<A> serviceProvider;

	private final ApiAdapter<A> apiAdapter;
	
	/**
	 * Creates a new ConnectionFactory.
	 * @param providerId the assigned, unique id of the provider this factory creates connections to (used when indexing this factory in a registry)
	 * @param serviceProvider the model for the ServiceProvider used to conduct the connection authorization/refresh flow and obtain a native service API instance
	 * @param apiAdapter the adapter that maps common operations exposed by the ServiceProvider's API to the uniform {@link Connection} model
	 */
	public ConnectionFactory(String providerId, ServiceProvider<A> serviceProvider, ApiAdapter<A> apiAdapter) {
		this.providerId = providerId;
		this.serviceProvider = serviceProvider;
		this.apiAdapter = nullSafeApiAdapter(apiAdapter);
	}

	// subclassing hooks
	
	/**
	 * The unique id of the provider this factory creates connections to.
	 * Used to index this {@link ConnectionFactory} in a registry to support dynamic lookup operations.
	 * @return the provider ID
	 */
	public String getProviderId() {
		return providerId;
	}

	/**
	 * Exposes the ServiceProvider instance to subclasses.
	 * @return the service provider
	 */
	protected ServiceProvider<A> getServiceProvider() {
		return serviceProvider;
	}

	/**
	 * Exposes the ApiAdapter to subclasses.
	 * @return the ApiAdapter
	 */
	protected ApiAdapter<A> getApiAdapter() {
		return apiAdapter;
	}

	// subclassing hooks
	
	public abstract Connection<A> createConnection(ConnectionData data);
	
	// internal helpers
	
	@SuppressWarnings("unchecked")
	private ApiAdapter<A> nullSafeApiAdapter(ApiAdapter<A> apiAdapter) {
		if (apiAdapter != null) {
			return apiAdapter;
		}
		return (ApiAdapter<A>) NullApiAdapter.INSTANCE;
	}

}
