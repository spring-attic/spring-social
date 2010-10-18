package org.springframework.social.linkedin;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.http.client.CommonsClientHttpRequestFactory;
import org.springframework.social.oauth.OAuthSigningClientHttpRequestFactory;
import org.springframework.social.oauth1.ScribeOAuth1RequestSigner;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;

/**
 * <p>
 * This is the central class for interacting with LinkedIn.
 * </p>
 * 
 * <p>
 * Greenhouse operations require OAuth authentication with the server.
 * Therefore, LinkedInTemplate must be constructed with the minimal information
 * required to sign requests with and OAuth 1 Authorization header.
 * </p>
 * 
 * @author Craig Walls
 */
public class LinkedInTemplate implements LinkedInOperations {

	private final RestOperations restOperations;

	/**
	 * Creates a new LinkedInTemplate given the minimal amount of information
	 * needed to sign requests with OAuth 1 credentials.
	 * 
	 * @param apiKey
	 *            the application's API key
	 * @param apiSecret
	 *            the application's API secret
	 * @param accessToken
	 *            an access token acquired through OAuth authentication with
	 *            LinkedIn
	 * @param accessTokenSecret
	 *            an access token secret acquired through OAuth authentication
	 *            with LinkedIn
	 */
	public LinkedInTemplate(String apiKey, String apiSecret, String accessToken, String accessTokenSecret) {
		RestTemplate restTemplate = new RestTemplate(new OAuthSigningClientHttpRequestFactory(
				new CommonsClientHttpRequestFactory(),
				new ScribeOAuth1RequestSigner(apiKey, apiSecret, accessToken, accessTokenSecret)));
		this.restOperations = restTemplate;
	}

	public String getProfileId() {
		return getUserProfile().getId();
	}

	public String getProfileUrl() {
		return getUserProfile().getPublicProfileUrl();
	}

	public void updateStatus(String status) {
		// TODO: There's no reason not to support this...come back to this when
		// I have opportunity to implement it
		throw new UnsupportedOperationException("Status update not supported for LinkedIn");
	}

	public LinkedInProfile getUserProfile() {
		ResponseEntity<LinkedInProfile> response = restOperations.getForEntity(GET_CURRENT_USER_INFO,
				LinkedInProfile.class);

		return response.getBody();
	}

	public List<LinkedInProfile> getConnections() {
		LinkedInConnections connections = restOperations.getForObject(
				"http://api.linkedin.com/v1/people/~/connections", LinkedInConnections.class);
		return connections.getConnections();
	}

	private static final String GET_CURRENT_USER_INFO = "https://api.linkedin.com/v1/people/~:public";

}
