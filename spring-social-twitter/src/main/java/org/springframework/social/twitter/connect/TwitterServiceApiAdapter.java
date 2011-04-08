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
import org.springframework.social.connect.ServiceProviderUser;
import org.springframework.social.connect.spi.ServiceApiAdapter;
import org.springframework.social.twitter.TwitterApi;
import org.springframework.social.twitter.types.TwitterProfile;

public class TwitterServiceApiAdapter implements ServiceApiAdapter<TwitterApi> {

	public boolean test(TwitterApi serviceApi) {
		try {
			serviceApi.userOperations().getUserProfile();
			return true;
		} catch (BadCredentialsException e) {
			return false;
		}
	}

	public ServiceProviderUser getUser(TwitterApi serviceApi) {
		TwitterProfile profile = serviceApi.userOperations().getUserProfile();
		return new ServiceProviderUser(Long.toString(profile.getId()), profile.getScreenName(), profile.getProfileUrl(), profile.getProfileImageUrl());
	}

	public void updateStatus(TwitterApi serviceApi, String message) {
		serviceApi.timelineOperations().updateStatus(message);	
	}

}