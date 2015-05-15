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
package org.springframework.social.connect.support;

import org.springframework.social.connect.ApiAdapter;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionData;
import org.springframework.social.connect.ConnectionKey;
import org.springframework.social.connect.ConnectionValues;
import org.springframework.social.connect.UserProfile;

/**
 * Base support class for {@link Connection} implementations.
 * Defines the state and behavior that is common across connection implementations and independent of any specific authorization protocol.
 * @author Keith Donald
 * @param <A> the service provider's API type
 */
public abstract class AbstractConnection<A> implements Connection<A> {

	private static final long serialVersionUID = 7330875324290049412L;

	private transient final ApiAdapter<A> apiAdapter;

	private ConnectionKey key;

	private String displayName;
	
	private String profileUrl;
	
	private String imageUrl;

	private boolean valuesInitialized;

	private transient final Object monitor = new Object();

	/**
	 * Creates a new connection.
	 * @param apiAdapter the Service API adapter
	 */
	public AbstractConnection(ApiAdapter<A> apiAdapter) {
		this.apiAdapter = apiAdapter;
	}
	
	/**
	 * Creates a connection from the data provider.
	 * @param data the connection data
	 * @param apiAdapter the Service API adapter
	 */
	public AbstractConnection(ConnectionData data, ApiAdapter<A> apiAdapter) {
		key = new ConnectionKey(data.getProviderId(), data.getProviderUserId());
		this.apiAdapter = apiAdapter;
		displayName = data.getDisplayName();
		profileUrl = data.getProfileUrl();
		imageUrl = data.getImageUrl();
		valuesInitialized = true;
	}
	
	// implementing Connection
	
	public ConnectionKey getKey() {
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
		return apiAdapter.test(getApi());
	}

	public boolean hasExpired() {
		return false;
	}

	public void refresh() {
		
	}

	public UserProfile fetchUserProfile() {
		return apiAdapter.fetchUserProfile(getApi());
	}

	public void updateStatus(String message) {
		apiAdapter.updateStatus(getApi(), message);
	}

	public void sync() {
		synchronized (monitor) {
			setValues();
		}
	}

	// subclassing hooks
	
	public abstract A getApi();

	public abstract ConnectionData createData();

	/**
	 * Hook that should be called by subclasses to initialize the key property when establishing a new connection.
	 * @param providerId the providerId
	 * @param providerUserId the providerUserId
	 */
	protected void initKey(String providerId, String providerUserId) {
		if (providerUserId == null) {
			providerUserId = setValues().providerUserId;
		}
		key = new ConnectionKey(providerId, providerUserId);		
	}

	/**
	 * Provides subclasses with access to a monitor that can be used to synchronize access to this connection.
	 * @return the monitor object
	 */
	protected Object getMonitor() {
		return monitor;
	}
	
	// identity
	
	@SuppressWarnings("rawtypes")
	public boolean equals(Object o) {
		if (!(o instanceof Connection)) {
			return false;
		}
		Connection other = (Connection) o;
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
		apiAdapter.setConnectionValues(getApi(), values);
		valuesInitialized = true;
		return values;
	}
	
	private class ServiceProviderConnectionValuesImpl implements ConnectionValues {

		public void setProviderUserId(String providerUserId) {
			this.providerUserId = providerUserId;
		}
		
		public void setDisplayName(String displayName) {
			AbstractConnection.this.displayName = displayName;
		}
		
		public void setProfileUrl(String profileUrl) {
			AbstractConnection.this.profileUrl = profileUrl;
		}
		
		public void setImageUrl(String imageUrl) {
			AbstractConnection.this.imageUrl = imageUrl;
		}

		private String providerUserId;
		
	}
}
