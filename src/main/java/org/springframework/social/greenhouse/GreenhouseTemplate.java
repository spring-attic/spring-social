package org.springframework.social.greenhouse;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.social.oauth.OAuthSigningClientHttpRequestFactory;
import org.springframework.social.oauth1.ScribeOAuth1RequestSigner;
import org.springframework.social.twitter.TwitterErrorHandler;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

public class GreenhouseTemplate implements GreenhouseOperations {
	private RestTemplate restOperations;

	public GreenhouseTemplate(String apiKey, String apiSecret, String accessToken, String accessTokenSecret) {
		RestTemplate restTemplate = new RestTemplate(new OAuthSigningClientHttpRequestFactory(
				new SimpleClientHttpRequestFactory(),
				new ScribeOAuth1RequestSigner(apiKey, apiSecret, accessToken, accessTokenSecret)));
		restTemplate.setErrorHandler(new TwitterErrorHandler());
		this.restOperations = restTemplate;
		jsonAcceptingHeaders = new LinkedMultiValueMap<String, String>();
		jsonAcceptingHeaders.add("Accept", "application/json");
	}
	

	public GreenhouseProfile getUserProfile() {
		return restOperations.exchange(PROFILE_URL, HttpMethod.GET,
				new HttpEntity<Object>(jsonAcceptingHeaders), GreenhouseProfile.class, "@self").getBody();
	}

	public List<Event> getUpcomingEvents() {
		return Arrays.asList(restOperations.exchange(EVENTS_URL, HttpMethod.GET,
				new HttpEntity<Object>(jsonAcceptingHeaders), Event[].class).getBody());
	}

	public List<Event> getEventsAfter(Date date) {
		String isoDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.000-00:00").format(date);
		return Arrays.asList(restOperations.exchange(EVENTS_URL + "?after={dateTime}", HttpMethod.GET,
				new HttpEntity<Object>(jsonAcceptingHeaders), Event[].class, isoDate).getBody());
	}

	public List<EventSession> getSessionsOnDay(long eventId, Date date) {
		String isoDate = new SimpleDateFormat("yyyy-MM-dd").format(date);
		return Arrays.asList(restOperations.exchange(SESSIONS_FOR_DAY_URL, HttpMethod.GET,
				new HttpEntity<Object>(jsonAcceptingHeaders), EventSession[].class, eventId, isoDate).getBody());
	}


	private static final String BASE_URL = "http://localhost:8080/greenhouse";
	private static final String PROFILE_URL = BASE_URL + "/members/{id}";
	private static final String EVENTS_URL = BASE_URL + "/events";
	private static final String SESSIONS_FOR_DAY_URL = BASE_URL + "/events/{eventId}/sessions/{day}";
	private MultiValueMap<String, String> jsonAcceptingHeaders;
}
