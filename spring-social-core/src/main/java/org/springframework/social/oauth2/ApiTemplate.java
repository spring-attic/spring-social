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
package org.springframework.social.oauth2;

import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

/**
 * Base class for OAuth 2-based provider API bindings.
 * @author Craig Walls
 */
public abstract class ApiTemplate {

	private final String accessToken;

	private final RestTemplate restTemplate;

	/**
	 * Constructs the API template with an access token for performing operations on behalf of a user.
	 * @param restTemplate the RestTemplate to use when communicating with the provider's REST API
	 * @param accessToken the access token
	 */
	protected ApiTemplate(RestTemplate restTemplate, String accessToken) {
		this.restTemplate = restTemplate;
		this.accessToken = accessToken;
	}
	
	/**
	 * Override the default ClientHttpRequestFactory. This is useful when custom configuration of the request factory is required, such as configuring proxy server details.
	 * @param requestFactory the request factory
	 */
	public void setRequestFactory(ClientHttpRequestFactory requestFactory) {
		restTemplate.setRequestFactory(createRequestFactory(requestFactory, accessToken));
	}

	protected abstract ClientHttpRequestFactory createRequestFactory(ClientHttpRequestFactory requestFactory, String accessToken);

	public RestTemplate getRestTemplate() {
		return restTemplate;
	}
	
	/**
	 * Base class for OAuth 2-based provider API bindings adhering to the latest OAuth 2 draft specification.
	 * @author Craig Walls
	 */
	public static class StandardApiTemplate extends ApiTemplate {

		public StandardApiTemplate(String accessToken) {
			super(ProtectedResourceClientFactory.standard(accessToken), accessToken);
		}
		
		protected ClientHttpRequestFactory createRequestFactory(ClientHttpRequestFactory requestFactory, String accessToken) {
			return ProtectedResourceClientFactory.standardOAuthSigningRequestFactoryIfNecessary(requestFactory, accessToken);
		}
	}

	/**
	 * Base class for OAuth 2-based provider API bindings adhering to the Draft 8 of the OAuth 2 specification.
	 * @author Craig Walls
	 */
	public static class Draft8ApiTemplate extends ApiTemplate {
		public Draft8ApiTemplate(String accessToken) {
			super(ProtectedResourceClientFactory.draft8(accessToken), accessToken);
		}

		protected ClientHttpRequestFactory createRequestFactory(ClientHttpRequestFactory requestFactory, String accessToken) {
			return ProtectedResourceClientFactory.draft8OAuthSigningRequestFactoryIfNecessary(requestFactory, accessToken);
		}
	}
	
	/**
	 * Base class for OAuth 2-based provider API bindings adhering to the Draft 10 of the OAuth 2 specification.
	 * @author Craig Walls
	 */
	public static class Draft10ApiTemplate extends ApiTemplate {
		public Draft10ApiTemplate(String accessToken) {
			super(ProtectedResourceClientFactory.draft10(accessToken), accessToken);
		}

		protected ClientHttpRequestFactory createRequestFactory(ClientHttpRequestFactory requestFactory, String accessToken) {
			return ProtectedResourceClientFactory.draft10OAuthSigningRequestFactoryIfNecessary(requestFactory, accessToken);
		}
	}
}
