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
import org.springframework.social.support.ClientHttpRequestFactorySelector;
import org.springframework.web.client.RestTemplate;

/**
 * Base class for OAuth 2-based provider API bindings.
 * @author Craig Walls
 */
public abstract class AbstractOAuth2ApiTemplate {

	private final String accessToken;

	private final RestTemplate restTemplate;

	/**
	 * Constructs the API template without user authorization. This is useful for accessing operations on a provider's API that do not require user authorization.
	 */
	protected AbstractOAuth2ApiTemplate() {
		accessToken = null;
		restTemplate = new RestTemplate(ClientHttpRequestFactorySelector.getRequestFactory());
	}
	
	/**
	 * Constructs the API template with OAuth credentials necessary to perform operations on behalf of a user.
	 * @param accessToken the access token
	 */
	protected AbstractOAuth2ApiTemplate(String accessToken) {
		this.accessToken = accessToken;
		restTemplate = ProtectedResourceClientFactory.create(accessToken, getOAuth2Version());
	}
	
	/**
	 * Set the ClientHttpRequestFactory. This is useful when custom configuration of the request factory is required, such as configuring proxy server details.
	 * @param requestFactory the request factory
	 */
	public void setRequestFactory(ClientHttpRequestFactory requestFactory) {
		if (isAuthorizedForUser()) {
			restTemplate.setRequestFactory(ProtectedResourceClientFactory.addOAuthSigning(requestFactory, accessToken, getOAuth2Version()));
		} else {
			restTemplate.setRequestFactory(requestFactory);
		}
	}
	
	public boolean isAuthorizedForUser() {
		return accessToken != null;
	}
	
	public RestTemplate getRestTemplate() {
		return restTemplate;
	}
	
	protected OAuth2Version getOAuth2Version() {
		return OAuth2Version.STANDARD;
	}

}