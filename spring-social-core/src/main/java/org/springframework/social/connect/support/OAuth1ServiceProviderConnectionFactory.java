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
import org.springframework.social.connect.ServiceProviderConnectionFactory;
import org.springframework.social.connect.ServiceProviderConnectionData;
import org.springframework.social.connect.ServiceProviderConnectionKey;
import org.springframework.social.connect.ServiceProviderUser;
import org.springframework.social.oauth1.OAuth1Operations;
import org.springframework.social.oauth1.OAuth1ServiceProvider;
import org.springframework.social.oauth1.OAuthToken;

/**
 * Factory for creating OAuth1-based ServiceProviderConnections.
 * May be subclassed to further simplify construction e.g. TwitterServiceProviderConnectionFactory.
 * @author Keith Donald
 * @param <S> the service API type.
 */
public class OAuth1ServiceProviderConnectionFactory<S> extends ServiceProviderConnectionFactory<S> {
	
	/**
	 * Create a {@link OAuth1ServiceProviderConnectionFactory}.
	 * @param providerId the provider id e.g. "twitter"
	 * @param serviceProvider the ServiceProvider model for conducting the authorization flow and obtaining a native service API instance.
	 * @param serviceApiAdapter the ServiceApiAdapter for mapping the provider-specific service API model to the uniform ServiceProviderConnection interface.
	 */
	public OAuth1ServiceProviderConnectionFactory(String providerId, OAuth1ServiceProvider<S> serviceProvider, ServiceApiAdapter<S> serviceApiAdapter) {
		super(providerId, serviceProvider, serviceApiAdapter);
	}

	/**
	 * Get the ServiceProvider's {@link OAuth1Operations} that allows the client application to conduct the OAuth1 flow with the provider.
	 */
	public OAuth1Operations getOAuthOperations() {
		return getOAuth1ServiceProvider().getOAuthOperations();
	}

	/**
	 * Create a OAuth1-based ServiceProviderConnection from the access token response returned after {@link #getOAuthOperations() completing the OAuth1 flow}.
	 * @param accessToken the access token
	 * @return the new service provider connection
	 * @see OAuth1Operations#exchangeForAccessToken(org.springframework.social.oauth1.AuthorizedRequestToken, org.springframework.util.MultiValueMap)
	 */
	public ServiceProviderConnection<S> createConnection(OAuthToken accessToken) {
		String providerUserId = extractProviderUserId(accessToken);
		return new OAuth1ServiceProviderConnection<S>(getProviderId(), providerUserId, accessToken.getValue(), accessToken.getSecret(), getOAuth1ServiceProvider(), getServiceApiAdapter());		
	}
	
	/**
	 * Create a OAuth1-based ServiceProviderConnection from the connection data.
	 */
	public ServiceProviderConnection<S> createConnection(ServiceProviderConnectionData data) {
		ServiceProviderConnectionKey key = new ServiceProviderConnectionKey(data.getProviderId(), data.getProviderUserId());
		ServiceProviderUser user = new ServiceProviderUser(data.getProviderUserId(), data.getProfileName(), data.getProfileUrl(), data.getProfilePictureUrl());
		return new OAuth1ServiceProviderConnection<S>(key, user, data.getAccessToken(), data.getSecret(), getOAuth1ServiceProvider(), getServiceApiAdapter());
	}

	// subclassing hooks
	
	/**
	 * Hook for extracting the providerUserId from the returned access token response, if it is available.
	 * Default implementation returns null, indicating it is not exposed and another remote API call will be required to obtain it.
	 * Subclasses may override.
	 */
	protected String extractProviderUserId(OAuthToken accessToken) {
		return null;
	}

	// internal helpers
	
	private OAuth1ServiceProvider<S> getOAuth1ServiceProvider() {
		return (OAuth1ServiceProvider<S>) getServiceProvider();
	}
	
}