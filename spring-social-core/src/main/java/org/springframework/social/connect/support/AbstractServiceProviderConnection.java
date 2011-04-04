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
import org.springframework.social.connect.ServiceProviderConnectionRecord;
import org.springframework.social.connect.spi.ProviderProfile;
import org.springframework.social.connect.spi.ServiceApiAdapter;

abstract class AbstractServiceProviderConnection<S> implements ServiceProviderConnection<S> {

	private final ServiceProviderConnectionKey key;
	
	private ProviderProfile profile;

	private final Object profileMonitor = new Object();
	
	private S serviceApi;
	
	private final Object serviceApiMonitor = new Object();
	
	private final ServiceApiAdapter<S> serviceApiAdapter;
	
 	public AbstractServiceProviderConnection(String providerId, String providerUserId, S serviceApi, ServiceApiAdapter<S> serviceApiAdapter) {
 		this.key = createKey(providerId, providerUserId, serviceApi, serviceApiAdapter);
 		this.serviceApi = serviceApi;
 		this.serviceApiAdapter = serviceApiAdapter;
 	}
 	 	
	public ServiceProviderConnectionKey getKey() {
		return key;
	}

	public String getProfileName() {
		return getProviderProfile().getName();
	}

	public String getProfileUrl() {
		return getProviderProfile().getUrl();
	}

	public String getProfilePictureUrl() {
		return getProviderProfile().getPictureUrl();
	}

	public boolean test() {
		return serviceApiAdapter.test(serviceApi);
	}
	
	public boolean hasExpired() {
		return false;
	}

	public void refresh() {
		synchronized (serviceApiMonitor) {
			this.serviceApi = doRefresh();
		}
	}

	public void updateStatus(String message) {
		serviceApiAdapter.updateStatus(serviceApi, message);
	}

	public void sync() {
		synchronized (profileMonitor) {
			profile = fetchProfile();
		}
	}

	public S getServiceApi() {
		synchronized (serviceApiMonitor) {
			return serviceApi;
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

	public ServiceProviderConnectionRecord createConnectionRecord() {
		return null;
	}
	
	// internal helpers

	private ServiceProviderConnectionKey createKey(String providerId, String providerUserId, S serviceApi, ServiceApiAdapter<S> serviceApiAdapter) {
		if (providerUserId == null) {
			profile = serviceApiAdapter.getProfile(serviceApi);
			providerUserId = profile.getId();
		}			
		return new ServiceProviderConnectionKey(providerId, providerUserId);
	}

	private ProviderProfile getProviderProfile() {
		synchronized (profileMonitor) {
			if (profile == null) {
				profile = fetchProfile();
			}
			return profile;
		}
	}
	
	private ProviderProfile fetchProfile() {
		return serviceApiAdapter.getProfile(serviceApi);
	}

}