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

import static org.junit.Assert.*;

import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.social.connect.UserProfile;
import org.springframework.social.facebook.api.FacebookApi;
import org.springframework.social.facebook.api.FacebookProfile;
import org.springframework.social.facebook.api.UserOperations;

public class FacebookApiAdapterTest {

	private FacebookApiAdapter apiAdapter = new FacebookApiAdapter();
	
	private FacebookApi api = Mockito.mock(FacebookApi.class);
	
	@Test
	public void fetchProfile() {		
		UserOperations userOperations = Mockito.mock(UserOperations.class);
		Mockito.when(api.userOperations()).thenReturn(userOperations);
		Mockito.when(userOperations.getUserProfile()).thenReturn(new FacebookProfile("12345678", "habuma", "Craig Walls", "Craig", "Walls", null, null));
		UserProfile profile = apiAdapter.fetchUserProfile(api);
		assertEquals("Craig Walls", profile.getName());
		assertEquals("Craig", profile.getFirstName());
		assertEquals("Walls", profile.getLastName());
		assertNull(profile.getEmail());
		assertEquals("habuma", profile.getUsername());
	}
	
}
