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
package org.springframework.social.twitter.api.impl;

import java.util.List;

import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJacksonHttpMessageConverter;
import org.springframework.social.BadCredentialsException;
import org.springframework.social.oauth1.ProtectedResourceClientFactory;
import org.springframework.social.support.ClientHttpRequestFactorySelector;
import org.springframework.social.twitter.api.DirectMessageOperations;
import org.springframework.social.twitter.api.FriendOperations;
import org.springframework.social.twitter.api.ListOperations;
import org.springframework.social.twitter.api.SearchOperations;
import org.springframework.social.twitter.api.TimelineOperations;
import org.springframework.social.twitter.api.TwitterApi;
import org.springframework.social.twitter.api.UserOperations;
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
		this(new RestTemplate(ClientHttpRequestFactorySelector.getRequestFactory()), false);
	}

	/**
	 * Create a new instance of TwitterTemplate.
	 * @param apiKey the application's API key
	 * @param apiSecret the application's API secret
	 * @param accessToken an access token acquired through OAuth authentication with Twitter
	 * @param accessTokenSecret an access token secret acquired through OAuth authentication with Twitter
	 */
	public TwitterTemplate(String apiKey, String apiSecret, String accessToken, String accessTokenSecret) {
		this(ProtectedResourceClientFactory.create(apiKey, apiSecret, accessToken, accessTokenSecret), true);
	}
	
	private TwitterTemplate(RestTemplate restTemplate, boolean isAuthorizedForUser) {
		this.restTemplate = restTemplate;
		this.isAuthorizedForUser = isAuthorizedForUser;
		registerTwitterModule(restTemplate);
		restTemplate.setErrorHandler(new TwitterErrorHandler());
		this.userOperations = new UserTemplate(restTemplate, isAuthorizedForUser);
		this.directMessageOperations = new DirectMessageTemplate(restTemplate, isAuthorizedForUser);
		this.friendOperations = new FriendTemplate(restTemplate, isAuthorizedForUser);
		this.listOperations = new ListTemplate(restTemplate, isAuthorizedForUser);
		this.timelineOperations = new TimelineTemplate(restTemplate, isAuthorizedForUser);
		this.searchOperations = new SearchTemplate(restTemplate, isAuthorizedForUser);
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
		
	// private helper 

	private void registerTwitterModule(RestTemplate restTemplate) {
		List<HttpMessageConverter<?>> converters = restTemplate.getMessageConverters();
		for (HttpMessageConverter<?> converter : converters) {
			if(converter instanceof MappingJacksonHttpMessageConverter) {
				MappingJacksonHttpMessageConverter jsonConverter = (MappingJacksonHttpMessageConverter) converter;
				ObjectMapper objectMapper = new ObjectMapper();				
				objectMapper.registerModule(new TwitterModule());
				jsonConverter.setObjectMapper(objectMapper);
			}
		}
	}
	
	// subclassing hooks

	protected RestTemplate getRestTemplate() {
		return restTemplate;
	}

}
