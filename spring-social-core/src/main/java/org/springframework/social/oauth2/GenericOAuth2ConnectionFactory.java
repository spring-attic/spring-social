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
package org.springframework.social.oauth2;

import org.springframework.social.connect.ApiAdapter;
import org.springframework.social.connect.support.OAuth2ConnectionFactory;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;

/**
 * Generic implementation of an {@link OAuth2ConnectionFactory} that carries no intrinsic knowledge of the API it will be creating connections against.
 * Instead, it must be configured with API provider-specific details.
 * This enables developers to use Spring Social to create connections against any API for which there isn't a ready-to-use connection support and API binding
 * without creating one-off connection factory and service provider implementations.
 * The API binding provided by this connection factory is simply a {@link RestOperations}, but the underlying {@link RestTemplate} is configured to automatically
 * add <code>Authorization</code> headers to all requests.
 * @author Craig Walls
 */
public class GenericOAuth2ConnectionFactory extends OAuth2ConnectionFactory<RestOperations> {

	/**
	 * Creates an instance of GenericOAuth2ConnectionFactory.
	 * Defaults to use the authorization URL as the authentication URL, to send client credentials via HTTP Basic, and to send the access token
	 * in the Authorization header for API requests.
	 * @param providerId Some String that acts as the unique ID for the API provider.
	 * @param appId The application's ID/key for the API.
	 * @param appSecret The application's secret for the API.
	 * @param authorizeUrl The API's OAuth 2 authorization URL. Will also be used as the default authentication URL.
	 * @param accessTokenUrl The API's OAuth 2 access token URL.
	 * @param apiAdapter A custom implementation of {@link ApiAdapter} used to fetch data when creating the connection.
	 */
	public GenericOAuth2ConnectionFactory(
			String providerId, 
			String appId, 
			String appSecret,
			String authorizeUrl,
			String accessTokenUrl,
			ApiAdapter<RestOperations> apiAdapter) {
		this(providerId, appId, appSecret, authorizeUrl, authorizeUrl, accessTokenUrl, false, TokenStrategy.AUTHORIZATION_HEADER, apiAdapter);
	}

	/**
	 * Creates an instance of GenericOAuth2ConnectionFactory for a provider that offers a separate authentication URL.
	 * @param providerId Some String that acts as the unique ID for the API provider.
	 * @param appId The application's ID/key for the API.
	 * @param appSecret The application's secret for the API.
	 * @param authorizeUrl The API's OAuth 2 authorization URL.
	 * @param authenticateUrl The API's OAuth 2 authentication URL.
	 * @param accessTokenUrl The API's OAuth2 access token URL.
	 * @param sendClientCredentialsAsParameters If true, send client credentials as query parameter. If false, use HTTP Basic.
	 * @param tokenStrategy The token strategy indicating how the access token should be carried on API requests.
	 * @param apiAdapter A custom implementation of {@link ApiAdapter} used to fetch data when creating the connection.
	 */
	public GenericOAuth2ConnectionFactory(
			String providerId, 
			String appId, 
			String appSecret,
			String authorizeUrl,
			String authenticateUrl,
			String accessTokenUrl,
			boolean sendClientCredentialsAsParameters,
			TokenStrategy tokenStrategy,
			ApiAdapter<RestOperations> apiAdapter) {
		super(providerId, 
			  new GenericOAuth2ServiceProvider(appId, appSecret, authorizeUrl, authenticateUrl, accessTokenUrl, sendClientCredentialsAsParameters, tokenStrategy), 
			  apiAdapter);
	}
}
