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
package org.springframework.social.twitter.connect;

import org.springframework.social.BadCredentialsException;
import org.springframework.social.connect.ServiceApiAdapter;
import org.springframework.social.connect.ServiceProviderConnectionValues;
import org.springframework.social.connect.ServiceProviderUserProfile;
import org.springframework.social.twitter.api.TwitterApi;
import org.springframework.social.twitter.api.TwitterProfile;

public class TwitterServiceApiAdapter implements ServiceApiAdapter<TwitterApi> {

	public boolean test(TwitterApi serviceApi) {
		try {
			serviceApi.userOperations().getUserProfile();
			return true;
		} catch (BadCredentialsException e) {
			return false;
		}
	}

	public ServiceProviderConnectionValues getConnectionValues(TwitterApi serviceApi) {
		TwitterProfile profile = serviceApi.userOperations().getUserProfile();
		return new ServiceProviderConnectionValues(Long.toString(profile.getId()), profile.getScreenName(), profile.getProfileUrl(), profile.getProfileImageUrl());
	}

	public ServiceProviderUserProfile fetchUserProfile(TwitterApi serviceApi) {
		TwitterProfile profile = serviceApi.userOperations().getUserProfile();
		String name = profile.getName();
		String[] firstAndLastName = firstAndLastName(name);
		return new ServiceProviderUserProfile(name, firstAndLastName[0], firstAndLastName[1], null, profile.getScreenName());
	}
	
	public void updateStatus(TwitterApi serviceApi, String message) {
		serviceApi.timelineOperations().updateStatus(message);	
	}
	
	// internal helpers
	
	private String[] firstAndLastName(String name) {
		if (name == null) {
			return EMPTY_FIRST_AND_LAST_NAME_ARRAY;
		}
		String[] nameParts = name.split("\\s+");
		if (nameParts.length == 1) {
			return new String[] { nameParts[0], null };
		} else {
			return new String[] { nameParts[0], nameParts[nameParts.length - 1] };
		}
	}
	
	private String[] EMPTY_FIRST_AND_LAST_NAME_ARRAY = new String[] { null, null };

}