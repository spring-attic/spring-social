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
import org.springframework.social.connect.UserProfile;
import org.springframework.social.twitter.api.Twitter;
import org.springframework.social.twitter.api.TwitterProfile;
import org.springframework.social.twitter.api.UserOperations;

public class TwitterAdapterTest {

	private TwitterAdapter apiAdapter = new TwitterAdapter();
	
	private Twitter twitter = Mockito.mock(Twitter.class);
	
	@Test
	public void fetchProfile() {
		UserOperations userOperations = Mockito.mock(UserOperations.class);
		Mockito.when(twitter.userOperations()).thenReturn(userOperations);
		Mockito.when(userOperations.getUserProfile()).thenReturn(new TwitterProfile(123L, "kdonald", "Keith Donald", "http://twitter.com/kdonald", "http://twitter.com/kdonald/picture", "me", "melbourne, fl", new Date()));
		UserProfile profile = apiAdapter.fetchUserProfile(twitter);
		assertEquals("Keith Donald", profile.getName());
		assertEquals("Keith", profile.getFirstName());
		assertEquals("Donald", profile.getLastName());
		assertNull(profile.getEmail());
		assertEquals("kdonald", profile.getUsername());
	}

	@Test
	public void fetchProfileFirstNameOnly() {
		UserOperations userOperations = Mockito.mock(UserOperations.class);
		Mockito.when(twitter.userOperations()).thenReturn(userOperations);
		Mockito.when(userOperations.getUserProfile()).thenReturn(new TwitterProfile(123L, "kdonald", "Keith", "http://twitter.com/kdonald", "http://twitter.com/kdonald/picture", "me", "melbourne, fl", new Date()));
		UserProfile profile = apiAdapter.fetchUserProfile(twitter);
		assertEquals("Keith", profile.getName());
		assertEquals("Keith", profile.getFirstName());
		assertNull(profile.getLastName());
		assertNull(profile.getEmail());
		assertEquals("kdonald", profile.getUsername());
	}

	@Test
	public void fetchProfileMiddleName() {
		UserOperations userOperations = Mockito.mock(UserOperations.class);
		Mockito.when(twitter.userOperations()).thenReturn(userOperations);
		Mockito.when(userOperations.getUserProfile()).thenReturn(new TwitterProfile(123L, "kdonald", "Keith Preston Donald", "http://twitter.com/kdonald", "http://twitter.com/kdonald/picture", "me", "melbourne, fl", new Date()));
		UserProfile profile = apiAdapter.fetchUserProfile(twitter);
		assertEquals("Keith Preston Donald", profile.getName());
		assertEquals("Keith", profile.getFirstName());
		assertEquals("Donald", profile.getLastName());
		assertNull(profile.getEmail());
		assertEquals("kdonald", profile.getUsername());
	}
	
	@Test
	public void fetchProfileExtraWhitespace() {
		UserOperations userOperations = Mockito.mock(UserOperations.class);
		Mockito.when(twitter.userOperations()).thenReturn(userOperations);
		Mockito.when(userOperations.getUserProfile()).thenReturn(new TwitterProfile(123L, "kdonald", "Keith 	Preston  Donald", "http://twitter.com/kdonald", "http://twitter.com/kdonald/picture", "me", "melbourne, fl", new Date()));
		UserProfile profile = apiAdapter.fetchUserProfile(twitter);
		assertEquals("Keith 	Preston  Donald", profile.getName());
		assertEquals("Keith", profile.getFirstName());
		assertEquals("Donald", profile.getLastName());
		assertNull(profile.getEmail());
		assertEquals("kdonald", profile.getUsername());
	}
	
}
