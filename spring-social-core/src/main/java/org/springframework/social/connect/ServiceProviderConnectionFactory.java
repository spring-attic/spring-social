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
	
	private final boolean allowSignIn;
	
	public ServiceProviderConnectionFactory(String providerId, ServiceProvider<S> serviceProvider, ServiceApiAdapter<S> serviceApiAdapter, boolean allowSignIn) {
		this.providerId = providerId;
		this.serviceProvider = serviceProvider;
		this.serviceApiAdapter = nullSafeServiceApiAdapter(serviceApiAdapter);
		this.allowSignIn = allowSignIn;
	}

	// sublassing hooks
	
	public String getProviderId() {
		return providerId;
	}

	public ServiceProvider<S> getServiceProvider() {
		return serviceProvider;
	}

	public ServiceApiAdapter<S> getServiceApiAdapter() {
		return serviceApiAdapter;
	}

	public boolean isAllowSignIn() {
		return allowSignIn;
	}

	// internal helpers
	
	@SuppressWarnings("unchecked")
	private ServiceApiAdapter<S> nullSafeServiceApiAdapter(ServiceApiAdapter<S> serviceApiAdapter) {
		if (serviceApiAdapter != null) {
			return serviceApiAdapter;
		}
		return (ServiceApiAdapter<S>) NullServiceApiAdapter.INSTANCE;
	}
	
}
