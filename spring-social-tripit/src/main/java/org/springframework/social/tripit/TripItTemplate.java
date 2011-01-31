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

import java.util.List;

import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.social.oauth1.OAuth1RequestInterceptor;
import org.springframework.social.oauth1.OAuthToken;
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
public class TripItTemplate implements TripItOperations {
	
	private final RestTemplate restTemplate;

	/**
	 * Constructs a TripItTemplate with the minimal amount of information required to sign requests with an OAuth <code>Authorization</code> header.
	 * @param apiKey the application's API key
	 * @param apiSecret the application's API secret
	 * @param accessToken an access token acquired through OAuth authentication with LinkedIn
	 * @param accessTokenSecret an access token secret acquired through OAuth authentication with LinkedIn
	 */
	public TripItTemplate(String apiKey, String apiSecret, String accessToken, String accessTokenSecret) {
		restTemplate = new RestTemplate();
		restTemplate.setInterceptors(new ClientHttpRequestInterceptor[] { new OAuth1RequestInterceptor(apiKey, apiSecret, new OAuthToken(accessToken, accessTokenSecret)) });
	}

	public String getProfileId() {
		return getUserProfile().getId();
	}

	public String getProfileUrl() {
		return getUserProfile().getProfileUrl();
	}

	public TripItProfile getUserProfile() {
		TripItProfileResponse response = restTemplate.getForObject("https://api.tripit.com/v1/get/profile?format=json", TripItProfileResponse.class);
		return response.getProfile();
	}

	public List<Trip> getUpcomingTrips() {
		return restTemplate.getForObject("https://api.tripit.com/v1/list/trip/traveler/true/past/false?format=json", TripListResponse.class).getTrips();
	}
	
	// subclassing hooks
	
	protected RestTemplate getRestTemplate() {
		return restTemplate;
	}

}
