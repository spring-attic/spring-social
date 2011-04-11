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

import org.springframework.social.connect.ServiceProviderUser;
import org.springframework.social.connect.spi.ServiceApiAdapter;
import org.springframework.social.linkedin.LinkedInApi;
import org.springframework.social.linkedin.LinkedInProfile;
import org.springframework.web.client.HttpClientErrorException;

public class LinkedInServiceApiAdapter implements ServiceApiAdapter<LinkedInApi> {

	public boolean test(LinkedInApi serviceApi) {
		try {
			serviceApi.getUserProfile();
			return true;
		} catch (HttpClientErrorException e) {
			// TODO: Have api throw more specific exception and trigger off of that.
			return false;
		}
	}

	public ServiceProviderUser getUser(LinkedInApi serviceApi) {
		LinkedInProfile profile = serviceApi.getUserProfile();
		String profileName = profile.getFirstName() + " " + profile.getLastName();
		return new ServiceProviderUser(profile.getId(), profileName, profile.getPublicProfileUrl(), profile.getProfilePictureUrl());
	}

	public void updateStatus(LinkedInApi serviceApi, String message) {
		// not supported yet
	}
	
}
