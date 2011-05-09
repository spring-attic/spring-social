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
package org.springframework.social.linkedin.connect;

import org.springframework.social.connect.ApiAdapter;
import org.springframework.social.connect.ConnectionValues;
import org.springframework.social.connect.UserProfile;
import org.springframework.social.connect.UserProfileBuilder;
import org.springframework.social.linkedin.api.LinkedIn;
import org.springframework.social.linkedin.api.LinkedInProfile;
import org.springframework.web.client.HttpClientErrorException;

/**
 * LinkedIn ApiAdapter implementation.
 * @author Keith Donald
 */
public class LinkedInApiAdapter implements ApiAdapter<LinkedIn> {

	public boolean test(LinkedIn api) {
		try {
			api.getUserProfile();
			return true;
		} catch (HttpClientErrorException e) {
			// TODO: Have api throw more specific exception and trigger off of that.
			return false;
		}
	}

	public void setConnectionValues(LinkedIn api, ConnectionValues values) {
		LinkedInProfile profile = api.getUserProfile();
		values.setProviderUserId(profile.getId());
		values.setDisplayName(profile.getFirstName() + " " + profile.getLastName());
		values.setProfileUrl(profile.getPublicProfileUrl());
		values.setImageUrl(profile.getProfilePictureUrl());
	}

	public UserProfile fetchUserProfile(LinkedIn api) {
		LinkedInProfile profile = api.getUserProfile();
		return new UserProfileBuilder().setName(profile.getFirstName() + " " + profile.getLastName()).build();
	}
	
	public void updateStatus(LinkedIn api, String message) {
		// not supported yet
	}
	
}
