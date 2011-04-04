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

import org.springframework.social.connect.ServiceProviderConnection;
import org.springframework.social.connect.ServiceProviderConnectionKey;
import org.springframework.social.connect.ServiceProviderConnectionMemento;
import org.springframework.social.connect.ServiceProviderUser;
import org.springframework.social.connect.spi.ServiceApiAdapter;

abstract class AbstractServiceProviderConnection<S> implements ServiceProviderConnection<S> {

	private final ServiceProviderConnectionKey key;
	
	private ServiceProviderUser user;

	private S serviceApi;
	
	private final ServiceApiAdapter<S> serviceApiAdapter;
	
 	public AbstractServiceProviderConnection(String providerId, String providerUserId, S serviceApi, ServiceApiAdapter<S> serviceApiAdapter) {
 		this.key = createKey(providerId, providerUserId, serviceApi, serviceApiAdapter);
 		this.serviceApi = serviceApi;
 		this.serviceApiAdapter = serviceApiAdapter;
 	}
 	 	
	public AbstractServiceProviderConnection(ServiceProviderConnectionMemento memento, S serviceApi, ServiceApiAdapter<S> serviceApiAdapter) {
		this.key = new ServiceProviderConnectionKey(memento.getProviderId(), memento.getProviderUserId());
		this.user = new ServiceProviderUser(memento.getProviderUserId(), memento.getProfileName(), memento.getProfileUrl(), memento.getProfilePictureUrl());
		this.serviceApi = serviceApi;
		this.serviceApiAdapter = serviceApiAdapter;
	}

	public final ServiceProviderConnectionKey getKey() {
		return key;
	}

	public ServiceProviderUser getUser() {
		synchronized (monitor) {
			if (user == null) {
				user = fetchUser();
			}			
			return user;
		}
	}

	public final boolean test() {
		return serviceApiAdapter.test(serviceApi);
	}
	
	public final boolean hasExpired() {
		return false;
	}

	public final void refresh() {
		synchronized (monitor) {
			this.serviceApi = doRefresh();
		}
	}

	public final void updateStatus(String message) {
		serviceApiAdapter.updateStatus(serviceApi, message);
	}

	public final void sync() {
		synchronized (monitor) {
			user = fetchUser();
		}
	}

	public final S getServiceApi() {
		synchronized (monitor) {
			return serviceApi;
		}
	}

	public final ServiceProviderConnectionMemento createMemento() {
		synchronized (monitor) {
			return doCreateMemento();
		}
	}
	
	// identity

	@SuppressWarnings("rawtypes")
	public boolean equals(Object o) {
		if (!(o instanceof AbstractServiceProviderConnection)) {
			return false;
		}
		AbstractServiceProviderConnection other = (AbstractServiceProviderConnection) o;
		return key.equals(other.key);
	}
	
	public int hashCode() {
		return key.hashCode();
	}

	// subclassing hooks
	
	protected S doRefresh() {
		return serviceApi;
	}
	
	protected abstract ServiceProviderConnectionMemento doCreateMemento();

	// internal helpers

	private ServiceProviderConnectionKey createKey(String providerId, String providerUserId, S serviceApi, ServiceApiAdapter<S> serviceApiAdapter) {
		if (providerUserId == null) {
			user = serviceApiAdapter.getUser(serviceApi);
			providerUserId = user.getId();
		}			
		return new ServiceProviderConnectionKey(providerId, providerUserId);
	}

	private final Object monitor = new Object();
		
	private ServiceProviderUser fetchUser() {
		return serviceApiAdapter.getUser(serviceApi);
	}

}