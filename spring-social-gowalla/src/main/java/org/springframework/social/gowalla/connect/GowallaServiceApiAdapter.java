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

import org.springframework.social.connect.ServiceApiAdapter;
import org.springframework.social.connect.ServiceProviderConnectionValues;
import org.springframework.social.connect.ServiceProviderUserProfile;
import org.springframework.social.gowalla.GowallaApi;
import org.springframework.social.gowalla.GowallaProfile;
import org.springframework.web.client.HttpClientErrorException;

public class GowallaServiceApiAdapter implements ServiceApiAdapter<GowallaApi> {

	public boolean test(GowallaApi serviceApi) {
		try {
			serviceApi.getUserProfile();
			return true;
		} catch (HttpClientErrorException e) {
			// TODO : Beef up Gowalla's error handling and trigger off of a more specific exception
			return false;
		}
	}

	public ServiceProviderConnectionValues getConnectionValues(GowallaApi serviceApi) {
		GowallaProfile userProfile = serviceApi.getUserProfile();
		String displayName = userProfile.getFirstName() + " " + userProfile.getLastName();
		String profileUrl = serviceApi.getProfileUrl();
		return new ServiceProviderConnectionValues(userProfile.getId(), displayName, profileUrl, userProfile.getProfileImageUrl());
	}

	public ServiceProviderUserProfile fetchUserProfile(GowallaApi serviceApi) {
		GowallaProfile profile = serviceApi.getUserProfile();
		String fullName = profile.getFirstName() + " " + profile.getLastName();
		// Gowalla doesn't expose the user email via the API.
		return new ServiceProviderUserProfile(fullName, profile.getFirstName(), profile.getLastName(), null, profile.getId());
	}
	
	public void updateStatus(GowallaApi serviceApi, String message) {
		// not supported
	}

}
