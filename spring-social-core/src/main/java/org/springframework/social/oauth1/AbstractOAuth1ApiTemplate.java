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
package org.springframework.social.oauth1;

import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.social.support.ClientHttpRequestFactorySelector;
import org.springframework.web.client.RestTemplate;

/**
 * Base class for OAuth 1-based provider API templates.
 * @author Craig Walls
 */
public abstract class AbstractOAuth1ApiTemplate {

	private final OAuth1Credentials credentials;

	private final RestTemplate restTemplate;

	/**
	 * Constructs the API template without user authorization. This is useful for accessing operations on a provider's API that do not require user authorization.
	 */
	protected AbstractOAuth1ApiTemplate() {
		credentials = null;
		restTemplate = new RestTemplate(ClientHttpRequestFactorySelector.getRequestFactory());
	}
	
	/**
	 * Constructs the API template with OAuth credentials necessary to perform operations on behalf of a user.
	 * @param consumerKey the application's consumer key
	 * @param consumerSecret the application's consumer secret
	 * @param accessToken the access token
	 * @param accessTokenSecret the access token secret
	 */
	protected AbstractOAuth1ApiTemplate(String consumerKey, String consumerSecret, String accessToken, String accessTokenSecret) {
		credentials = new OAuth1Credentials(consumerKey, consumerSecret, accessToken, accessTokenSecret);
		restTemplate = ProtectedResourceClientFactory.create(credentials);
	}
	
	/**
	 * Set the ClientHttpRequestFactory. This is useful when custom configuration of the request factory is required, such as configuring proxy server details.
	 * @param requestFactory the request factory
	 */
	public void setRequestFactory(ClientHttpRequestFactory requestFactory) {
		if (isAuthorizedForUser()) {
			restTemplate.setRequestFactory(ProtectedResourceClientFactory.addOAuthSigning(requestFactory, credentials));
		} else {
			restTemplate.setRequestFactory(requestFactory);
		}
	}
	
	public boolean isAuthorizedForUser() {
		return credentials != null;
	}
	
	public RestTemplate getRestTemplate() {
		return restTemplate;
	}

}
