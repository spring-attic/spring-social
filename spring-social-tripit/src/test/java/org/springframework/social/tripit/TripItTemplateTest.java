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
import static org.mockito.Mockito.*;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.springframework.web.client.RestOperations;

/**
 * @author Craig Walls
 */
public class TripItTemplateTest {
	private TripItTemplate tripIt;
	private RestOperations restOperations;

	@Before
	public void setup() {
		tripIt = new TripItTemplate("API_KEY", "API_SECRET", "ACCESS_TOKEN", "ACCESS_TOKEN_SECRET");
		restOperations = mock(RestOperations.class);
		tripIt.restOperations = restOperations;
	}

	@Test
	public void getUserProfile() {
		setupRestOperationsToTripItRetrieveProfile();

		TripItProfile userProfile = tripIt.getUserProfile();
		assertEquals("12345", userProfile.getId());
		assertEquals("Craig Walls", userProfile.getPublicDisplayName());
		assertEquals("habuma", userProfile.getScreenName());
		assertEquals("SpringSource", userProfile.getCompany());
		assertEquals("Plano, TX", userProfile.getHomeCity());
		assertEquals("http://www.tripit.com/habuma", userProfile.getProfileUrl());
	}

	@Test
	public void getProfileId() {
		setupRestOperationsToTripItRetrieveProfile();
		assertEquals("12345", tripIt.getProfileId());
	}

	@Test
	public void getProfileUrl() {
		setupRestOperationsToTripItRetrieveProfile();
		assertEquals("http://www.tripit.com/habuma", tripIt.getProfileUrl());
	}

	@Test
	public void getTrips_singleTrip() throws Exception {
		Map<String, Object> responseMap = new HashMap<String, Object>();
		Map<String, Object> tripMap = new HashMap<String, Object>();
		tripMap.put("id", 100);
		tripMap.put("display_name", "Trip to Chicago");
		tripMap.put("primary_location", "Chicago, IL");
		tripMap.put("start_date", "2010-10-19");
		tripMap.put("end_date", "2010-10-22");
		tripMap.put("relative_url", "/trips/100");
		responseMap.put("Trip", tripMap);
		when(
				restOperations.getForObject("https://api.tripit.com/v1/list/trip/traveler/true/past/false?format=json",
						Map.class)).thenReturn(responseMap);

		List<Trip> trips = tripIt.getUpcomingTrips();
		assertEquals(1, trips.size());
		Trip trip = trips.get(0);
		assertEquals(100, trip.getId());
		assertEquals("Trip to Chicago", trip.getDisplayName());
		assertEquals("Chicago, IL", trip.getPrimaryLocation());
		assertEquals("http://www.tripit.com/trips/100", trip.getTripUrl());
		SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
		assertEquals(dateFormatter.parse("2010-10-19"), trip.getStartDate());
		assertEquals(dateFormatter.parse("2010-10-22"), trip.getEndDate());
	}

	@Test
	public void getTrips_moreThanOneTrip() throws Exception {
		Map<String, Object> responseMap = new HashMap<String, Object>();
		List<Map<String, Object>> tripList = new ArrayList<Map<String, Object>>();
		Map<String, Object> tripMap = new HashMap<String, Object>();
		tripMap.put("id", 100);
		tripMap.put("display_name", "Trip to Chicago");
		tripMap.put("primary_location", "Chicago, IL");
		tripMap.put("start_date", "2010-10-19");
		tripMap.put("end_date", "2010-10-22");
		tripMap.put("relative_url", "/trips/100");
		tripList.add(tripMap);
		tripMap = new HashMap<String, Object>();
		tripMap.put("id", 200);
		tripMap.put("display_name", "Reston Trip");
		tripMap.put("primary_location", "Reston, VA");
		tripMap.put("start_date", "2010-11-05");
		tripMap.put("end_date", "2010-11-07");
		tripMap.put("relative_url", "/trips/200");
		tripList.add(tripMap);
		responseMap.put("Trip", tripList);
		when(
				restOperations.getForObject("https://api.tripit.com/v1/list/trip/traveler/true/past/false?format=json",
						Map.class)).thenReturn(responseMap);

		List<Trip> trips = tripIt.getUpcomingTrips();
		assertEquals(2, trips.size());
		Trip trip = trips.get(0);
		assertEquals(100, trip.getId());
		assertEquals("Trip to Chicago", trip.getDisplayName());
		assertEquals("Chicago, IL", trip.getPrimaryLocation());
		assertEquals("http://www.tripit.com/trips/100", trip.getTripUrl());
		SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
		assertEquals(dateFormatter.parse("2010-10-19"), trip.getStartDate());
		assertEquals(dateFormatter.parse("2010-10-22"), trip.getEndDate());

		trip = trips.get(1);
		assertEquals(200, trip.getId());
		assertEquals("Reston Trip", trip.getDisplayName());
		assertEquals("Reston, VA", trip.getPrimaryLocation());
		assertEquals("http://www.tripit.com/trips/200", trip.getTripUrl());
		assertEquals(dateFormatter.parse("2010-11-5"), trip.getStartDate());
		assertEquals(dateFormatter.parse("2010-11-7"), trip.getEndDate());
	}

	private void setupRestOperationsToTripItRetrieveProfile() {
		Map<String, Object> responseMap = new HashMap<String, Object>();
		Map<String, Object> profileMap = new HashMap<String, Object>();
		Map<String, String> attributesMap = new HashMap<String, String>();
		attributesMap.put("ref", "12345");
		profileMap.put("@attributes", attributesMap);
		profileMap.put("screen_name", "habuma");
		profileMap.put("public_display_name", "Craig Walls");
		profileMap.put("company", "SpringSource");
		profileMap.put("home_city", "Plano, TX");
		profileMap.put("profile_url", "habuma");
		responseMap.put("Profile", profileMap);

		when(restOperations.getForObject("https://api.tripit.com/v1/get/profile?format=json", Map.class)).thenReturn(
				responseMap);
	}
}
