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
package org.springframework.social.facebook.connect;

import org.springframework.social.BadCredentialsException;
import org.springframework.social.connect.ApiAdapter;
import org.springframework.social.connect.ConnectionValues;
import org.springframework.social.connect.UserProfile;
import org.springframework.social.connect.UserProfileBuilder;
import org.springframework.social.facebook.api.FacebookApi;
import org.springframework.social.facebook.api.FacebookProfile;

public class FacebookServiceApiAdapter implements ApiAdapter<FacebookApi> {

	public boolean test(FacebookApi serviceApi) {
		try {
			serviceApi.userOperations().getUserProfile();
			return true;
		} catch (BadCredentialsException e) {
			return false;
		}
	}

	public void setConnectionValues(FacebookApi serviceApi, ConnectionValues values) {
		FacebookProfile profile = serviceApi.userOperations().getUserProfile();
		values.setProviderUserId(profile.getId());
		values.setDisplayName(profile.getUsername());
		values.setProfileUrl("http://facebook.com/#!/profile.php?id=" + profile.getId());
		values.setImageUrl("http://graph.facebook.com/" + profile.getId() + "/picture");
	}

	public UserProfile fetchUserProfile(FacebookApi serviceApi) {
		FacebookProfile profile = serviceApi.userOperations().getUserProfile();
		return new UserProfileBuilder().setName(profile.getName()).setFirstName(profile.getFirstName()).setLastName(profile.getLastName()).
			setEmail(profile.getEmail()).setUsername(profile.getUsername()).build();
	}
	
	public void updateStatus(FacebookApi serviceApi, String message) {
		serviceApi.feedOperations().updateStatus(message);
	}

}
