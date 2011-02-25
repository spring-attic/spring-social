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
package org.springframework.social.github;

import static org.junit.Assert.*;
import static org.springframework.http.HttpMethod.*;
import static org.springframework.social.test.client.RequestMatchers.*;
import static org.springframework.social.test.client.ResponseCreators.*;

import org.junit.Before;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.social.test.client.MockRestServiceServer;

/**
 * @author Craig Walls
 */
public class GitHubTemplateTest {

	private GitHubTemplate github;
	private MockRestServiceServer mockServer;
	private HttpHeaders responseHeaders;

	@Before
	public void setup() {
		github = new GitHubTemplate("ACCESS_TOKEN");
		mockServer = MockRestServiceServer.createServer(github.getRestTemplate());
		responseHeaders = new HttpHeaders();
		responseHeaders.setContentType(MediaType.APPLICATION_JSON);
	}

	@Test
	public void getUserProfile() throws Exception {
		mockServer.expect(requestTo("https://github.com/api/v2/json/user/show")).andExpect(method(GET))
				.andExpect(header("Authorization", "Token token=\"ACCESS_TOKEN\""))
				.andRespond(withResponse(new ClassPathResource("profile.json", getClass()), responseHeaders));
		GitHubUserProfile profile = github.getUserProfile();
		assertEquals("habuma", profile.getUsername());
		assertEquals("Craig Walls", profile.getName());
		assertEquals("SpringSource", profile.getCompany());
		assertEquals("http://blog.springsource.com/author/cwalls", profile.getBlog());
		assertEquals("cwalls@vmware.com", profile.getEmail());
		assertEquals(123456, profile.getId());
	}

	@Test
	public void getProfileId() {
		mockServer.expect(requestTo("https://github.com/api/v2/json/user/show")).andExpect(method(GET))
				.andExpect(header("Authorization", "Token token=\"ACCESS_TOKEN\""))
				.andRespond(withResponse(new ClassPathResource("profile.json", getClass()), responseHeaders));
		assertEquals("habuma", github.getProfileId());
	}

	@Test
	public void getProfileUrl() {
		mockServer.expect(requestTo("https://github.com/api/v2/json/user/show")).andExpect(method(GET))
				.andExpect(header("Authorization", "Token token=\"ACCESS_TOKEN\""))
				.andRespond(withResponse(new ClassPathResource("profile.json", getClass()), responseHeaders));
		assertEquals("https://github.com/habuma", github.getProfileUrl());
	}
}
