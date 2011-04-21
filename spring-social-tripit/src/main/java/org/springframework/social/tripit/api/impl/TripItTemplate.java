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
package org.springframework.social.tripit.api.impl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.springframework.social.oauth1.ProtectedResourceClientFactory;
import org.springframework.social.tripit.api.Trip;
import org.springframework.social.tripit.api.TripItApi;
import org.springframework.social.tripit.api.TripItProfile;
import org.springframework.web.client.RestTemplate;

/**
 * The central class for interacting with TripIt.
 * <p>
 * TripIt operations require OAuth 1 authentication. Therefore TripIt template
 * must be given the minimal amount of information required to sign requests to
 * the TripIt API with an OAuth <code>Authorization</code> header.
 * </p>
 * @author Craig Walls
 */
public class TripItTemplate implements TripItApi {
	
	private final RestTemplate restTemplate;

	/**
	 * Constructs a TripItTemplate with the minimal amount of information required to sign requests with an OAuth <code>Authorization</code> header.
	 * @param apiKey the application's API key
	 * @param apiSecret the application's API secret
	 * @param accessToken an access token acquired through OAuth authentication with LinkedIn
	 * @param accessTokenSecret an access token secret acquired through OAuth authentication with LinkedIn
	 */
	public TripItTemplate(String apiKey, String apiSecret, String accessToken, String accessTokenSecret) {
		this.restTemplate = ProtectedResourceClientFactory.create(apiKey, apiSecret, accessToken, accessTokenSecret);
	}

	public String getProfileId() {
		return getUserProfile().getId();
	}

	public String getProfileUrl() {
		return getUserProfile().getProfileUrl();
	}

	@SuppressWarnings("unchecked")
	public TripItProfile getUserProfile() {
		Map<String, ?> responseMap = restTemplate.getForObject("https://api.tripit.com/v1/get/profile?format=json",
				Map.class);
		Map<String, ?> profileMap = (Map<String, ?>) responseMap.get("Profile");
		Map<String, String> attributesMap = (Map<String, String>) profileMap.get("@attributes");
		String id = attributesMap.get("ref");
		String screenName = String.valueOf(profileMap.get("screen_name"));
		String publicDisplayName = String.valueOf(profileMap.get("public_display_name"));
		String homeCity = String.valueOf(profileMap.get("home_city"));
		String company = String.valueOf(profileMap.get("company"));
		String profilePath = String.valueOf(profileMap.get("profile_url"));
		String profileImageUrl = String.valueOf(profileMap.get("photo_url"));
		String emailAddress = null;
		Map<String, Object> emailAddressesMap = (Map<String, Object>) profileMap.get("ProfileEmailAddresses");
		if(emailAddressesMap != null) {
			Map<String, String> emailAddressMap = (Map<String, String>) emailAddressesMap.get("ProfileEmailAddress");
			if(emailAddressMap != null) {
				emailAddress = emailAddressMap.get("address");
			}
		}
		return new TripItProfile(id, screenName, publicDisplayName, emailAddress, homeCity, company, profilePath, profileImageUrl);
	}

	public List<Trip> getUpcomingTrips() {
		@SuppressWarnings("unchecked")
		Map<String, ?> responseMap = restTemplate.getForObject(
				"https://api.tripit.com/v1/list/trip/traveler/true/past/false?format=json", Map.class);

		List<Trip> trips = new ArrayList<Trip>();
		List<Map<String, ?>> tripsList = getTripsList(responseMap);
		for (Map<String, ?> tripItem : tripsList) {
			long id = Long.valueOf(String.valueOf(tripItem.get("id")));
			String displayName = String.valueOf(tripItem.get("display_name"));
			String primaryLocation = String.valueOf(tripItem.get("primary_location"));
			Date startDate = parseDate(String.valueOf(tripItem.get("start_date")));
			Date endDate = parseDate(String.valueOf(tripItem.get("end_date")));
			String tripPath = String.valueOf(tripItem.get("relative_url"));
			Trip trip = new Trip(id, displayName, primaryLocation, startDate, endDate, tripPath);
			trips.add(trip);
		}

		return trips;
	}

	@SuppressWarnings("unchecked")
	private List<Map<String, ?>> getTripsList(Map<String, ?> result) {
		List<Map<String, ?>> trips;
		Object tripObject = result.get("Trip");
		if (tripObject instanceof Map) {
			trips = new ArrayList<Map<String, ?>>();
			trips.add((Map<String, ?>) tripObject);
		} else {
			trips = (List<Map<String, ?>>) tripObject;
		}
		return trips;
	}

	private SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

	private Date parseDate(String dateString) {
		try {
			Date startDate = dateFormatter.parse(dateString);
			return startDate;
		} catch (ParseException e) {
			return null;
		}
	}
	
	// subclassing hooks
	
	protected RestTemplate getRestTemplate() {
		return restTemplate;
	}

}
