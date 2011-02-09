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

import static org.junit.Assert.*;
import static org.springframework.http.HttpMethod.*;
import static org.springframework.web.client.test.RequestMatchers.*;
import static org.springframework.web.client.test.ResponseCreators.*;

import java.net.URI;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.test.MockRestServiceServer;

/**
 * @author Craig Walls
 */
public class FacebookTemplateTest {
	
	private static final String ACCESS_TOKEN = "someAccessToken";
	
	private FacebookTemplate facebook;
	private MockRestServiceServer mockServer;
	private HttpHeaders responseHeaders;

	@Before
	public void setup() {
		facebook = new FacebookTemplate(ACCESS_TOKEN);
		mockServer = MockRestServiceServer.createServer(facebook.getRestTemplate());
		responseHeaders = new HttpHeaders();
		responseHeaders.setContentType(MediaType.APPLICATION_JSON);
	}

	// TODO complete with testing of json response reading/request writing behavior
	// the use of a mock object before was not testing this important interaction
	
	@Test
	public void getFriendIds() {
		mockServer.expect(requestTo("https://graph.facebook.com/me/friends")).andExpect(method(GET))
				.andExpect(header("Authorization", "OAuth someAccessToken"))
				.andRespond(withResponse(new ClassPathResource("friends.json", getClass()), responseHeaders));

		List<String> friendIds = facebook.getFriendIds();
		assertEquals(3, friendIds.size());
		assertTrue(friendIds.contains("12345"));
		assertTrue(friendIds.contains("67890"));
		assertTrue(friendIds.contains("24680"));
	}

	@Test
	public void getUserProfile() {
		mockServer.expect(requestTo("https://graph.facebook.com/me")).andExpect(method(GET))
				.andExpect(header("Authorization", "OAuth someAccessToken"))
				.andRespond(withResponse(new ClassPathResource("me.json", getClass()), responseHeaders));

		FacebookProfile profile = facebook.getUserProfile();
		assertEquals(123456789, profile.getId());
		assertEquals("Craig", profile.getFirstName());
		assertEquals("Walls", profile.getLastName());
		assertEquals("Craig Walls", profile.getName());
		assertEquals("cwalls@vmware.com", profile.getEmail());
	}

	@Test
	public void getProfileId() {
		mockServer.expect(requestTo("https://graph.facebook.com/me")).andExpect(method(GET))
				.andExpect(header("Authorization", "OAuth someAccessToken"))
				.andRespond(withResponse(new ClassPathResource("me.json", getClass()), responseHeaders));

		assertEquals("123456789", facebook.getProfileId());
	}

	@Test
	public void getProfileUrl() {
		mockServer.expect(requestTo("https://graph.facebook.com/me")).andExpect(method(GET))
				.andExpect(header("Authorization", "OAuth someAccessToken"))
				.andRespond(withResponse(new ClassPathResource("me.json", getClass()), responseHeaders));

		assertEquals("http://www.facebook.com/profile.php?id=123456789", facebook.getProfileUrl());
	}

	@Test
	public void updateStatus() throws Exception {
		responseHeaders.setLocation(new URI("http://www.facebook.com/me"));
		String requestBody = "message=Hello+Facebook+World";
		mockServer.expect(requestTo("https://graph.facebook.com/me/feed")).andExpect(method(POST))
				.andExpect(header("Authorization", "OAuth someAccessToken"))
				.andExpect(body(requestBody)).andRespond(withResponse("", responseHeaders));
		facebook.updateStatus("Hello Facebook World");
		mockServer.verify();
	}

	@Test
	public void updateStatus_withLink() throws Exception {
		responseHeaders.setLocation(new URI("http://www.facebook.com/me"));
		String requestBody = "link=someLink&name=some+name&caption=some+caption&description=some+description&message=Hello+Facebook+World";
		mockServer.expect(requestTo("https://graph.facebook.com/me/feed")).andExpect(method(POST))
				.andExpect(header("Authorization", "OAuth someAccessToken"))
				.andExpect(body(requestBody)).andRespond(withResponse("", responseHeaders));
		FacebookLink link = new FacebookLink("someLink", "some name", "some caption", "some description");
		facebook.updateStatus("Hello Facebook World", link);
		mockServer.verify();
	}
	
}
