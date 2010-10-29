/*
 * Copyright 2010 the original author or authors.
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
package org.springframework.social.facebook;

import static java.util.Collections.*;
import static org.junit.Assert.*;
import static org.junit.internal.matchers.IsCollectionContaining.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.social.facebook.FacebookTemplate.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestOperations;

/**
 * @author Craig Walls
 */
public class FacebookTemplateTest {
	private static final String ACCESS_TOKEN = "someAccessToken";
	private FacebookTemplate facebook;
	private RestOperations restOperations;

	@Before
	public void setup() {
		facebook = new FacebookTemplate(ACCESS_TOKEN);
		restOperations = mock(RestOperations.class);
		facebook.restOperations = restOperations;
	}

	@Test
	public void getFriendIds() {
		Map<String, List<Map<String, String>>> resultsMap = new HashMap<String, List<Map<String, String>>>();
		List<Map<String, String>> friendsList = new ArrayList<Map<String, String>>();
		friendsList.add(singletonMap("id", "12345"));
		friendsList.add(singletonMap("id", "67890"));
		friendsList.add(singletonMap("id", "24680"));
		resultsMap.put("data", friendsList);

		ResponseEntity<Map> response = new ResponseEntity<Map>(resultsMap, OK);
		when(restOperations.getForEntity(eq(CONNECTION_URL), eq(Map.class), eq(CURRENT_USER), eq(FRIENDS),
						eq(ACCESS_TOKEN))).thenReturn(response);

		List<String> friendIds = facebook.getFriendIds();
		assertEquals(3, friendIds.size());
		assertThat(friendIds, hasItem("12345"));
		assertThat(friendIds, hasItem("67890"));
		assertThat(friendIds, hasItem("24680"));
	}

	@Test
	public void getUserProfile() {
		FacebookProfile fbProfile = setupRestOperationsForGettingProfile();
		FacebookProfile actual = facebook.getUserProfile();
		assertEquals("Craig", actual.getFirstName());
		assertEquals("Walls", actual.getLastName());
		assertEquals("Craig Walls", actual.getName());
		assertEquals("cwalls@vmware.com", actual.getEmail());
		assertEquals(12345L, actual.getId());
	}

	@Test
	public void getProfileId() {
		setupRestOperationsForGettingProfile();
		assertEquals("12345", facebook.getProfileId());
	}

	@Test
	public void getProfileUrl() {
		setupRestOperationsForGettingProfile();
		assertEquals("http://www.facebook.com/profile.php?id=12345", facebook.getProfileUrl());
	}

	@Test
	public void getProfilePicture() {
		byte[] imageBytes = new byte[] { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 };
		ResponseEntity<byte[]> response = new ResponseEntity<byte[]>(imageBytes, OK);
		when(
				restOperations.getForEntity(eq(PROFILE_LARGE_PICTURE_URL), eq(byte[].class), eq(CURRENT_USER),
						eq(ACCESS_TOKEN)))
				.thenReturn(response);

		byte[] profilePicture = facebook.getProfilePicture();
		assertEquals(imageBytes, profilePicture);
	}

	@Test
	public void getProfilePicture_anotherUser() {
		byte[] imageBytes = new byte[] { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 };
		ResponseEntity<byte[]> response = new ResponseEntity<byte[]>(imageBytes, OK);
		when(restOperations.getForEntity(eq(PROFILE_LARGE_PICTURE_URL), eq(byte[].class), eq("54321"),
						eq(ACCESS_TOKEN))).thenReturn(response);

		byte[] profilePicture = facebook.getProfilePicture("54321");
		assertEquals(imageBytes, profilePicture);
	}

	@Test
	public void updateStatus() {
		facebook.updateStatus("Hello Facebook!");

		MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
		map.set("message", "Hello Facebook!");
		verify(restOperations).postForLocation(eq(CONNECTION_URL), eq(map), eq(CURRENT_USER), eq(FEED),
				eq(ACCESS_TOKEN));
	}

	@Test
	public void updateStatus_withLink() {
		String linkUrl = "http://www.springsource.com";
		String linkName = "SpringSource";
		String linkCaption = "SpringSource Home Page";
		String linkDescription = "SpringSource is the leader in Java application and infrastructure management.";
		facebook.updateStatus("Hello Facebook!", new FacebookLink(linkUrl, linkName, linkCaption, linkDescription));

		MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
		map.set("message", "Hello Facebook!");
		map.set("link", linkUrl);
		map.set("name", linkName);
		map.set("caption", linkCaption);
		map.set("description", linkDescription);
		verify(restOperations).postForLocation(eq(CONNECTION_URL), eq(map), eq(CURRENT_USER), eq(FEED),
				eq(ACCESS_TOKEN));
	}

	private FacebookProfile setupRestOperationsForGettingProfile() {
		FacebookProfile fbProfile = new FacebookProfile();
		fbProfile.firstName = "Craig";
		fbProfile.lastName = "Walls";
		fbProfile.name = "Craig Walls";
		fbProfile.email = "cwalls@vmware.com";
		fbProfile.id = 12345L;
		when(restOperations.getForObject(eq(OBJECT_URL + "?access_token={accessToken}"), eq(FacebookProfile.class),
						eq("me"), eq(ACCESS_TOKEN))).thenReturn(fbProfile);
		return fbProfile;
	}


}
