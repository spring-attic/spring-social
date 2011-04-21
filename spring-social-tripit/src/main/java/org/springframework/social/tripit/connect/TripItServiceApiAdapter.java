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

import org.springframework.social.connect.ServiceApiAdapter;
import org.springframework.social.connect.ServiceProviderConnectionValues;
import org.springframework.social.connect.ServiceProviderUserProfile;
import org.springframework.social.tripit.api.TripItApi;
import org.springframework.social.tripit.api.TripItProfile;
import org.springframework.web.client.HttpClientErrorException;

public class TripItServiceApiAdapter implements ServiceApiAdapter<TripItApi> {

	public boolean test(TripItApi serviceApi) {
		try {
			serviceApi.getUserProfile();
			return true;
		} catch (HttpClientErrorException e) { 
			// TODO: Have api throw more specific exception and trigger off of that.
			return false;
		}
	}

	public ServiceProviderConnectionValues getConnectionValues(TripItApi serviceApi) {
		TripItProfile userProfile = serviceApi.getUserProfile();
		String profileUrl = serviceApi.getProfileUrl();
		return new ServiceProviderConnectionValues(userProfile.getId(), userProfile.getScreenName(), profileUrl, userProfile.getProfileImageUrl());
	}

	public ServiceProviderUserProfile fetchUserProfile(TripItApi serviceApi) {
		TripItProfile profile = serviceApi.getUserProfile();
		String name = profile.getPublicDisplayName();
		String[] firstAndLastName = firstAndLastName(name);
		return new ServiceProviderUserProfile(profile.getPublicDisplayName(), firstAndLastName[0], firstAndLastName[1], profile.getEmailAddress(), profile.getScreenName());
	}
	
	public void updateStatus(TripItApi serviceApi, String message) {
		// not supported
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
