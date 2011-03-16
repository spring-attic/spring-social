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

import org.springframework.social.AccountNotConnectedException;
import org.springframework.social.ResponseStatusCodeTranslator;
import org.springframework.social.oauth1.ProtectedResourceClientFactory;
import org.springframework.social.twitter.support.DirectMessageApiTemplate;
import org.springframework.social.twitter.support.FriendsApiTemplate;
import org.springframework.social.twitter.support.SearchApiTemplate;
import org.springframework.social.twitter.support.TweetApiTemplate;
import org.springframework.social.twitter.support.TwitterResponseStatusCodeTranslator;
import org.springframework.social.twitter.support.UserApiTemplate;
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
public class TwitterTemplate implements TwitterApi {

	private final RestTemplate restTemplate;

	private final ResponseStatusCodeTranslator statusCodeTranslator;

	/**
	 * Create a new instance of TwitterTemplate.
	 * This constructor creates a new TwitterTemplate able to perform unauthenticated operations against Twitter's API.
	 * Some operations, such as search, do not require OAuth authentication.
	 * A TwitterTemplate created with this constructor will support those operations.
	 * Those operations requiring authentication will throw {@link AccountNotConnectedException}.
	 */
	public TwitterTemplate() {
		restTemplate = new RestTemplate();
		restTemplate.setErrorHandler(new TwitterErrorHandler());
		this.statusCodeTranslator = new TwitterResponseStatusCodeTranslator();
	}

	/**
	 * Create a new instance of TwitterTemplate.
	 * @param apiKey the application's API key
	 * @param apiSecret the application's API secret
	 * @param accessToken an access token acquired through OAuth authentication with LinkedIn
	 * @param accessTokenSecret an access token secret acquired through OAuth authentication with LinkedIn
	 */
	public TwitterTemplate(String apiKey, String apiSecret, String accessToken, String accessTokenSecret) {
		restTemplate = ProtectedResourceClientFactory.create(apiKey, apiSecret, accessToken, accessTokenSecret);
		restTemplate.setErrorHandler(new TwitterErrorHandler());
		this.statusCodeTranslator = new TwitterResponseStatusCodeTranslator();
	}

	public TweetApi tweetApi() {
		return new TweetApiTemplate(restTemplate, statusCodeTranslator);
	}

	public FriendsApi friendsApi() {
		return new FriendsApiTemplate(restTemplate);
	}

	public SearchApi searchApi() {
		return new SearchApiTemplate(restTemplate);
	}

	public DirectMessageApi directMessageApi() {
		return new DirectMessageApiTemplate(restTemplate, statusCodeTranslator);
	}

	public UserApi userApi() {
		return new UserApiTemplate(restTemplate);
	}

	public List<String> getFriends(String screenName) {
		List<Map<String, String>> response = restTemplate.getForObject(FRIENDS_STATUSES_URL, List.class, Collections.singletonMap("screen_name", screenName));
		List<String> friends = new ArrayList<String>(response.size());
		for (Map<String, String> item : response) {
			friends.add(item.get("screen_name"));
		}
		return friends;
	}
	
	public List<String> getFollowers(String screenName) {
	    List<Map<String, String>> response = restTemplate.getForObject(FOLLOWERS_STATUSES_URL, List.class, Collections.singletonMap("screen_name", screenName));
	    List<String> followers = new ArrayList<String>(response.size());
	    for(Map<String, String> item : response) {
	        followers.add(item.get("screen_name"));
	    }
	    
	    return followers;
	    
	}
	
	public String follow(String screenName) {
	    return this.friendshipAssist(FOLLOW_URL, screenName);	    
	}
	
	public String unfollow(String screenName) {
	    return this.friendshipAssist(UNFOLLOW_URL, screenName);
	}
	
	private String friendshipAssist(String url, String screenName) {
	    ResponseEntity<Map> response = restTemplate.postForEntity(url, "", Map.class, Collections.singletonMap("screen_name", screenName));
        handleResponseErrors(response);
        Map<String, Object> body = response.getBody();
        return (String) body.get("screen_name");
	}

	// subclassing hooks

	protected RestTemplate getRestTemplate() {
		return restTemplate;
	}

	public static final String API_URL_BASE = "https://api.twitter.com/1/";
}
