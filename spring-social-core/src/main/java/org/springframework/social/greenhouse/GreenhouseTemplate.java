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
package org.springframework.social.greenhouse;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.social.oauth.OAuthSigningClientHttpRequestFactory;
import org.springframework.social.oauth1.OAuth1RequestSignerFactory;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;

/**
 * <p>
 * This is the central class for interacting with Greenhouse.
 * </p>
 * 
 * <p>
 * Greenhouse operations require OAuth authentication with the server.
 * Therefore, GreenhouseTemplate must be constructed with the minimal
 * information required to sign requests with and OAuth 1 Authorization header.
 * </p>
 * 
 * @author Craig Walls
 */
public class GreenhouseTemplate implements GreenhouseOperations {
	RestOperations restOperations;
	private String baseUrl;

	/**
	 * <p>
	 * Constructs a GreenhouseTemplate with the minimal amount of information
	 * required to sign requests with an OAuth 1 Authorization header.
	 * </p>
	 * 
	 * <p>
	 * This constructor assumes that the application will be conversing with the
	 * production Greenhouse server at http://springsource.greenhouse.org.
	 * </p>
	 * 
	 * @param apiKey
	 *            The application's API Key as assigned when registering the
	 *            application with Greenhouse
	 * @param apiSecret
	 *            The application's API Secret as assigned when registering the
	 *            application with Greenhouse
	 * @param accessToken
	 *            An access token acquired through successful OAuth 1
	 *            authentication with Greenhouse
	 * @param accessTokenSecret
	 *            An access token secret acquired through successful OAuth 1
	 *            authentication with Greenhouse
	 */
	public GreenhouseTemplate(String apiKey, String apiSecret, String accessToken, String accessTokenSecret) {
        this(apiKey, apiSecret, accessToken, accessTokenSecret, DEFAULT_BASE_URL);
    }

	/**
	 * <p>
	 * Constructs a GreenhouseTemplate with the minimal amount of information
	 * required to sign requests with an OAuth 1 Authorization header.
	 * </p>
	 * 
	 * <p>
	 * This constructor allows the application to specify the base URL of the
	 * Greenhouse server, enabling the template to converse with a development
	 * or test server.
	 * </p>
	 * 
	 * @param apiKey
	 *            The application's API Key as assigned when registering the
	 *            application with Greenhouse
	 * @param apiSecret
	 *            The application's API Secret as assigned when registering the
	 *            application with Greenhouse
	 * @param accessToken
	 *            An access token acquired through successful OAuth 1
	 *            authentication with Greenhouse
	 * @param accessTokenSecret
	 *            An access token secret acquired through successful OAuth 1
	 *            authentication with Greenhouse
	 * @param baseUrl
	 *            The base URL of the Greenhouse server
	 */
	public GreenhouseTemplate(String apiKey, String apiSecret, String accessToken, String accessTokenSecret, String baseUrl) {
		RestTemplate restTemplate = new RestTemplate(new OAuthSigningClientHttpRequestFactory(
				new SimpleClientHttpRequestFactory(),
				OAuth1RequestSignerFactory.getRequestSigner(apiKey, apiSecret, accessToken, accessTokenSecret)));
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


	static final String DEFAULT_BASE_URL = "https://greenhouse.springsource.org";
	static final String PROFILE_PATH = "/members/{id}";
	static final String EVENTS_PATH = "/events";
	static final String SESSIONS_FOR_DAY_PATH = "/events/{eventId}/sessions/{day}";
	private MultiValueMap<String, String> jsonAcceptingHeaders;
}
