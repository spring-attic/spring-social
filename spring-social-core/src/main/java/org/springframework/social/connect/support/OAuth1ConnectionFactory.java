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
import org.springframework.social.connect.ConnectionFactory;
import org.springframework.social.oauth1.OAuth1Operations;
import org.springframework.social.oauth1.OAuth1ServiceProvider;
import org.springframework.social.oauth1.OAuthToken;

/**
 * Factory for creating OAuth1-based Connections.
 * May be subclassed to further simplify construction e.g. TwitterConnectionFactory.
 * @author Keith Donald
 * @param <A> the service provider's API type.
 */
public class OAuth1ConnectionFactory<A> extends ConnectionFactory<A> {
	
	/**
	 * Create a {@link OAuth1ConnectionFactory}.
	 * @param providerId the provider id e.g. "twitter"
	 * @param serviceProvider the ServiceProvider model for conducting the authorization flow and obtaining a native service API instance.
	 * @param apiAdapter the ApiAdapter for mapping the provider-specific service API model to the uniform {@link Connection} interface.
	 */
	public OAuth1ConnectionFactory(String providerId, OAuth1ServiceProvider<A> serviceProvider, ApiAdapter<A> apiAdapter) {
		super(providerId, serviceProvider, apiAdapter);
	}

	/**
	 * @return the ServiceProvider's {@link OAuth1Operations} that allows the client application to conduct the OAuth1 flow with the provider.
	 */
	public OAuth1Operations getOAuthOperations() {
		return getOAuth1ServiceProvider().getOAuthOperations();
	}

	/**
	 * Create a OAuth1-based Connection from the access token response returned after {@link #getOAuthOperations() completing the OAuth1 flow}.
	 * @param accessToken the access token
	 * @return the new service provider connection
	 * @see OAuth1Operations#exchangeForAccessToken(org.springframework.social.oauth1.AuthorizedRequestToken, org.springframework.util.MultiValueMap)
	 */
	public Connection<A> createConnection(OAuthToken accessToken) {
		String providerUserId = extractProviderUserId(accessToken);
		return new OAuth1Connection<A>(getProviderId(), providerUserId, accessToken.getValue(), accessToken.getSecret(), getOAuth1ServiceProvider(), getApiAdapter());		
	}
	
	/**
	 * Create a OAuth1-based {@link Connection} from the connection data.
	 */
	public Connection<A> createConnection(ConnectionData data) {
		return new OAuth1Connection<A>(data, getOAuth1ServiceProvider(), getApiAdapter());
	}

	// subclassing hooks
	
	/**
	 * Hook for extracting the providerUserId from the returned access token response, if it is available.
	 * Default implementation returns null, indicating it is not exposed and another remote API call will be required to obtain it.
	 * Subclasses may override.
	 * @param accessToken an access token
	 * @return the providerId associated with an access token, if that information is available.
	 */
	protected String extractProviderUserId(OAuthToken accessToken) {
		return null;
	}

	// internal helpers
	
	private OAuth1ServiceProvider<A> getOAuth1ServiceProvider() {
		return (OAuth1ServiceProvider<A>) getServiceProvider();
	}
	
}
