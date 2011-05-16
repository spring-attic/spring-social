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
package org.springframework.social.gowalla.connect;

import org.springframework.social.connect.ApiAdapter;
import org.springframework.social.connect.ConnectionValues;
import org.springframework.social.connect.UserProfile;
import org.springframework.social.connect.UserProfileBuilder;
import org.springframework.social.gowalla.api.Gowalla;
import org.springframework.social.gowalla.api.GowallaProfile;
import org.springframework.web.client.HttpClientErrorException;

/**
 * Gowalla ApiAdapter implementation.
 * @author Keith Donald
 */
public class GowallaAdapter implements ApiAdapter<Gowalla> {

	public boolean test(Gowalla gowalla) {
		try {
			gowalla.getUserProfile();
			return true;
		} catch (HttpClientErrorException e) {
			// TODO : Beef up Gowalla's error handling and trigger off of a more specific exception
			return false;
		}
	}

	public void setConnectionValues(Gowalla gowalla, ConnectionValues values) {
		GowallaProfile profile = gowalla.getUserProfile();
		values.setProviderUserId(profile.getId());
		values.setDisplayName(profile.getFirstName() + " " + profile.getLastName());
		values.setProfileUrl(gowalla.getProfileUrl());
		values.setImageUrl(profile.getProfileImageUrl());
	}

	public UserProfile fetchUserProfile(Gowalla gowalla) {
		GowallaProfile profile = gowalla.getUserProfile();
		return new UserProfileBuilder().setName(profile.getFirstName() + " " + profile.getLastName()).setUsername(profile.getId()).build();
	}
	
	public void updateStatus(Gowalla gowalla, String message) {
		// not supported
	}

}
