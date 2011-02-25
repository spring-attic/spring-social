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
package org.springframework.social.tripit;

import static org.junit.Assert.*;
import static org.springframework.http.HttpMethod.*;
import static org.springframework.social.test.client.RequestMatchers.*;
import static org.springframework.social.test.client.ResponseCreators.*;

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
public class TripItTemplateTest {
	
	private TripItTemplate tripIt;
	private MockRestServiceServer mockServer;
	private HttpHeaders responseHeaders;
	
	@Before
	public void setup() {
		tripIt = new TripItTemplate("API_KEY", "API_SECRET", "ACCESS_TOKEN", "ACCESS_TOKEN_SECRET");
		mockServer = MockRestServiceServer.createServer(tripIt.getRestTemplate());
		responseHeaders = new HttpHeaders();
		responseHeaders.setContentType(MediaType.APPLICATION_JSON);
	}

	@Test
	public void getUserProfile() {
		mockServer.expect(requestTo("https://api.tripit.com/v1/get/profile?format=json")).andExpect(method(GET))
				.andRespond(withResponse(new ClassPathResource("profile.json", getClass()), responseHeaders));
		TripItProfile profile = tripIt.getUserProfile();
		assertEquals("123456", profile.getId());
		assertEquals("habuma", profile.getScreenName());
		assertEquals("Craig Walls", profile.getPublicDisplayName());
		assertEquals("Plano, TX", profile.getHomeCity());
		assertEquals("SpringSource", profile.getCompany());
		assertEquals("http://www.tripit.com/user/habuma", profile.getProfileUrl());
	}

	@Test
	public void getProfileId() {
		mockServer.expect(requestTo("https://api.tripit.com/v1/get/profile?format=json")).andExpect(method(GET))
				.andRespond(withResponse(new ClassPathResource("profile.json", getClass()), responseHeaders));
		assertEquals("123456", tripIt.getProfileId());
	}

	@Test
	public void getProfileUrl() {
		mockServer.expect(requestTo("https://api.tripit.com/v1/get/profile?format=json")).andExpect(method(GET))
				.andRespond(withResponse(new ClassPathResource("profile.json", getClass()), responseHeaders));
		assertEquals("http://www.tripit.com/user/habuma", tripIt.getProfileUrl());
	}

	@Test
	public void getTrips() {
		mockServer.expect(requestTo("https://api.tripit.com/v1/list/trip/traveler/true/past/false?format=json"))
				.andExpect(method(GET))
				.andRespond(withResponse(new ClassPathResource("trips.json", getClass()), responseHeaders));

		List<Trip> trips = tripIt.getUpcomingTrips();
		assertEquals(2, trips.size());
		Trip trip = trips.get(0);
		assertEquals(12736853, trip.getId());
		assertEquals("Minneapolis, MN, March 2011", trip.getDisplayName());
		assertEquals("Minneapolis, MN", trip.getPrimaryLocation());
		assertEquals("http://www.tripit.com/trip/show/id/12736853", trip.getTripUrl());
		assertDateEquals("2011-03-04", trip.getStartDate());
		assertDateEquals("2011-03-05", trip.getEndDate());
		trip = trips.get(1);
		assertEquals(12400396, trip.getId());
		assertEquals("Madison, WI, February 2011", trip.getDisplayName());
		assertEquals("Madison, WI", trip.getPrimaryLocation());
		assertEquals("http://www.tripit.com/trip/show/id/12400396", trip.getTripUrl());
		assertDateEquals("2011-02-25", trip.getStartDate());
		assertDateEquals("2011-02-27", trip.getEndDate());
	}

	private SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

	private void assertDateEquals(String expected, Date actual) {
		assertEquals(expected, dateFormatter.format(actual));
	}
}
