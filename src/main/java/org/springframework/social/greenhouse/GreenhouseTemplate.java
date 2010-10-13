package org.springframework.social.greenhouse;

import java.util.List;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.CommonsClientHttpRequestFactory;
import org.springframework.social.oauth.OAuthSigningClientHttpRequestFactory;
import org.springframework.social.oauth1.ScribeOAuth1RequestSigner;
import org.springframework.social.twitter.SearchResults;
import org.springframework.social.twitter.TwitterErrorHandler;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

public class GreenhouseTemplate implements GreenhouseOperations {
	private RestTemplate restOperations;

	public GreenhouseTemplate(String apiKey, String apiSecret, String accessToken, String accessTokenSecret) {
		RestTemplate restTemplate = new RestTemplate(new OAuthSigningClientHttpRequestFactory(
				new CommonsClientHttpRequestFactory(),
				new ScribeOAuth1RequestSigner(apiKey, apiSecret, accessToken, accessTokenSecret)));
		restTemplate.setErrorHandler(new TwitterErrorHandler());
		this.restOperations = restTemplate;
	}
	

	public GreenhouseProfile getUserProfile() {
		MultiValueMap<String, String> headers = new LinkedMultiValueMap<String, String>();
		headers.add("Accept", "application/json");
		HttpEntity<Object> requestEntity = new HttpEntity<Object>(headers);

		ResponseEntity<GreenhouseProfile> response = restOperations.exchange(PROFILE_URL, HttpMethod.GET,
				requestEntity, GreenhouseProfile.class, "@self");
		return response.getBody();
	}

	public List<RecentActivity> getRecentActivity() {
		throw new UnsupportedOperationException();
	}

	public List<RecentActivity> getRecentActivity(String last) {
		throw new UnsupportedOperationException();
	}

	public List<Event> getUpcomingEvents() {
		throw new UnsupportedOperationException();
	}

	public List<Event> getEventsAfter(String dateTime) {
		throw new UnsupportedOperationException();
	}

	public List<EventSession> getEventFavorites(long eventId) {
		throw new UnsupportedOperationException();
	}

	public SearchResults getEventTweets(long eventId) {
		throw new UnsupportedOperationException();
	}

	public SearchResults getEventTweets(long eventId, int page, int pageSize) {
		throw new UnsupportedOperationException();
	}

	public void postEventTweet(long eventId, String status) {
		throw new UnsupportedOperationException();
	}

	public void postEventRetweet(long eventId, long tweetId) {
		throw new UnsupportedOperationException();
	}

	public List<EventSession> getMyEventFavorites(long eventId) {
		throw new UnsupportedOperationException();
	}

	public List<EventSession> getSessionsOnDay(long eventId, String dateTime) {
		throw new UnsupportedOperationException();
	}

	public void toggleSessionFavorite(long eventId, long sessionId) {
		throw new UnsupportedOperationException();
	}

	public void updateSessionRating(long eventId, long sessionId, short value, String comment) {
		throw new UnsupportedOperationException();
	}

	public SearchResults getSessionTweets(long eventId, long sessionId) {
		throw new UnsupportedOperationException();
	}

	public SearchResults getSessionTweets(long eventId, long sessionId, int page, int pageSize) {
		throw new UnsupportedOperationException();
	}

	public void postSessionTweet(long eventId, long sessionId, String status) {
		throw new UnsupportedOperationException();
	}

	public void postSessionRetweet(long eventId, long sessionId, long tweetId) {
		throw new UnsupportedOperationException();
	}

	private static final String BASE_URL = "http://localhost:8888/greenhouse";
	private static final String PROFILE_URL = BASE_URL + "/members/{id}";
}
