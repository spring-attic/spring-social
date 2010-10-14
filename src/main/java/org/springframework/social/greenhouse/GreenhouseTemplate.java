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
	private String baseUrl;

	public GreenhouseTemplate(String apiKey, String apiSecret, String accessToken, String accessTokenSecret) {
        this(apiKey, apiSecret, accessToken, accessTokenSecret, DEFAULT_BASE_URL);
    }
    
	public GreenhouseTemplate(String apiKey, String apiSecret, String accessToken, String accessTokenSecret, String baseUrl) {
		RestTemplate restTemplate = new RestTemplate(new OAuthSigningClientHttpRequestFactory(
				new SimpleClientHttpRequestFactory(),
				new ScribeOAuth1RequestSigner(apiKey, apiSecret, accessToken, accessTokenSecret)));
		restTemplate.setErrorHandler(new TwitterErrorHandler());
		this.restOperations = restTemplate;
		jsonAcceptingHeaders = new LinkedMultiValueMap<String, String>();
		jsonAcceptingHeaders.add("Accept", "application/json");
		this.baseUrl = baseUrl;
	}
	

	public GreenhouseProfile getUserProfile() {
		return restOperations.exchange(baseUrl + PROFILE_PATH, HttpMethod.GET,
				new HttpEntity<Object>(jsonAcceptingHeaders), GreenhouseProfile.class, "@self").getBody();
	}

	public List<Event> getUpcomingEvents() {
		return Arrays.asList(restOperations.exchange(baseUrl + EVENTS_PATH, HttpMethod.GET,
				new HttpEntity<Object>(jsonAcceptingHeaders), Event[].class).getBody());
	}

	public List<Event> getEventsAfter(Date date) {
		String isoDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.000-00:00").format(date);
		return Arrays.asList(restOperations.exchange(baseUrl + EVENTS_PATH + "?after={dateTime}", HttpMethod.GET,
				new HttpEntity<Object>(jsonAcceptingHeaders), Event[].class, isoDate).getBody());
	}

	public List<EventSession> getSessionsOnDay(long eventId, Date date) {
		String isoDate = new SimpleDateFormat("yyyy-MM-dd").format(date);
		return Arrays.asList(restOperations.exchange(baseUrl + SESSIONS_FOR_DAY_PATH, HttpMethod.GET,
				new HttpEntity<Object>(jsonAcceptingHeaders), EventSession[].class, eventId, isoDate).getBody());
	}


	private static final String DEFAULT_BASE_URL = "https://greenhouse.springsource.org";
	private static final String PROFILE_PATH = "/members/{id}";
	private static final String EVENTS_PATH = "/events";
	private static final String SESSIONS_FOR_DAY_PATH = "/events/{eventId}/sessions/{day}";
	private MultiValueMap<String, String> jsonAcceptingHeaders;
}
