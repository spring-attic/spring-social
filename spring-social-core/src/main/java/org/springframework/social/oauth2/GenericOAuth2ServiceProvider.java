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

import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;

/**
 * Generic implementation of {@link OAuth2ServiceProvider} that carries no intrinsic knowledge of the API it will be creating connections against.
 * Instead, it must be configured with API provider-specific details.
 * This enables developers to use Spring Social to create connections against any API for which there isn't a ready-to-use connection support and API binding
 * without creating one-off connection factory and service provider implementations.
 * The API binding provided by this service provider is simply a {@link RestOperations}, but the underlying {@link RestTemplate} is configured to automatically
 * add <code>Authorization</code> headers to all requests.
 * @author Craig Walls
 */
public class GenericOAuth2ServiceProvider extends AbstractOAuth2ServiceProvider<RestOperations> {

	private TokenStrategy tokenStrategy;

	/**
	 * Creates an instance of GenericOAuth1ServiceProvider for a provider that offers a separate authentication URL.
	 * @param appId The application's ID/key for the API.
	 * @param appSecret The application's secret for the API.
	 * @param authorizeUrl The API's OAuth 2 authorization URL.
	 * @param authenticateUrl The API's OAuth 2 authentication URL. If null, then the authorization URL will be used by default.
	 * @param accessTokenUrl The API's OAuth 2 access token URL.
	 * @param useParametersForClientCredentials If true, client credentials will be sent as parameters. If false, the client with be authenticated via HTTP Basic
	 * @param tokenStrategy The token strategy indicating how the access token should be carried on API requests.
	 */
	public GenericOAuth2ServiceProvider(String appId, String appSecret, String authorizeUrl, String authenticateUrl, String accessTokenUrl, boolean useParametersForClientCredentials, TokenStrategy tokenStrategy) {
		super(getOAuth2Template(appId, appSecret, authorizeUrl, authenticateUrl, accessTokenUrl, useParametersForClientCredentials));
		this.tokenStrategy = tokenStrategy;
	}
	
	@Override
	public RestOperations getApi(String accessToken) {
		return new GenericApiBinding(accessToken, tokenStrategy).getRestTemplate();
	}

	private static OAuth2Template getOAuth2Template(String appId, String appSecret, String authorizeUrl, String authenticateUrl, String accessTokenUrl, boolean useParameters) {
		OAuth2Template oAuth2Template = new OAuth2Template(appId, appSecret, authorizeUrl, authenticateUrl != null ? authenticateUrl : authorizeUrl, accessTokenUrl);
		oAuth2Template.setUseParametersForClientAuthentication(useParameters);
		return oAuth2Template;
	}
	
	private static class GenericApiBinding extends AbstractOAuth2ApiBinding {
		public GenericApiBinding(String accessToken, TokenStrategy tokenStrategy) {
			super(accessToken, tokenStrategy);
		}
	}

}
