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
package org.springframework.social.tripit.connect;

import org.springframework.social.connect.ApiAdapter;
import org.springframework.social.connect.ConnectionValues;
import org.springframework.social.connect.UserProfile;
import org.springframework.social.connect.UserProfileBuilder;
import org.springframework.social.tripit.api.TripItApi;
import org.springframework.social.tripit.api.TripItProfile;
import org.springframework.web.client.HttpClientErrorException;

public class TripItServiceApiAdapter implements ApiAdapter<TripItApi> {

	public boolean test(TripItApi serviceApi) {
		try {
			serviceApi.getUserProfile();
			return true;
		} catch (HttpClientErrorException e) { 
			// TODO: Have api throw more specific exception and trigger off of that.
			return false;
		}
	}

	public void setConnectionValues(TripItApi serviceApi, ConnectionValues values) {
		TripItProfile profile = serviceApi.getUserProfile();
		values.setProviderUserId(profile.getId());
		values.setDisplayName(profile.getScreenName());
		values.setProfileUrl(profile.getProfileUrl());
		values.setImageUrl(profile.getProfileImageUrl());
	}

	public UserProfile fetchUserProfile(TripItApi serviceApi) {
		TripItProfile profile = serviceApi.getUserProfile();
		return new UserProfileBuilder().setName(profile.getPublicDisplayName()).setEmail(profile.getEmailAddress()).setUsername(profile.getScreenName()).build();
	}
	
	public void updateStatus(TripItApi serviceApi, String message) {
		// not supported
	}

}
