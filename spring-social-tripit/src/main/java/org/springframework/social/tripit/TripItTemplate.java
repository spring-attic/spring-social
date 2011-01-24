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

import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.social.provider.oauth.OAuthSigningClientHttpRequestFactory;
import org.springframework.social.provider.oauth1.OAuth1ClientRequestSigner;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;

/**
 * <p>
 * The central class for interacting with TripIt.
 * </p>
 * 
 * <p>
 * TripIt operations require OAuth 1 authentication. Therefore TripIt template
 * must be given the minimal amount of information required to sign requests to
 * the TripIt API with an OAuth <code>Authorization</code> header.
 * </p>
 * 
 * @author Craig Walls
 */
public class TripItTemplate implements TripItOperations {
	RestOperations restOperations;

	/**
	 * Constructs a TripItTemplate with the minimal amount of information
	 * required to sign requests with an OAuth <code>Authorization</code>
	 * header.
	 * 
	 * @param apiKey
	 *            The application's API key as given by TripIt when registering
	 *            the application.
	 * @param apiSecret
	 *            The application's API secret as given by TripIt when
	 *            registering the application.
	 * @param accessToken
	 *            An access token granted to the application after OAuth
	 *            authentication.
	 * @param accessTokenSecret
	 *            An access token secret granted to the application after OAuth
	 *            authentication.
	 */
	public TripItTemplate(String apiKey, String apiSecret, String accessToken, String accessTokenSecret) {
		RestTemplate restTemplate = new RestTemplate(new OAuthSigningClientHttpRequestFactory(
				new SimpleClientHttpRequestFactory(),
				new OAuth1ClientRequestSigner(apiKey, apiSecret, accessToken, accessTokenSecret)));
		this.restOperations = restTemplate;
	}

	public String getProfileId() {
		return getUserProfile().getId();
	}

	public String getProfileUrl() {
		return getUserProfile().getProfileUrl();
	}

	public TripItProfile getUserProfile() {
		TripItProfileResponse response = restOperations.getForObject(
				"https://api.tripit.com/v1/get/profile?format=json", TripItProfileResponse.class);
		return response.getProfile();
	}

	public List<Trip> getUpcomingTrips() {
		return restOperations.getForObject("https://api.tripit.com/v1/list/trip/traveler/true/past/false?format=json",
				TripListResponse.class).getTrips();
	}
}
