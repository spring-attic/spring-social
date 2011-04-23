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
import org.springframework.social.oauth1.OAuth1ServiceProvider;

/**
 * An OAuth1-based ServiceProviderConnection implementation.
 * In general, this implementation is expected to be suitable for all OAuth1-based providers and should not require subclassing.
 * Subclasses of {@link OAuth1ServiceProviderConnectionFactory} should be favored to encapsulate details specific to an OAuth1-based provider.
 * @author Keith Donald
 * @param <S> the service API type
 * @see OAuth1ServiceProviderConnectionFactory
 */
public class OAuth1ServiceProviderConnection<S> extends AbstractServiceProviderConnection<S> {

	private final OAuth1ServiceProvider<S> serviceProvider;
	
	private String accessToken;
	
	private String secret;

	private S serviceApi;

	/**
	 * Creates a new {@link OAuth1ServiceProviderConnection} from a OAuth1 access token response.
	 * Designed to be called to establish a new {@link OAuth1ServiceProviderConnection} after receiving an access token response successfully.
	 * The providerUserId may be null in this case: if so, this constructor will try to resolve it using the service API obtained from the {@link OAuth1ServiceProvider}.
	 * @param providerId the provider id e.g. "twitter"
	 * @param providerUserId the provider user ID (may be null if not returned as part of the access token response)
	 * @param accessToken the granted access token
	 * @param secret the access token secret (OAuth1-specific)
	 * @param serviceProvider the OAuth1-based ServiceProvider
	 * @param serviceApiAdapter the ServiceApiAdapter for the ServiceProvider
	 */
	public OAuth1ServiceProviderConnection(String providerId, String providerUserId, String accessToken, String secret, OAuth1ServiceProvider<S> serviceProvider, ServiceApiAdapter<S> serviceApiAdapter) {
		super(serviceApiAdapter);
		this.serviceProvider = serviceProvider;
		initAccessTokens(accessToken, secret);
		initServiceApi();
		initKey(providerId, providerUserId);
	}

	/**
	 * Creates a new {@link OAuth1ServiceProviderConnection} from the data provided.
	 * Designed to be called when re-constituting an existing {@link ServiceProviderConnection} using {@link ServiceProviderConnectionData}.
	 * @param data the data holding the state of this service provider connection
	 * @param serviceProvider the OAuth1-based ServiceProvider
	 * @param serviceApiAdapter the ServiceApiAdapter for the ServiceProvider
	 */
	public OAuth1ServiceProviderConnection(ServiceProviderConnectionData data, OAuth1ServiceProvider<S> serviceProvider, ServiceApiAdapter<S> serviceApiAdapter) {
		super(data, serviceApiAdapter);
		this.serviceProvider = serviceProvider;
		initAccessTokens(data.getAccessToken(), data.getSecret());
		initServiceApi();
	}

	// implementing ServiceProviderConnection
	
	public S getServiceApi() {
		return serviceApi;
	}

	public ServiceProviderConnectionData createData() {
		synchronized (getMonitor()) {
			return new ServiceProviderConnectionData(getKey().getProviderId(), getKey().getProviderUserId(), getDisplayName(), getProfileUrl(), getImageUrl(), accessToken, secret, null, null);
		}
	}

	// internal helpers
	
	private void initAccessTokens(String accessToken, String secret) {
		this.accessToken = accessToken;
		this.secret = secret;
	}

	private void initServiceApi() {
		serviceApi = serviceProvider.getServiceApi(accessToken, secret);
	}
	
}
