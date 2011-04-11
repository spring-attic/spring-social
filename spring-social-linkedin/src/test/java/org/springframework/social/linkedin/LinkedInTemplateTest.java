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
package org.springframework.social.linkedin;

import static org.junit.Assert.*;
import static org.springframework.http.HttpMethod.*;
import static org.springframework.social.test.client.RequestMatchers.*;
import static org.springframework.social.test.client.ResponseCreators.*;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.social.test.client.MockRestServiceServer;

/**
 * @author Craig Walls
 */
public class LinkedInTemplateTest {

	private LinkedInTemplate linkedIn;
	private MockRestServiceServer mockServer;
	private HttpHeaders responseHeaders;

	@Before
	public void setup() {
		linkedIn = new LinkedInTemplate("API_KEY", "API_SECRET", "ACCESS_TOKEN", "ACCESS_TOKEN_SECRET");
		mockServer = MockRestServiceServer.createServer(linkedIn.getRestTemplate());
		responseHeaders = new HttpHeaders();
		responseHeaders.setContentType(MediaType.APPLICATION_XML);
	}

	@Test
	public void getUserProfile() {
		mockServer.expect(requestTo("https://api.linkedin.com/v1/people/~:public")).andExpect(method(GET))
				.andRespond(withResponse(new ClassPathResource("profile.xml", getClass()), responseHeaders));
		LinkedInProfile profile = linkedIn.getUserProfile();
		assertEquals("z37f0n3A05", profile.getId());
		assertEquals("Just a guy", profile.getHeadline());
		assertEquals("Craig", profile.getFirstName());
		assertEquals("Walls", profile.getLastName());
		assertEquals("Computer Software", profile.getIndustry());
		assertEquals("http://www.linkedin.com/in/habuma", profile.getPublicProfileUrl());
		assertEquals("http://www.linkedin.com/standardProfileUrl", profile.getStandardProfileUrl());
		assertEquals("http://media.linkedin.com/pictureUrl", profile.getProfilePictureUrl());
	}

	@Test
	public void getProfileId() {
		mockServer.expect(requestTo("https://api.linkedin.com/v1/people/~:public")).andExpect(method(GET))
				.andRespond(withResponse(new ClassPathResource("profile.xml", getClass()), responseHeaders));
		assertEquals("z37f0n3A05", linkedIn.getProfileId());
	}

	@Test
	public void getProfileUrl() {
		mockServer.expect(requestTo("https://api.linkedin.com/v1/people/~:public")).andExpect(method(GET))
				.andRespond(withResponse(new ClassPathResource("profile.xml", getClass()), responseHeaders));
		assertEquals("http://www.linkedin.com/in/habuma", linkedIn.getProfileUrl());
	}

	@Test
	public void getConnections() {
		mockServer.expect(requestTo("https://api.linkedin.com/v1/people/~/connections")).andExpect(method(GET))
				.andRespond(withResponse(new ClassPathResource("connections.xml", getClass()), responseHeaders));
		List<LinkedInProfile> connections = linkedIn.getConnections();
		assertEquals(4, connections.size());
		assertProfile(connections.get(0), "kR0lnX1ll8", "SpringSource Cofounder", "Keith", "Donald", "Computer Software",
				"http://www.linkedin.com/profile?viewProfile=&key=2526541&authToken=61Sm&authType=name&trk=api*a121026*s129482*");
		assertProfile(connections.get(1), "VRcwcqPCtP", "GM, SpringSource and SVP, Middleware at VMware", "Rod",
				"Johnson",
				"Computer Software",
				"http://www.linkedin.com/profile?viewProfile=&key=210059&authToken=3hU1&authType=name&trk=api*a121026*s129482*");
		assertProfile(connections.get(2), "Ia7uR1OmDB", "Spring and AOP expert; author AspectJ in Action", "Ramnivas",
				"Laddad", "Computer Software",
				"http://www.linkedin.com/profile?viewProfile=&key=208994&authToken=P5K9&authType=name&trk=api*a121026*s129482*");
		assertProfile(connections.get(3), "gKEMq4CMdl", "Head of Groovy Development at SpringSource", "Guillaume",
				"Laforge", "Information Technology and Services",
				"http://www.linkedin.com/profile?viewProfile=&key=822306&authToken=YmIW&authType=name&trk=api*a121026*s129482*");
	}

	private void assertProfile(LinkedInProfile connection, String id, String headline, String firstName,
			String lastName, String industry, String standardUrl) {
		assertEquals(id, connection.getId());
		assertEquals(headline, connection.getHeadline());
		assertEquals(firstName, connection.getFirstName());
		assertEquals(lastName, connection.getLastName());
		assertEquals(industry, connection.getIndustry());
		assertEquals(standardUrl, connection.getStandardProfileUrl());
	}

}
