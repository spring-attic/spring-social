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
package org.springframework.social.linkedin.api.impl;

import java.util.List;

import org.springframework.social.linkedin.api.LinkedInApi;
import org.springframework.social.linkedin.api.LinkedInConnections;
import org.springframework.social.linkedin.api.LinkedInProfile;
import org.springframework.social.oauth1.ApiTemplate;

/**
 * This is the central class for interacting with LinkedIn.
 * <p>
 * Greenhouse operations require OAuth authentication with the server.
 * Therefore, LinkedInTemplate must be constructed with the minimal information
 * required to sign requests with and OAuth 1 Authorization header.
 * </p>
 * @author Craig Walls
 */
public class LinkedInTemplate extends ApiTemplate implements LinkedInApi {

	/**
	 * Creates a new LinkedInTemplate given the minimal amount of information needed to sign requests with OAuth 1 credentials.
	 * @param consumerKey the application's API key
	 * @param consumerSecret the application's API secret
	 * @param accessToken an access token acquired through OAuth authentication with LinkedIn
	 * @param accessTokenSecret an access token secret acquired through OAuth authentication with LinkedIn
	 */
	public LinkedInTemplate(String consumerKey, String consumerSecret, String accessToken, String accessTokenSecret) {
		super(consumerKey, consumerSecret, accessToken, accessTokenSecret);
	}
	
	public String getProfileId() {
		return getUserProfile().getId();
	}

	public String getProfileUrl() {
		return getUserProfile().getPublicProfileUrl();
	}

	public LinkedInProfile getUserProfile() {
		return getRestTemplate().getForObject(GET_CURRENT_USER_INFO, LinkedInProfile.class);
	}

	public List<LinkedInProfile> getConnections() {
		LinkedInConnections connections = getRestTemplate().getForObject("https://api.linkedin.com/v1/people/~/connections", LinkedInConnections.class);
		return connections.getConnections();
	}

	static final String GET_CURRENT_USER_INFO = "https://api.linkedin.com/v1/people/~:public";

}
