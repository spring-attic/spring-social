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
package org.springframework.social.oauth1;

import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;

/**
 * Generic implementation of {@link OAuth1ServiceProvider} that carries no intrinsic knowledge of the API it will be creating connections against.
 * Instead, it must be configured with API provider-specific details.
 * This enables developers to use Spring Social to create connections against any API for which there isn't a ready-to-use connection support and API binding
 * without creating one-off connection factory and service provider implementations.
 * The API binding provided by this service provider is simply a {@link RestOperations}, but the underlying {@link RestTemplate} is configured to automatically
 * add <code>Authorization</code> headers to all requests.
 * @author Craig Walls
 */
public class GenericOAuth1ServiceProvider extends AbstractOAuth1ServiceProvider<RestOperations> {

	/**
	 * Creates an instance of GenericOAuth1ServiceProvider for a provider that offers a separate authentication URL.
	 * @param consumerKey The application's consumer key for the API.
	 * @param consumerSecret The application's consumer secret for the API.
	 * @param requestTokenUrl The API's OAuth 1.0/1.0a request token URL.
	 * @param authorizeUrl The API's OAuth 1.0/1.0a authorization URL.
	 * @param authenticateUrl The API's OAuth 1.0/1.0a authentication URL. If null, then the authorization URL will be used by default.
	 * @param accessTokenUrl The API's OAuth 1.0/1.0a access token URL.
	 * @param oauth1Version The version of OAuth 1 (OAuth 1.0 or OAuth 1.0a) supported by the provider.
	 */
	public GenericOAuth1ServiceProvider(String consumerKey, String consumerSecret, String requestTokenUrl, String authorizeUrl, String authenticateUrl, String accessTokenUrl, OAuth1Version oauth1Version) {
		super(consumerKey, consumerSecret, new OAuth1Template(
				consumerKey, 
				consumerSecret, 
				requestTokenUrl, 
				authorizeUrl, 
				authenticateUrl != null ? authenticateUrl : authorizeUrl, 
				accessTokenUrl,
				oauth1Version));
	}

	@Override
	public RestOperations getApi(String accessToken, String secret) {
		return new GenericApiBinding(getConsumerKey(), getConsumerSecret(), accessToken, secret).getRestTemplate();
	}
	
	private static class GenericApiBinding extends AbstractOAuth1ApiBinding {
		public GenericApiBinding(String consumerKey, String consumerSecret, String accessToken, String accessTokenSecret) {
			super(consumerKey, consumerSecret, accessToken, accessTokenSecret);
		}
	}

}
