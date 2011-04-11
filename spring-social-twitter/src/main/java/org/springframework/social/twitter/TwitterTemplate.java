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
package org.springframework.social.twitter;

import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.social.BadCredentialsException;
import org.springframework.social.oauth1.ProtectedResourceClientFactory;
import org.springframework.social.twitter.support.TwitterErrorHandler;
import org.springframework.social.twitter.support.extractors.ResponseExtractor;
import org.springframework.social.util.URIBuilder;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

/**
 * This is the central class for interacting with Twitter.
 * <p>
 * Most (not all) Twitter operations require OAuth authentication. To perform
 * such operations, {@link TwitterTemplate} must be constructed with the minimal
 * amount of information required to sign requests to Twitter's API with an
 * OAuth <code>Authorization</code> header.
 * </p>
 * <p>
 * There are a few operations, such as searching, that do not require OAuth
 * authentication. In those cases, you may use a {@link TwitterTemplate} that is
 * created through the default constructor and without any OAuth details.
 * Attempts to perform secured operations through such an instance, however,
 * will result in {@link BadCredentialsException} being thrown.
 * </p>
 * @author Craig Walls
 */
public class TwitterTemplate implements TwitterApi {

	private boolean isAuthorizedForUser;
	
	private final RestTemplate restTemplate;

	private final TimelineOperations timelineOperations;

	private final UserOperations userOperations;

	private final FriendOperations friendOperations;

	private final ListOperations listOperations;

	private final SearchOperations searchOperations;

	private final DirectMessageOperations directMessageOperations;

	/**
	 * Create a new instance of TwitterTemplate.
	 * This constructor creates a new TwitterTemplate able to perform unauthenticated operations against Twitter's API.
	 * Some operations, such as search, do not require OAuth authentication.
	 * A TwitterTemplate created with this constructor will support those operations.
	 * Those operations requiring authentication will throw {@link BadCredentialsException}.
	 */
	public TwitterTemplate() {
		this(new RestTemplate());
	}

	/**
	 * Create a new instance of TwitterTemplate.
	 * @param apiKey the application's API key
	 * @param apiSecret the application's API secret
	 * @param accessToken an access token acquired through OAuth authentication with LinkedIn
	 * @param accessTokenSecret an access token secret acquired through OAuth authentication with LinkedIn
	 */
	public TwitterTemplate(String apiKey, String apiSecret, String accessToken, String accessTokenSecret) {
		this(ProtectedResourceClientFactory.create(apiKey, apiSecret, accessToken, accessTokenSecret));
		isAuthorizedForUser = true;
	}
	
	private TwitterTemplate(RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
		restTemplate.setErrorHandler(new TwitterErrorHandler());
		this.userOperations = new UserTemplate(this);
		this.directMessageOperations = new DirectMessageTemplate(this);
		this.friendOperations = new FriendTemplate(this);
		this.timelineOperations = new TimelineTemplate(this);
		
		// TODO : Break ListTemplate's  dependence on userOperations
		this.listOperations = new ListTemplate(this, userOperations);
		
		// TODO : Break the dependence on restTemplate
		this.searchOperations = new SearchTemplate(this, restTemplate);
	}

	public boolean isAuthorizedForUser() {
		return isAuthorizedForUser;
	}
	
	public TimelineOperations timelineOperations() {
		return timelineOperations;
	}

	public FriendOperations friendOperations() {
		return friendOperations;
	}

	public ListOperations listOperations() {
		return listOperations;
	}

	public SearchOperations searchOperations() {
		return searchOperations;
	}

	public DirectMessageOperations directMessageOperations() {
		return directMessageOperations;
	}

	public UserOperations userOperations() {
		return userOperations;
	}
	
	// low-level
	public <T> T fetchObject(String path, ResponseExtractor<T> extractor) {
		return fetchObject(path, extractor, Collections.<String, String>emptyMap() );
	}
	
	public <T> T fetchObject(String path, ResponseExtractor<T> extractor, Map<String, String> params) {
		@SuppressWarnings("unchecked")
		Map<String, Object> response = restTemplate.getForObject(buildUri(path, params), Map.class);
		return extractor.extractObject(response);
	}
	
	public <T> List<T> fetchObjects(String path, ResponseExtractor<T> extractor) {
		@SuppressWarnings("unchecked")
		List<Map<String, Object>> response = restTemplate.getForObject(buildUri(path, Collections.<String, String>emptyMap() ), List.class);
		return extractor.extractObjects(response);
	}

	public <T> List<T> fetchObjects(String path, ResponseExtractor<T> extractor, Map<String, String> params) {
		@SuppressWarnings("unchecked")
		List<Map<String, Object>> response = restTemplate.getForObject(buildUri(path, params), List.class);
		return extractor.extractObjects(response);
	}
	
	@SuppressWarnings("unchecked")
	public <T> List<T> fetchObjects(String path, String jsonProperty, ResponseExtractor<T> extractor) {
		Map<String, Object> response = restTemplate.getForObject(buildUri(path, Collections.<String, String>emptyMap()), Map.class);
		List<Map<String, Object>> list = (List<Map<String, Object>>) response.get(jsonProperty);
		return extractor.extractObjects(list);
	}
	
	public <T> T fetchObject(String path, Class<T> type) {
		return fetchObject(path, type, Collections.<String, String>emptyMap());
	}

	public <T> T fetchObject(String path, Class<T> type, Map<String, String> params) {
		return restTemplate.getForObject(buildUri(path, params), type);
	}

	public byte[] fetchImage(String path) {		
		ResponseEntity<byte[]> response = restTemplate.getForEntity(buildUri(path, Collections.<String, String>emptyMap()), byte[].class);
		if(response.getStatusCode() == HttpStatus.FOUND) {
			throw new UnsupportedOperationException("Attempt to fetch image resulted in a redirect which could not be followed. Add Apache HttpComponents HttpClient to the classpath " +
					"to be able to follow redirects.");
		}
		return response.getBody();
	}
	
	public void publish(String path, MultiValueMap<String, Object> data) {
		restTemplate.postForEntity(buildUri(path, Collections.<String, String>emptyMap()), data, Map.class);
	}

	public <T> T publish(String path, MultiValueMap<String, Object> data, ResponseExtractor<T> extractor) {
		return publish(path, data, extractor, Collections.<String, String>emptyMap());
	}
	
	@SuppressWarnings("unchecked")
	public <T> T publish(String path, MultiValueMap<String, Object> data, ResponseExtractor<T> extractor, Map<String, String> params) {
		Map<String, Object> response = (Map<String, Object>) restTemplate.postForObject(buildUri(path, params), data, Map.class);
		return extractor.extractObject(response);
	}

	public void delete(String path) {
		delete(path, Collections.<String, String>emptyMap());
	}
	
	public void delete(String path, Map<String, String> queryParams) {
		restTemplate.delete(buildUri(path, queryParams));
	}
	
	// private helper 
	
	private URI buildUri(String path, Map<String, String> params) {
		URIBuilder uriBuilder = URIBuilder.fromUri(API_URL_BASE + path);
		for (String paramName : params.keySet()) {
			uriBuilder.queryParam(paramName, String.valueOf(params.get(paramName)));
		}
		URI uri = uriBuilder.build();
		return uri;
	}
	// subclassing hooks

	protected RestTemplate getRestTemplate() {
		return restTemplate;
	}

	public static final String API_URL_BASE = "https://api.twitter.com/1/";

}
