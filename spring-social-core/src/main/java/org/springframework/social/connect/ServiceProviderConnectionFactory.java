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
import org.springframework.social.connect.spi.ServiceApiAdapter;

public abstract class ServiceProviderConnectionFactory<S> {

	private final String providerId;
	
	private final ServiceProvider<S> serviceProvider;

	private final ServiceApiAdapter<S> serviceApiAdapter;
	
	public ServiceProviderConnectionFactory(String providerId, ServiceProvider<S> serviceProvider, ServiceApiAdapter<S> serviceApiAdapter) {
		this.providerId = providerId;
		this.serviceProvider = serviceProvider;
		this.serviceApiAdapter = nullSafeServiceApiAdapter(serviceApiAdapter);
	}

	// sublassing hooks
	
	public String getProviderId() {
		return providerId;
	}

	protected ServiceProvider<S> getServiceProvider() {
		return serviceProvider;
	}

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
