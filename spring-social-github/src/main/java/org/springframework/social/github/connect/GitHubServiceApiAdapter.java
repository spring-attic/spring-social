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

import org.springframework.social.connect.ServiceApiAdapter;
import org.springframework.social.connect.ServiceProviderConnectionValues;
import org.springframework.social.connect.ServiceProviderUserProfile;
import org.springframework.social.connect.ServiceProviderUserProfileBuilder;
import org.springframework.social.github.api.GitHubApi;
import org.springframework.social.github.api.GitHubUserProfile;
import org.springframework.web.client.HttpClientErrorException;

public class GitHubServiceApiAdapter implements ServiceApiAdapter<GitHubApi> {

	public boolean test(GitHubApi serviceApi) {
		try {
			serviceApi.getUserProfile();
			return true;
		} catch (HttpClientErrorException e) {
			// TODO : Beef up GitHub's error handling and trigger off of a more specific exception
			return false;
		}
	}

	public void setConnectionValues(GitHubApi serviceApi, ServiceProviderConnectionValues values) {
		GitHubUserProfile userProfile = serviceApi.getUserProfile();
		values.setProviderUserId(String.valueOf(userProfile.getId()));		
		values.setDisplayName(userProfile.getUsername());
		values.setProfileUrl("https://github.com/" + userProfile.getId());
		values.setImageUrl(userProfile.getProfileImageUrl());
	}

	public ServiceProviderUserProfile fetchUserProfile(GitHubApi serviceApi) {
		GitHubUserProfile profile = serviceApi.getUserProfile();
		return new ServiceProviderUserProfileBuilder().setName(profile.getName()).setEmail(profile.getEmail()).setUsername(profile.getUsername()).build();
	}
	
	public void updateStatus(GitHubApi serviceApi, String message) {
		// not supported
	}
	
}
