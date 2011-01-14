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
package org.springframework.social.github;

import java.util.Map;

import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;

/**
 * <p>
 * The central class for interacting with TripIt.
 * </p>
 * 
 * <p>
 * TripIt operations require OAuth 1 authentication. Therefore TripIt template
 * must be given the minimal amount of information required to sign requests to
 * the TripIt API with an OAuth <code>Authorization</code> header.
 * </p>
 * 
 * @author Craig Walls
 */
public class GitHubTemplate implements GitHubOperations {
	RestOperations restOperations;
	private final String accessToken;

	/**
	 * Constructs a TripItTemplate with the minimal amount of information
	 * required to sign requests with an OAuth <code>Authorization</code>
	 * header.
	 * 
	 * @param apiKey
	 *            The application's API key as given by TripIt when registering
	 *            the application.
	 * @param apiSecret
	 *            The application's API secret as given by TripIt when
	 *            registering the application.
	 * @param accessToken
	 *            An access token granted to the application after OAuth
	 *            authentication.
	 * @param accessTokenSecret
	 *            An access token secret granted to the application after OAuth
	 *            authentication.
	 */
	public GitHubTemplate(String accessToken) {
		this.accessToken = accessToken;
		this.restOperations = new RestTemplate();
	}

	public String getProfileId() {
		return getUserProfile().getUsername();
	}

	@SuppressWarnings("unchecked")
	public GitHubUserProfile getUserProfile() {
		Map<String, ?> result = restOperations.getForObject(PROFILE_URL, Map.class, accessToken);
		Map<String, ?> user = (Map<String, String>) result.get("user");

		Long gitHubId = Long.valueOf(String.valueOf(user.get("id")));
		String username = String.valueOf(user.get("login"));
		String name = String.valueOf(user.get("name"));
		String company = user.get("company") != null ? String.valueOf(user.get("company")) : null;
		String blog = user.get("blog") != null ? String.valueOf(user.get("blog")) : null;
		String email = user.get("email") != null ? String.valueOf(user.get("email")) : null;

		return new GitHubUserProfile(gitHubId, username, name, company, blog, email);
	}

	public String getProfileUrl() {
		return "https://github.com/" + getProfileId();
	}
	
	static final String PROFILE_URL = "https://github.com/api/v2/json/user/show?access_token={accessToken}";
}
