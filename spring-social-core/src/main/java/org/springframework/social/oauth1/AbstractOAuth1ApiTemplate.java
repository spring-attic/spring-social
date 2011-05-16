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
	 * Set the ClientHttpRequestFactory. This is useful when custom configuration of the request factory is required, such as configuring custom SSL details.
	 * @param requestFactory the request factory
	 */
	public void setRequestFactory(ClientHttpRequestFactory requestFactory) {
		if (isAuthorizedForUser()) {
			restTemplate.setRequestFactory(ProtectedResourceClientFactory.addOAuthSigning(requestFactory, credentials));
		} else {
			restTemplate.setRequestFactory(requestFactory);
		}
	}
	
	/**
	 * Returns true if this API binding has been authorized on behalf of a specific user.
	 * If so, calls to the API are signed with the user's authorization credentials, indicating an application is invoking the API on a user's behalf.
	 * If not, API calls do not contain any user authorization information.
	 * Callers can use this status flag to determine if API operations requiring authorization can be invoked.
	 */
	public boolean isAuthorizedForUser() {
		return credentials != null;
	}
	
	/**
	 * Obtains a reference to the REST client backing this API binding and used to perform API calls.
	 * Callers may use the RestTemplate to invoke other API operations not yet modeled by the binding interface.
	 * Callers may also modify the configuration of the RestTemplate to support unit testing the API binding with a mock server in a test environment.
	 * During construction, subclasses may apply customizations to the RestTemplate needed to invoke a specific API.
	 * @see RestTemplate#setMessageConverters(java.util.List)
	 * @see RestTemplate#setErrorHandler(org.springframework.web.client.ResponseErrorHandler)
	 */
	public RestTemplate getRestTemplate() {
		return restTemplate;
	}

}