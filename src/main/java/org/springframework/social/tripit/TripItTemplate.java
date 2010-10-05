package org.springframework.social.tripit;

import java.util.List;

import org.springframework.http.client.CommonsClientHttpRequestFactory;
import org.springframework.social.oauth.OAuthSigningClientHttpRequestFactory;
import org.springframework.social.oauth1.ScribeOAuth1RequestSigner;
import org.springframework.social.twitter.TwitterErrorHandler;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;

public class TripItTemplate implements TripItOperations {
	private final RestOperations restOperations;

	public TripItTemplate(String apiKey, String apiSecret, String accessToken, String accessTokenSecret) {
		RestTemplate restTemplate = new RestTemplate(new OAuthSigningClientHttpRequestFactory(
				new CommonsClientHttpRequestFactory(),
				new ScribeOAuth1RequestSigner(apiKey, apiSecret, accessToken, accessTokenSecret)));
		restTemplate.setErrorHandler(new TwitterErrorHandler());
		this.restOperations = restTemplate;
	}

	public String getProfileId() {
		return getProfile().getId();
	}

	public void setStatus(String status) {
		// TODO: There's no reason not to support this...come back to this when
		// I am back on the network and can lookup how to do it
		throw new UnsupportedOperationException("Status update not supported for LinkedIn");
	}

	public TripItProfile getProfile() {
		TripItProfileResponse response = restOperations.getForObject(
				"https://api.tripit.com/v1/get/profile?format=json", TripItProfileResponse.class);
		return response.getProfile();
	}

	public List<Trip> getTrips() {
		return restOperations.getForObject("https://api.tripit.com/v1/list/trip/traveler/true/past/false?format=json",
				TripListResponse.class).getTrips();
	}
}
