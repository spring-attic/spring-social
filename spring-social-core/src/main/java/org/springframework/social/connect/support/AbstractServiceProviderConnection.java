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

import org.springframework.social.connect.ServiceApiAdapter;
import org.springframework.social.connect.ServiceProviderConnection;
import org.springframework.social.connect.ServiceProviderConnectionData;
import org.springframework.social.connect.ServiceProviderConnectionKey;
import org.springframework.social.connect.ServiceProviderConnectionValues;
import org.springframework.social.connect.ServiceProviderUserProfile;

/**
 * Base support class for {@link ServiceProviderConnection} implementations.
 * Defines state and behavior that is common across implementations and independent of the authorization protocol.
 * @author Keith Donald
 * @param <S> the service API type
 */
public abstract class AbstractServiceProviderConnection<S> implements ServiceProviderConnection<S> {

	private final ServiceApiAdapter<S> serviceApiAdapter;

	private ServiceProviderConnectionKey key;

	private String displayName;
	
	private String profileUrl;
	
	private String imageUrl;

	private boolean valuesInitialized;

	private final Object monitor = new Object();

	/**
	 * Creates a new connection.
	 * @param serviceApiAdapter the Service API adapter
	 */
	public AbstractServiceProviderConnection(ServiceApiAdapter<S> serviceApiAdapter) {
		this.serviceApiAdapter = serviceApiAdapter;
	}
	
	/**
	 * Creates a connection from the data provider.
	 * @param data the connection data
	 * @param serviceApiAdapter the Service API adapter
	 */
	public AbstractServiceProviderConnection(ServiceProviderConnectionData data, ServiceApiAdapter<S> serviceApiAdapter) {
		key = new ServiceProviderConnectionKey(data.getProviderId(), data.getProviderUserId());
		this.serviceApiAdapter = serviceApiAdapter;
		displayName = data.getDisplayName();
		profileUrl = data.getProfileUrl();
		imageUrl = data.getImageUrl();
		valuesInitialized = true;
	}
	
	// implementing ServiceProviderConnection
	
	public ServiceProviderConnectionKey getKey() {
		return key;
	}

	public String getDisplayName() {
		synchronized (monitor) {
			initValues();
			return displayName;			
		}
	}

	public String getProfileUrl() {
		synchronized (monitor) {
			initValues();
			return profileUrl;
		}
	}

	public String getImageUrl() {
		synchronized (monitor) {
			initValues();			
			return imageUrl;
		}
	}

	public boolean test() {
		return serviceApiAdapter.test(getServiceApi());
	}

	public boolean hasExpired() {
		return false;
	}

	public void refresh() {
		
	}

	public ServiceProviderUserProfile fetchUserProfile() {
		return serviceApiAdapter.fetchUserProfile(getServiceApi());
	}

	public void updateStatus(String message) {
		serviceApiAdapter.updateStatus(getServiceApi(), message);
	}

	public void sync() {
		synchronized (monitor) {
			setValues();
		}
	}

	// subclassing hooks
	
	public abstract S getServiceApi();

	public abstract ServiceProviderConnectionData createData();

	/**
	 * Hook that should be called by subclasses to initialize the key property when establishing a new connection.
	 * @param providerId the providerId
	 * @param providerUserId the providerUserId
	 */
	protected void initKey(String providerId, String providerUserId) {
		if (providerUserId == null) {
			providerUserId = setValues().providerUserId;
		}
		key = new ServiceProviderConnectionKey(providerId, providerUserId);		
	}

	/**
	 * Provides subclasses with access to a monitor that can be used to synchronize access to this connection.
	 */
	protected Object getMonitor() {
		return monitor;
	}
	
	// identity
	
	@SuppressWarnings("rawtypes")
	public boolean equals(Object o) {
		if (!(o instanceof ServiceProviderConnection)) {
			return false;
		}
		ServiceProviderConnection other = (ServiceProviderConnection) o;
		return key.equals(other.getKey());
	}
	
	public int hashCode() {
		return key.hashCode();
	}

	// internal helpers
	
	private void initValues() {
		if (!valuesInitialized) {
			setValues();
		}
	}
	
	private ServiceProviderConnectionValuesImpl setValues() {
		ServiceProviderConnectionValuesImpl values = new ServiceProviderConnectionValuesImpl();
		this.serviceApiAdapter.setConnectionValues(getServiceApi(), values);
		return values;
	}
	
	private class ServiceProviderConnectionValuesImpl implements ServiceProviderConnectionValues {

		public void setProviderUserId(String providerUserId) {
			this.providerUserId = providerUserId;
		}
		
		public void setDisplayName(String displayName) {
			AbstractServiceProviderConnection.this.displayName = displayName;
		}
		
		public void setProfileUrl(String profileUrl) {
			AbstractServiceProviderConnection.this.profileUrl = profileUrl;
		}
		
		public void setImageUrl(String imageUrl) {
			AbstractServiceProviderConnection.this.imageUrl = imageUrl;
		}

		private String providerUserId;
		
	}
}
