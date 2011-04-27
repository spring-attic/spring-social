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
package org.springframework.social.github.connect;

import org.springframework.social.connect.ApiAdapter;
import org.springframework.social.connect.ConnectionValues;
import org.springframework.social.connect.UserProfile;
import org.springframework.social.connect.UserProfileBuilder;
import org.springframework.social.github.api.GitHubApi;
import org.springframework.social.github.api.GitHubUserProfile;
import org.springframework.web.client.HttpClientErrorException;

/**
 * Github ApiAdapter implementation.
 * @author Keith Donald
 */
public class GitHubApiAdapter implements ApiAdapter<GitHubApi> {

	public boolean test(GitHubApi api) {
		try {
			api.getUserProfile();
			return true;
		} catch (HttpClientErrorException e) {
			// TODO : Beef up GitHub's error handling and trigger off of a more specific exception
			return false;
		}
	}

	public void setConnectionValues(GitHubApi api, ConnectionValues values) {
		GitHubUserProfile profile = api.getUserProfile();
		values.setProviderUserId(String.valueOf(profile.getId()));		
		values.setDisplayName(profile.getUsername());
		values.setProfileUrl("https://github.com/" + profile.getId());
		values.setImageUrl(profile.getProfileImageUrl());
	}

	public UserProfile fetchUserProfile(GitHubApi api) {
		GitHubUserProfile profile = api.getUserProfile();
		return new UserProfileBuilder().setName(profile.getName()).setEmail(profile.getEmail()).setUsername(profile.getUsername()).build();
	}
	
	public void updateStatus(GitHubApi api, String message) {
		// not supported
	}
	
}
