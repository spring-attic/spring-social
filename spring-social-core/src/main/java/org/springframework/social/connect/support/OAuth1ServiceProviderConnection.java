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
import org.springframework.social.connect.ServiceProviderConnectionData;
import org.springframework.social.connect.ServiceProviderConnectionKey;
import org.springframework.social.connect.ServiceProviderUser;
import org.springframework.social.connect.spi.ServiceApiAdapter;
import org.springframework.social.oauth1.OAuth1ServiceProvider;

/**
 * An OAuth1-based ServiceProviderConnection implementation.
 * In general, this implementation is expected to be suitable for all OAuth1-based providers and should not require subclassing.
 * Subclasses of {@link OAuth1ServiceProviderConnectionFactory} should be favored to encapsulate details specific to an OAuth1-based provider.
 * @author Keith Donald
 * @param <S> the service API type
 * @see OAuth1ServiceProviderConnectionFactory
 */
public class OAuth1ServiceProviderConnection<S> implements ServiceProviderConnection<S> {

	private final ServiceProviderConnectionKey key;

	private final OAuth1ServiceProvider<S> serviceProvider;
	
	private final ServiceApiAdapter<S> serviceApiAdapter;

	private ServiceProviderUser user;

	private String accessToken;
	
	private String secret;

	private S serviceApi;

	private final Object monitor = new Object();
	
	/**
	 * Creates a new {@link OAuth1ServiceProviderConnection} from the data provided.
	 * Designed to be called to create a {@link OAuth1ServiceProviderConnection} after receiving an access token response successfully.
	 * The providerUserId may be null in this case: if so, this constructor will try to resolve it using the service API obtained from the {@link OAuth1ServiceProvider}.
	 * @param providerId the provider id e.g. "twitter"
	 * @param providerUserId the provider user ID (may be null if not returned as part of the access token response)
	 * @param accessToken the granted access token
	 * @param secret the access token secret (OAuth1-specific)
	 * @param serviceProvider the ServiceProvider model
	 * @param serviceApiAdapter the ServiceApiAdapter for the ServiceProvider.
	 */
	public OAuth1ServiceProviderConnection(String providerId, String providerUserId, String accessToken, String secret, OAuth1ServiceProvider<S> serviceProvider, ServiceApiAdapter<S> serviceApiAdapter) {
		this.serviceProvider = serviceProvider;
		this.serviceApiAdapter = serviceApiAdapter;
		initAccessTokens(accessToken, secret);
		initServiceApi();
		this.key = createKey(providerId, providerUserId);
	}

	/**
	 * Creates a new {@link OAuth1ServiceProviderConnection} from the data provided.
	 * Designed to be called when re-constituting an existing {@link ServiceProviderConnection}, for example, from {@link ServiceProviderConnectionData}.
	 * @param key the service provider connection key
	 * @param user the service provider user model
	 * @param accessToken the access token
	 * @param secret the access token secret
	 * @param serviceProvider the ServiceProvider model
	 * @param serviceApiAdapter the ServiceApiAdapter for the ServiceProvider.
	 */
	public OAuth1ServiceProviderConnection(ServiceProviderConnectionKey key, ServiceProviderUser user, String accessToken, String secret, OAuth1ServiceProvider<S> serviceProvider, ServiceApiAdapter<S> serviceApiAdapter) {
		this.key = key;
		this.user = user;
		this.serviceProvider = serviceProvider;
		this.serviceApiAdapter = serviceApiAdapter;
		initAccessTokens(accessToken, secret);
		initServiceApi();
	}

	// implementing ServiceProviderConnection
	
	public ServiceProviderConnectionKey getKey() {
		return key;
	}

	public ServiceProviderUser getUser() {
		synchronized (monitor) {
			return user;
		}
	}

	public boolean test() {
		return serviceApiAdapter.test(serviceApi);
	}

	public boolean hasExpired() {
		// not supported by OAuth 1
		return false;
	}

	public void refresh() {
		// not supported by OAuth 1
	}

	public void updateStatus(String message) {
		serviceApiAdapter.updateStatus(serviceApi, message);
	}

	public void sync() {
		synchronized (monitor) {
			initUser();
		}
	}

	public S getServiceApi() {
		return serviceApi;
	}

	public ServiceProviderConnectionData createData() {
		return new ServiceProviderConnectionData(key.getProviderId(), key.getProviderUserId(), user.getProfileName(), user.getProfileUrl(), user.getProfilePictureUrl(), accessToken, secret, null, null);
	}

	// identity
	
	@SuppressWarnings("rawtypes")
	public boolean equals(Object o) {
		if (!(o instanceof OAuth2ServiceProviderConnection)) {
			return false;
		}
		OAuth1ServiceProviderConnection other = (OAuth1ServiceProviderConnection) o;
		return key.equals(other.key);
	}
	
	public int hashCode() {
		return key.hashCode();
	}
	
	// internal helpers
	
	private void initAccessTokens(String accessToken, String secret) {
		this.accessToken = accessToken;
		this.secret = secret;
	}

	private void initServiceApi() {
		serviceApi = serviceProvider.getServiceApi(accessToken, secret);
	}
	
	private ServiceProviderConnectionKey createKey(String providerId, String providerUserId) {
		if (providerUserId == null) {
			initUser();
			providerUserId = user.getId();
		}
		return new ServiceProviderConnectionKey(providerId, providerUserId);		
	}
	
	private void initUser() {
		user = serviceApiAdapter.getUser(serviceApi);
	}

}
