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
package org.springframework.social.twitter;

import static org.junit.Assert.*;
import static org.springframework.http.HttpMethod.*;
import static org.springframework.social.test.client.RequestMatchers.*;
import static org.springframework.social.test.client.ResponseCreators.*;

import java.util.List;

import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;


/**
 * @author Craig Walls
 */
public class UserApiTemplateTest extends AbstractTwitterApiTest {

	@Test
	public void getProfileId() {
		responseHeaders.setContentType(MediaType.APPLICATION_JSON);
		mockServer.expect(requestTo("https://api.twitter.com/1/account/verify_credentials.json"))
				.andExpect(method(GET))
				.andRespond(withResponse(new ClassPathResource("verify-credentials.json", getClass()), responseHeaders));
		assertEquals("habuma", twitter.userApi().getProfileId());
	}

	@Test
	public void getUserProfile() throws Exception {
		mockServer.expect(requestTo("https://api.twitter.com/1/account/verify_credentials.json"))
				.andExpect(method(GET))
				.andRespond(withResponse(new ClassPathResource("verify-credentials.json", getClass()), responseHeaders));
		mockServer.expect(requestTo("https://api.twitter.com/1/users/show.json?screen_name=habuma"))
				.andExpect(method(GET))
				.andRespond(withResponse(new ClassPathResource("twitter-profile.json", getClass()), responseHeaders));

		TwitterProfile profile = twitter.userApi().getUserProfile();
		assertEquals(12345, profile.getId());
		assertEquals("habuma", profile.getScreenName());
		assertEquals("Craig Walls", profile.getName());
		assertEquals("Spring Guy", profile.getDescription());
		assertEquals("Plano, TX", profile.getLocation());
		assertEquals("http://www.springsource.org", profile.getUrl());
		assertEquals("http://a3.twimg.com/profile_images/1205746571/me2_300.jpg", profile.getProfileImageUrl());
	}
	
	@Test
	public void getUserProfile_userId() throws Exception {
		mockServer.expect(requestTo("https://api.twitter.com/1/users/show.json?user_id=12345"))
				.andExpect(method(GET))
				.andRespond(withResponse(new ClassPathResource("twitter-profile.json", getClass()), responseHeaders));

		TwitterProfile profile = twitter.userApi().getUserProfile(12345);
		assertEquals(12345, profile.getId());
		assertEquals("habuma", profile.getScreenName());
		assertEquals("Craig Walls", profile.getName());
		assertEquals("Spring Guy", profile.getDescription());
		assertEquals("Plano, TX", profile.getLocation());
		assertEquals("http://www.springsource.org", profile.getUrl());
		assertEquals("http://a3.twimg.com/profile_images/1205746571/me2_300.jpg", profile.getProfileImageUrl());
	}
	
	@Test
	public void getUsers_byUserId() {
		mockServer.expect(requestTo("https://api.twitter.com/1/users/lookup.json?user_id=14846645,14718006"))
			.andExpect(method(GET))
			.andRespond(withResponse(new ClassPathResource("list-of-profiles.json", getClass()), responseHeaders));
		List<TwitterProfile> users = twitter.userApi().getUsers(14846645, 14718006);
		assertEquals(2, users.size());
		assertEquals("royclarkson", users.get(0).getScreenName());
		assertEquals("kdonald", users.get(1).getScreenName());
	}
	
	@Test
	public void getUsers_byScreenName() {
		mockServer.expect(requestTo("https://api.twitter.com/1/users/lookup.json?screen_name=royclarkson,kdonald"))
			.andExpect(method(GET))
			.andRespond(withResponse(new ClassPathResource("list-of-profiles.json", getClass()), responseHeaders));
		List<TwitterProfile> users = twitter.userApi().getUsers("royclarkson", "kdonald");
		assertEquals(2, users.size());
		assertEquals("royclarkson", users.get(0).getScreenName());
		assertEquals("kdonald", users.get(1).getScreenName());
	}
	
	@Test
	public void searchForUsers() {
		mockServer.expect(requestTo("https://api.twitter.com/1/users/search.json?q=some%20query"))
			.andExpect(method(GET))
			.andRespond(withResponse(new ClassPathResource("list-of-profiles.json", getClass()), responseHeaders));
		List<TwitterProfile> users = twitter.userApi().searchForUsers("some query");
		assertEquals(2, users.size());
		assertEquals("royclarkson", users.get(0).getScreenName());
		assertEquals("kdonald", users.get(1).getScreenName());
	}
	
	@Test
	public void getSuggestionCategories() {
		mockServer.expect(requestTo("https://api.twitter.com/1/users/suggestions.json"))
			.andExpect(method(GET))
			.andRespond(withResponse(new ClassPathResource("suggestion-categories.json", getClass()), responseHeaders));
		List<SuggestionCategory> categories = twitter.userApi().getSuggestionCategories();
		assertEquals(4, categories.size());
		assertEquals("Art & Design", categories.get(0).getName());
		assertEquals("art-design", categories.get(0).getSlug());
		assertEquals(56, categories.get(0).getSize());
		assertEquals("Books", categories.get(1).getName());
		assertEquals("books", categories.get(1).getSlug());
		assertEquals(72, categories.get(1).getSize());
		assertEquals("Business", categories.get(2).getName());
		assertEquals("business", categories.get(2).getSlug());
		assertEquals(65, categories.get(2).getSize());
		assertEquals("Twitter", categories.get(3).getName());
		assertEquals("twitter", categories.get(3).getSlug());
		assertEquals(16, categories.get(3).getSize());
	}
	
	@Test
	public void getSuggestions() {
		mockServer.expect(requestTo("https://api.twitter.com/1/users/suggestions/springsource.json"))
			.andExpect(method(GET))
			.andRespond(withResponse(new ClassPathResource("suggestions.json", getClass()), responseHeaders));

		List<TwitterProfile> users = twitter.userApi().getSuggestions("springsource");
		assertEquals(2, users.size());
		assertEquals("royclarkson", users.get(0).getScreenName());
		assertEquals("kdonald", users.get(1).getScreenName());
	}
}
