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
package org.springframework.social.github.api.impl;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

import org.springframework.social.github.api.GitHubApi;
import org.springframework.social.github.api.GitHubUserProfile;
import org.springframework.social.oauth2.AbstractOAuth2ApiTemplate;

/**
 * <p>
 * The central class for interacting with TripIt.
 * </p>
 * <p>
 * TripIt operations require OAuth 1 authentication. Therefore TripIt template
 * must be given the minimal amount of information required to sign requests to
 * the TripIt API with an OAuth <code>Authorization</code> header.
 * </p>
 * @author Craig Walls
 */
public class GitHubTemplate extends AbstractOAuth2ApiTemplate.Draft8ApiTemplate implements GitHubApi {

	/**
	 * Constructs a GitHubTemplate with the minimal amount of information
	 * required to sign requests with an OAuth <code>Authorization</code>
	 * header.
	 * 
	 * @param accessToken
	 *            An access token granted to the application after OAuth
	 *            authentication.
	 */
	public GitHubTemplate(String accessToken) {
		super(accessToken);
	}

	public String getProfileId() {
		return getUserProfile().getUsername();
	}

	@SuppressWarnings("unchecked")
	public GitHubUserProfile getUserProfile() {
		Map<String, ?> result = getRestTemplate().getForObject(PROFILE_URL, Map.class);
		Map<String, ?> user = (Map<String, String>) result.get("user");
		Long gitHubId = Long.valueOf(String.valueOf(user.get("id")));
		String username = String.valueOf(user.get("login"));
		String name = String.valueOf(user.get("name"));
		String location = user.get("location") != null ? String.valueOf(user.get("location")) : null;
		String company = user.get("company") != null ? String.valueOf(user.get("company")) : null;
		String blog = user.get("blog") != null ? String.valueOf(user.get("blog")) : null;
		String email = user.get("email") != null ? String.valueOf(user.get("email")) : null;
		Date createdDate = toDate(String.valueOf(user.get("created_at")), dateFormat);
		String gravatarId = (String) user.get("gravatar_id");
		String profileImageUrl = gravatarId != null ? "https://secure.gravatar.com/avatar/" + gravatarId : null;
		return new GitHubUserProfile(gitHubId, username, name, location, company, blog, email, profileImageUrl, createdDate);
	}

	public String getProfileUrl() {
		return "https://github.com/" + getProfileId();
	}
	
	// internal helpers

	private Date toDate(String dateString, DateFormat dateFormat) {
		try {
			return dateFormat.parse(dateString);
		} catch (ParseException e) {
			return null;
		}
	}

	private DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss Z", Locale.ENGLISH);

	static final String PROFILE_URL = "https://github.com/api/v2/json/user/show";
}
