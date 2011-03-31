/*
 * Copyright 2011 the original author or authors.
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
import org.springframework.social.ResponseStatusCodeTranslator;
import org.springframework.social.SocialException;
import org.springframework.social.twitter.support.extractors.TwitterProfileResponseExtractor;
import org.springframework.social.twitter.types.TwitterProfile;
import org.springframework.web.client.RestTemplate;

/**
 * Implementation of {@link FriendsApiTemplate}, providing a binding to Twitter's friends and followers-oriented REST resources.
 * @author Craig Walls
 */
public class FriendsApiTemplate implements FriendsApi {
	
	private final RestTemplate restTemplate;
	private final ResponseStatusCodeTranslator statusCodeTranslator;
	private TwitterProfileResponseExtractor profileExtractor;

	public FriendsApiTemplate(RestTemplate restTemplate, ResponseStatusCodeTranslator statusCodeTranslator) {
		this.restTemplate = restTemplate;
		this.statusCodeTranslator = statusCodeTranslator;
		this.profileExtractor = new TwitterProfileResponseExtractor();
	}

	public List<TwitterProfile> getFriends(long userId) {
		return getFriendsOrFollowers(FRIENDS_STATUSES_URL + "?user_id={user_id}", userId);
	}

	public List<TwitterProfile> getFriends(String screenName) {
		return getFriendsOrFollowers(FRIENDS_STATUSES_URL + "?screen_name={screen_name}", screenName);
	}
	
	@SuppressWarnings("unchecked")
	public List<Long> getFriendIds(long userId) {
		return restTemplate.getForObject(FRIEND_IDS_URL + "?user_id={userId}", List.class, userId);
	}

	@SuppressWarnings("unchecked")
	public List<Long> getFriendIds(String screenName) {
		return restTemplate.getForObject(FRIEND_IDS_URL + "?screen_name={screenName}", List.class, screenName);
	}

	public List<TwitterProfile> getFollowers(long userId) {
		return getFriendsOrFollowers(FOLLOWERS_STATUSES_URL + "?user_id={user_id}", userId);
	}

	public List<TwitterProfile> getFollowers(String screenName) {
		return getFriendsOrFollowers(FOLLOWERS_STATUSES_URL + "?screen_name={screen_name}", screenName);
	}

	@SuppressWarnings("unchecked")
	public List<Long> getFollowerIds(long userId) {
		return restTemplate.getForObject(FOLLOWER_IDS_URL + "?user_id={userId}", List.class, userId);
	}

	@SuppressWarnings("unchecked")
	public List<Long> getFollowerIds(String screenName) {
		return restTemplate.getForObject(FOLLOWER_IDS_URL + "?screen_name={screenName}", List.class, screenName);
	}

	public String follow(long userId) {
		return this.friendshipAssist(FOLLOW_URL + "?user_id={user_id}", userId);
	}

	public String follow(String screenName) {
		return this.friendshipAssist(FOLLOW_URL + "?screen_name={screen_name}", screenName);
	}
	
	public String unfollow(long userId) {
		return this.friendshipAssist(UNFOLLOW_URL + "?user_id={user_id}", userId);
	}

	public String unfollow(String screenName) {
		return this.friendshipAssist(UNFOLLOW_URL + "?screen_name={screen_name}", screenName);
	}
	
	public boolean friendshipExists(String userA, String userB) {
		return restTemplate.getForObject(EXISTS_URL, boolean.class, userA, userB);
	}

	@SuppressWarnings("unchecked")
	public List<Long> getIncomingFriendships() {
		Map<String, Object> incomingMap = restTemplate.getForObject(FRIENDSHIPS_INCOMING_URL, Map.class);
		return (List<Long>) incomingMap.get("ids");
	}

	@SuppressWarnings("unchecked")
	public List<Long> getOutgoingFriendships() {
		Map<String, Object> outgoingMap = restTemplate.getForObject(FRIENDSHIPS_OUTGOING_URL, Map.class);
		return (List<Long>) outgoingMap.get("ids");
	}

	@SuppressWarnings("unchecked")
	private List<TwitterProfile> getFriendsOrFollowers(String url, Object... urlArgs) {
		return profileExtractor.extractObjects((List<Map<String, Object>>) restTemplate.getForObject(url, List.class, urlArgs));
	}

	@SuppressWarnings("unchecked")
	private String friendshipAssist(String url, Object urlArgs) {
		@SuppressWarnings("rawtypes")
		ResponseEntity<Map> response = restTemplate.postForEntity(url, "", Map.class, urlArgs);
		handleResponseErrors(response);
        Map<String, Object> body = response.getBody();
        return (String) body.get("screen_name");
	}	
	
	@SuppressWarnings("rawtypes")
	private void handleResponseErrors(ResponseEntity<Map> response) {
		SocialException exception = statusCodeTranslator.translate(response);
		if (exception != null) {
			throw exception;
		}
	}

	static final String FRIEND_IDS_URL = TwitterTemplate.API_URL_BASE + "friends/ids.json";
	static final String FOLLOWER_IDS_URL = TwitterTemplate.API_URL_BASE + "followers/ids.json";
	static final String FRIENDS_STATUSES_URL = TwitterTemplate.API_URL_BASE + "statuses/friends.json";
	static final String FOLLOWERS_STATUSES_URL = TwitterTemplate.API_URL_BASE + "statuses/followers.json";
	static final String FOLLOW_URL = TwitterTemplate.API_URL_BASE + "friendships/create.json";
	static final String UNFOLLOW_URL = TwitterTemplate.API_URL_BASE + "friendships/destroy.json";
	static final String EXISTS_URL = TwitterTemplate.API_URL_BASE + "friendships/exists.json?user_a={user_a}&user_b={user_b}";
	static final String FRIENDSHIPS_INCOMING_URL = TwitterTemplate.API_URL_BASE + "friendships/incoming.json";
	static final String FRIENDSHIPS_OUTGOING_URL = TwitterTemplate.API_URL_BASE + "friendships/outgoing.json";
}
