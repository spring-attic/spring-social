/*
 * Copyright 2010 the original author or authors.
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
package org.springframework.social.linkedin;

import java.util.List;

import org.springframework.security.oauth.client.InterceptorCallingRestTemplate;
import org.springframework.security.oauth.client.oauth1.OAuth1ClientRequestInterceptor;
import org.springframework.security.oauth.client.oauth1.OAuthToken;
import org.springframework.web.client.RestOperations;

/**
 * <p>
 * This is the central class for interacting with LinkedIn.
 * </p>
 * 
 * <p>
 * Greenhouse operations require OAuth authentication with the server.
 * Therefore, LinkedInTemplate must be constructed with the minimal information
 * required to sign requests with and OAuth 1 Authorization header.
 * </p>
 * 
 * @author Craig Walls
 */
public class LinkedInTemplate implements LinkedInOperations {

	RestOperations restOperations;

	/**
	 * Creates a new LinkedInTemplate given the minimal amount of information
	 * needed to sign requests with OAuth 1 credentials.
	 * 
	 * @param apiKey
	 *            the application's API key
	 * @param apiSecret
	 *            the application's API secret
	 * @param accessToken
	 *            an access token acquired through OAuth authentication with
	 *            LinkedIn
	 * @param accessTokenSecret
	 *            an access token secret acquired through OAuth authentication
	 *            with LinkedIn
	 */
	public LinkedInTemplate(String apiKey, String apiSecret, String accessToken, String accessTokenSecret) {
		// RestTemplate restTemplate = new RestTemplate();
		// temporarily use InterceptorCallingRestTemplate instead of a regular
		// RestTemplate. This is to simulate the work that Arjen is doing for
		// SPR-7494. Once Arjen's finished, a regular RestTemplate should be
		// used with the interceptors registered appropriately.
		InterceptorCallingRestTemplate restTemplate = new InterceptorCallingRestTemplate();
		restTemplate.addInterceptor(new OAuth1ClientRequestInterceptor(apiKey, apiSecret, new OAuthToken(accessToken,
				accessTokenSecret)));
		this.restOperations = restTemplate;
	}

	public String getProfileId() {
		return getUserProfile().getId();
	}

	public String getProfileUrl() {
		return getUserProfile().getPublicProfileUrl();
	}

	public LinkedInProfile getUserProfile() {
		return restOperations.getForObject(GET_CURRENT_USER_INFO, LinkedInProfile.class);
	}

	public List<LinkedInProfile> getConnections() {
		LinkedInConnections connections = restOperations.getForObject(
				"http://api.linkedin.com/v1/people/~/connections", LinkedInConnections.class);
		return connections.getConnections();
	}

	static final String GET_CURRENT_USER_INFO = "https://api.linkedin.com/v1/people/~:public";

}
