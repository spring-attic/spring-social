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
import static org.junit.internal.matchers.IsCollectionContaining.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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
	public void getTrips() {
		TripListResponse response = new TripListResponse();
		Trip trip1 = new Trip();
		trip1.id = 100;
		trip1.displayName = "Trip to Chicago";
		trip1.primaryLocation = "Chicago, IL";
		trip1.tripPath = "/trip/100";

		Trip trip2 = new Trip();
		trip2.id = 200;
		trip2.displayName = "Virginia Trip";
		trip2.primaryLocation = "Reston, VA";
		trip2.tripPath = "/trip/200";

		response.setTrips(Arrays.asList(trip1, trip2));
		response.setTimestamp(123456789L);

		when(restOperations.getForObject("https://api.tripit.com/v1/list/trip/traveler/true/past/false?format=json",
						TripListResponse.class)).thenReturn(response);

		List<Trip> trips = tripIt.getUpcomingTrips();
		assertThat(trips, hasItem(trip1));
		assertThat(trips, hasItem(trip2));
		assertEquals(123456789L, response.getTimestamp());
	}

	private void setupRestOperationsToTripItRetrieveProfile() {
		TripItProfileResponse response = new TripItProfileResponse();
		response.profile = new TripItProfile();
		response.profile.attributes = Collections.singletonMap("ref", "12345");
		response.profile.screenName = "habuma";
		response.profile.publicDisplayName = "Craig Walls";
		response.profile.company = "SpringSource";
		response.profile.homeCity = "Plano, TX";
		response.profile.profilePath = "habuma";
		when(restOperations.getForObject("https://api.tripit.com/v1/get/profile?format=json",
						TripItProfileResponse.class)).thenReturn(response);
	}
}
