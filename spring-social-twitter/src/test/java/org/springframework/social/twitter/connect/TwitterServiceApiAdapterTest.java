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

import static org.junit.Assert.*;

import java.util.Date;

import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.social.connect.ServiceProviderUserProfile;
import org.springframework.social.twitter.api.TwitterApi;
import org.springframework.social.twitter.api.TwitterProfile;
import org.springframework.social.twitter.api.UserOperations;

public class TwitterServiceApiAdapterTest {

	private TwitterServiceApiAdapter apiAdapter = new TwitterServiceApiAdapter();
	
	private TwitterApi serviceApi = Mockito.mock(TwitterApi.class);
	
	@Test
	public void fetchProfile() {
		UserOperations userOperations = Mockito.mock(UserOperations.class);
		Mockito.when(serviceApi.userOperations()).thenReturn(userOperations);
		Mockito.when(userOperations.getUserProfile()).thenReturn(new TwitterProfile(123L, "kdonald", "Keith Donald", "http://www.twitter.com/kdonald", "http://www.twitter.com/kdonald/picture", "me", "melbourne, fl", new Date()));
		ServiceProviderUserProfile profile = apiAdapter.fetchUserProfile(serviceApi);
		assertEquals("Keith Donald", profile.getName());
		assertEquals("Keith", profile.getFirstName());
		assertEquals("Donald", profile.getLastName());
		assertNull(profile.getEmail());
		assertEquals("kdonald", profile.getUsername());
	}

	@Test
	public void fetchProfileFirstNameOnly() {
		UserOperations userOperations = Mockito.mock(UserOperations.class);
		Mockito.when(serviceApi.userOperations()).thenReturn(userOperations);
		Mockito.when(userOperations.getUserProfile()).thenReturn(new TwitterProfile(123L, "kdonald", "Keith", "http://www.twitter.com/kdonald", "http://www.twitter.com/kdonald/picture", "me", "melbourne, fl", new Date()));
		ServiceProviderUserProfile profile = apiAdapter.fetchUserProfile(serviceApi);
		assertEquals("Keith", profile.getName());
		assertEquals("Keith", profile.getFirstName());
		assertNull(profile.getLastName());
		assertNull(profile.getEmail());
		assertEquals("kdonald", profile.getUsername());
	}

	@Test
	public void fetchProfileMiddleName() {
		UserOperations userOperations = Mockito.mock(UserOperations.class);
		Mockito.when(serviceApi.userOperations()).thenReturn(userOperations);
		Mockito.when(userOperations.getUserProfile()).thenReturn(new TwitterProfile(123L, "kdonald", "Keith Preston Donald", "http://www.twitter.com/kdonald", "http://www.twitter.com/kdonald/picture", "me", "melbourne, fl", new Date()));
		ServiceProviderUserProfile profile = apiAdapter.fetchUserProfile(serviceApi);
		assertEquals("Keith Preston Donald", profile.getName());
		assertEquals("Keith", profile.getFirstName());
		assertEquals("Donald", profile.getLastName());
		assertNull(profile.getEmail());
		assertEquals("kdonald", profile.getUsername());
	}
	
	@Test
	public void fetchProfileExtraWhitespace() {
		UserOperations userOperations = Mockito.mock(UserOperations.class);
		Mockito.when(serviceApi.userOperations()).thenReturn(userOperations);
		Mockito.when(userOperations.getUserProfile()).thenReturn(new TwitterProfile(123L, "kdonald", "Keith 	Preston  Donald", "http://www.twitter.com/kdonald", "http://www.twitter.com/kdonald/picture", "me", "melbourne, fl", new Date()));
		ServiceProviderUserProfile profile = apiAdapter.fetchUserProfile(serviceApi);
		assertEquals("Keith 	Preston  Donald", profile.getName());
		assertEquals("Keith", profile.getFirstName());
		assertEquals("Donald", profile.getLastName());
		assertNull(profile.getEmail());
		assertEquals("kdonald", profile.getUsername());
	}
	
}
