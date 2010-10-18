package org.springframework.social.tripit;

import java.util.List;

import org.springframework.http.client.CommonsClientHttpRequestFactory;
import org.springframework.social.oauth.OAuthSigningClientHttpRequestFactory;
import org.springframework.social.oauth1.ScribeOAuth1RequestSigner;
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
	private final RestOperations restOperations;

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
				new CommonsClientHttpRequestFactory(),
				new ScribeOAuth1RequestSigner(apiKey, apiSecret, accessToken, accessTokenSecret)));
		this.restOperations = restTemplate;
	}

	public String getProfileId() {
		return getUserProfile().getId();
	}

	public String getProfileUrl() {
		return getUserProfile().getProfileUrl();
	}

	public void updateStatus(String status) {
		// TODO: There's no reason not to support this...come back to this when
		// I am back on the network and can lookup how to do it
		throw new UnsupportedOperationException("Status update not supported for LinkedIn");
	}

	public TripItProfile getUserProfile() {
		TripItProfileResponse response = restOperations.getForObject(
				"https://api.tripit.com/v1/get/profile?format=json", TripItProfileResponse.class);
		return response.getProfile();
	}

	public List<Trip> getTrips() {
		return restOperations.getForObject("https://api.tripit.com/v1/list/trip/traveler/true/past/false?format=json",
				TripListResponse.class).getTrips();
	}
}
