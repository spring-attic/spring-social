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
import static org.springframework.social.test.client.RequestMatchers.*;
import static org.springframework.social.test.client.ResponseCreators.*;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.junit.Before;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.social.test.client.MockRestServiceServer;

/**
 * @author Craig Walls
 */
public class UserApiImplTest {
	
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
	
	@Test
	public void getUserProfile_authenticatedUser() {
		mockServer.expect(requestTo("https://graph.facebook.com/me"))
				.andExpect(method(GET))
				.andExpect(header("Authorization", "OAuth someAccessToken"))
				.andRespond(withResponse(new ClassPathResource("full-profile.json", getClass()), responseHeaders));

		FacebookProfile profile = facebook.userApi().getUserProfile();
		assertBasicProfileData(profile);
		assertEquals("cwalls@vmware.com", profile.getEmail());
		assertEquals("http://www.facebook.com/habuma", profile.getLink());
		assertEquals("xyz123abc987", profile.getThirdPartyId());
		assertEquals(Integer.valueOf(-6), profile.getTimezone());
		assertEquals(toDate("2010-08-22T00:01:59+0000"), profile.getUpdatedTime());
		assertTrue(profile.isVerified());
		assertEquals("Just some dude", profile.getAbout());
		assertEquals("I was born at a very early age.", profile.getBio());
		assertEquals("06/09/1971", profile.getBirthday());
		assertEquals("111762725508574", profile.getLocation().getId());
		assertEquals("Dallas, Texas", profile.getLocation().getName());
		assertEquals("107925612568471", profile.getHometown().getId());
		assertEquals("Plano, Texas", profile.getHometown().getName());
		assertEquals(1, profile.getInterestedIn().size());
		assertEquals("female", profile.getInterestedIn().get(0));
		assertEquals("Jedi", profile.getReligion());
		assertEquals("Galactic Republic", profile.getPolitical());
		assertEquals("\"May the force be with you.\" - Common Jedi greeting", profile.getQuotes());
		assertEquals("Married", profile.getRelationshipStatus());
		assertEquals("533477039", profile.getSignificantOther().getId());
		assertEquals("Raymie Walls", profile.getSignificantOther().getName());
		assertEquals("http://www.habuma.com", profile.getWebsite());
		assertWorkHistory(profile.getWork());
		assertEducationHistory(profile.getEducation());
	}

	@Test
	public void getUserProfile_specificUserByUserId() {
		mockServer.expect(requestTo("https://graph.facebook.com/123456789"))
				.andExpect(method(GET))
				.andExpect(header("Authorization", "OAuth someAccessToken"))
				.andRespond(withResponse(new ClassPathResource("minimal-profile.json", getClass()), responseHeaders));

		FacebookProfile profile = facebook.userApi().getUserProfile(123456789);
		assertBasicProfileData(profile);
	}
	
	@Test
	public void getUserProfile_specificUserByUsername() {
		mockServer.expect(requestTo("https://graph.facebook.com/habuma"))
				.andExpect(method(GET))
				.andExpect(header("Authorization", "OAuth someAccessToken"))
				.andRespond(withResponse(new ClassPathResource("minimal-profile.json", getClass()), responseHeaders));

		FacebookProfile profile = facebook.userApi().getUserProfile("habuma");
		assertBasicProfileData(profile);
	}

	@Test
	public void getLikes() {
		mockServer.expect(requestTo("https://graph.facebook.com/me/likes"))
			.andExpect(method(GET))
			.andExpect(header("Authorization", "OAuth someAccessToken"))
			.andRespond(withResponse(new ClassPathResource("user-likes.json", getClass()), responseHeaders));
		List<UserLike> likes = facebook.userApi().getLikes();
		assertEquals(3, likes.size());
		UserLike like1 = likes.get(0);
		assertEquals("113294925350820", like1.getId());
		assertEquals("Pirates of the Caribbean", like1.getName());
		assertEquals("Movie", like1.getCategory());
		UserLike like2 = likes.get(1);
		assertEquals("38073733123", like2.getId());
		assertEquals("Dublin Dr Pepper", like2.getName());
		assertEquals("Company", like2.getCategory());
		UserLike like3 = likes.get(2);
		assertEquals("10264922373", like3.getId());
		assertEquals("Freebirds World Burrito", like3.getName());
		assertEquals("Restaurant/cafe", like3.getCategory());
	}

	private void assertBasicProfileData(FacebookProfile profile) {
		assertEquals(123456789, profile.getId());
		assertEquals("Craig", profile.getFirstName());
		assertEquals("Walls", profile.getLastName());
		assertEquals("Craig Walls", profile.getName());
		assertEquals(new Locale("en_us"), profile.getLocale());
		assertEquals("male", profile.getGender());
	}

	private void assertEducationHistory(List<EducationEntry> educationHistory) {
		assertEquals(2, educationHistory.size());
		assertEquals("College", educationHistory.get(0).getType());
		assertEquals("103768553006294", educationHistory.get(0).getSchool().getId());
		assertEquals("New Mexico", educationHistory.get(0).getSchool().getName());
		assertEquals("117348274968344", educationHistory.get(0).getYear().getId());
		assertEquals("1994", educationHistory.get(0).getYear().getName());
		assertEquals("High School", educationHistory.get(1).getType());
		assertEquals("115157218496067", educationHistory.get(1).getSchool().getId());
		assertEquals("Jal High School", educationHistory.get(1).getSchool().getName());
		assertEquals("127132740657422", educationHistory.get(1).getYear().getId());
		assertEquals("1989", educationHistory.get(1).getYear().getName());
	}

	private void assertWorkHistory(List<WorkEntry> workHistory) {
		assertEquals(2, workHistory.size());
		assertEquals("119387448093014", workHistory.get(0).getEmployer().getId());
		assertEquals("SpringSource", workHistory.get(0).getEmployer().getName());
		assertEquals("0000-00", workHistory.get(0).getStartDate());
		assertEquals("0000-00", workHistory.get(0).getEndDate());
		assertEquals("298846151879", workHistory.get(1).getEmployer().getId());
		assertEquals("Improving", workHistory.get(1).getEmployer().getName());
		assertEquals("2009-03", workHistory.get(1).getStartDate());
		assertEquals("2010-05", workHistory.get(1).getEndDate());
	}

	private static final DateFormat FB_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.ENGLISH);

	private Date toDate(String dateString) {
		try {
			return FB_DATE_FORMAT.parse(dateString);
		} catch (ParseException e) {
			return null;
		}
	}

}
