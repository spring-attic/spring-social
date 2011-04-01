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

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.social.AccountNotConnectedException;
import org.springframework.social.ResponseStatusCodeTranslator;
import org.springframework.social.SocialException;
import org.springframework.social.oauth1.ProtectedResourceClientFactory;
import org.springframework.social.twitter.support.TwitterErrorHandler;
import org.springframework.social.twitter.support.TwitterResponseStatusCodeTranslator;
import org.springframework.social.twitter.support.extractors.ResponseExtractor;
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
 * will result in {@link AccountNotConnectedException} being thrown.
 * </p>
 * @author Craig Walls
 */
public class TwitterTemplate implements TwitterApi, TwitterRequestApi {

	private final RestTemplate restTemplate;

	private final TweetApi tweetApi;

	private final UserApi userApi;

	private final FriendsApi friendsApi;

	private final ListsApi listsApi;

	private final SearchApi searchApi;

	private final DirectMessageApi directMessageApi;

	/**
	 * Create a new instance of TwitterTemplate.
	 * This constructor creates a new TwitterTemplate able to perform unauthenticated operations against Twitter's API.
	 * Some operations, such as search, do not require OAuth authentication.
	 * A TwitterTemplate created with this constructor will support those operations.
	 * Those operations requiring authentication will throw {@link AccountNotConnectedException}.
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
	}
	
	private TwitterTemplate(RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
		restTemplate.setErrorHandler(new TwitterErrorHandler());
		statusCodeTranslator = new TwitterResponseStatusCodeTranslator();
		this.tweetApi = new TweetApiTemplate(this, restTemplate);
		this.userApi = new UserApiTemplate(this, restTemplate);
		this.friendsApi = new FriendsApiTemplate(this, restTemplate, statusCodeTranslator);
		this.listsApi = new ListsApiTemplate(this, restTemplate, userApi);
		this.searchApi = new SearchApiTemplate(this, restTemplate);
		this.directMessageApi = new DirectMessageApiTemplate(this);
	}

	public TweetApi tweetApi() {
		return tweetApi;
	}

	public FriendsApi friendsApi() {
		return friendsApi;
	}

	public ListsApi listsApi() {
		return listsApi;
	}

	public SearchApi searchApi() {
		return searchApi;
	}

	public DirectMessageApi directMessageApi() {
		return directMessageApi;
	}

	public UserApi userApi() {
		return userApi;
	}
	
	// low-level
	public <T> T fetchObject(String path, ResponseExtractor<T> extractor, Object... params) {
		Map<String, Object> response = restTemplate.getForObject(API_URL_BASE + path, Map.class, params);
		return extractor.extractObject(response);
	}
	
	public <T> List<T> fetchObjects(String path, ResponseExtractor<T> extractor, Object... params) {
		List<Map<String, Object>> response = restTemplate.getForObject(API_URL_BASE + path, List.class, params);
		return extractor.extractObjects(response);
	}
	
	public <T> List<T> fetchObjects(String path, String jsonPath, ResponseExtractor<T> extractor, Object... params) {
		Map<String, Object> response = restTemplate.getForObject(API_URL_BASE + path, Map.class, params);
		List<Map<String, Object>> list = (List<Map<String, Object>>) response.get(jsonPath);
		return extractor.extractObjects(list);
	}
	
	public void publish(String path, MultiValueMap<String, Object> data, Object... params) {
		ResponseEntity<Map> response = restTemplate.postForEntity(API_URL_BASE + path, data, Map.class, params);
		handleResponseErrors(response);
	}
	
	public <T> T publish(String path, MultiValueMap<String, Object> data, ResponseExtractor<T> extractor, Object... params) {
		@SuppressWarnings("rawtypes")
		ResponseEntity<Map> response = restTemplate.postForEntity(API_URL_BASE + path, data, Map.class, params);
		handleResponseErrors(response);
		return extractor.extractObject(response.getBody());
	}

	public void delete(String path, Object... params) {
		restTemplate.delete(API_URL_BASE + path, params);
	}
	
	// subclassing hooks

	protected RestTemplate getRestTemplate() {
		return restTemplate;
	}

	@SuppressWarnings("rawtypes")
	private void handleResponseErrors(ResponseEntity<Map> response) {
		SocialException exception = statusCodeTranslator.translate(response);
		if (exception != null) {
			throw exception;
		}
	}
	
	public static final String API_URL_BASE = "https://api.twitter.com/1/";

	private ResponseStatusCodeTranslator statusCodeTranslator;
}
