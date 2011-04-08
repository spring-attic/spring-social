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

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.springframework.social.twitter.support.extractors.ListOfLongExtractor;
import org.springframework.social.twitter.support.extractors.TwitterProfileResponseExtractor;
import org.springframework.social.twitter.types.TwitterProfile;
import org.springframework.web.client.RestTemplate;

/**
 * Implementation of {@link FriendTemplate}, providing a binding to Twitter's friends and followers-oriented REST resources.
 * @author Craig Walls
 */
class FriendTemplate implements FriendOperations {
	
	private final RestTemplate restTemplate;
	
	private TwitterProfileResponseExtractor profileExtractor;
	
	private final LowLevelTwitterApi requestApi;

	public FriendTemplate(LowLevelTwitterApi lowLevelApi, RestTemplate restTemplate) {
		this.requestApi = lowLevelApi;
		this.restTemplate = restTemplate;
		this.profileExtractor = new TwitterProfileResponseExtractor();
	}

	public List<TwitterProfile> getFriends(long userId) {
		return requestApi.fetchObjects("statuses/friends.json", profileExtractor, Collections.singletonMap("user_id", String.valueOf(userId)));
	}

	public List<TwitterProfile> getFriends(String screenName) {
		return requestApi.fetchObjects("statuses/friends.json", profileExtractor, Collections.singletonMap("screen_name", screenName));
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
		return requestApi.fetchObjects("statuses/followers.json", profileExtractor, Collections.singletonMap("user_id", String.valueOf(userId)));
	}

	public List<TwitterProfile> getFollowers(String screenName) {
		return requestApi.fetchObjects("statuses/followers.json", profileExtractor, Collections.singletonMap("screen_name", screenName));
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

	public List<Long> getIncomingFriendships() {
		return requestApi.fetchObject("friendships/incoming.json", new ListOfLongExtractor("ids"));
	}

	public List<Long> getOutgoingFriendships() {
		return requestApi.fetchObject("friendships/outgoing.json", new ListOfLongExtractor("ids"));
	}

	@SuppressWarnings("unchecked")
	private String friendshipAssist(String url, Object urlArgs) {
		Map<String, Object> response = restTemplate.postForObject(url, "", Map.class, urlArgs);
        return (String) response.get("screen_name");
	}	

	private static final String FRIEND_IDS_URL = TwitterTemplate.API_URL_BASE + "friends/ids.json";
	private static final String FOLLOWER_IDS_URL = TwitterTemplate.API_URL_BASE + "followers/ids.json";
	private static final String FOLLOW_URL = TwitterTemplate.API_URL_BASE + "friendships/create.json";
	private static final String UNFOLLOW_URL = TwitterTemplate.API_URL_BASE + "friendships/destroy.json";
	private static final String EXISTS_URL = TwitterTemplate.API_URL_BASE + "friendships/exists.json?user_a={user_a}&user_b={user_b}";
}
