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
package org.springframework.social.twitter.api.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.springframework.social.twitter.api.FriendOperations;
import org.springframework.social.twitter.api.TwitterProfile;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

/**
 * Implementation of {@link FriendTemplate}, providing a binding to Twitter's friends and followers-oriented REST resources.
 * @author Craig Walls
 */
class FriendTemplate extends AbstractTwitterOperations implements FriendOperations {
	
	private final RestTemplate restTemplate;

	public FriendTemplate(RestTemplate restTemplate, boolean isAuthorizedForUser) {
		super(isAuthorizedForUser);
		this.restTemplate = restTemplate;
	}

	public List<TwitterProfile> getFriends(long userId) {
		return restTemplate.getForObject(buildUri("statuses/friends.json", Collections.singletonMap("user_id", String.valueOf(userId))), TwitterProfileList.class);
	}

	public List<TwitterProfile> getFriends(String screenName) {
		return restTemplate.getForObject(buildUri("statuses/friends.json", Collections.singletonMap("screen_name", screenName)), TwitterProfileList.class);
	}
	
	public List<Long> getFriendIds(long userId) {
		return restTemplate.getForObject(buildUri("friends/ids.json", Collections.singletonMap("user_id", String.valueOf(userId))), LongList.class);
	}

	public List<Long> getFriendIds(String screenName) {
		return restTemplate.getForObject(buildUri("friends/ids.json", Collections.singletonMap("screen_name", screenName)), LongList.class);
	}

	public List<TwitterProfile> getFollowers(long userId) {
		return restTemplate.getForObject(buildUri("statuses/followers.json", Collections.singletonMap("user_id", String.valueOf(userId))), TwitterProfileList.class);
	}

	public List<TwitterProfile> getFollowers(String screenName) {
		return restTemplate.getForObject(buildUri("statuses/followers.json", Collections.singletonMap("screen_name", screenName)), TwitterProfileList.class);
	}

	public List<Long> getFollowerIds(long userId) {
		return restTemplate.getForObject(buildUri("followers/ids.json", Collections.singletonMap("user_id", String.valueOf(userId))), LongList.class);
	}

	public List<Long> getFollowerIds(String screenName) {
		return restTemplate.getForObject(buildUri("followers/ids.json", Collections.singletonMap("screen_name", screenName)), LongList.class);
	}

	public String follow(long userId) {
		requireUserAuthorization();
		return (String) restTemplate.postForObject(buildUri("friendships/create.json", Collections.singletonMap("user_id", String.valueOf(userId))), EMPTY_DATA, Map.class).get("screen_name");
	}

	public String follow(String screenName) {
		requireUserAuthorization();
		return (String) restTemplate.postForObject(buildUri("friendships/create.json", Collections.singletonMap("screen_name", screenName)), EMPTY_DATA, Map.class).get("screen_name");
	}
	
	public String unfollow(long userId) {
		requireUserAuthorization();
		return (String) restTemplate.postForObject(buildUri("friendships/destroy.json", Collections.singletonMap("user_id", String.valueOf(userId))), EMPTY_DATA, Map.class).get("screen_name");
	}

	public String unfollow(String screenName) {
		requireUserAuthorization();
		return (String) restTemplate.postForObject(buildUri("friendships/destroy.json", Collections.singletonMap("screen_name", screenName)), EMPTY_DATA, Map.class).get("screen_name");
	}
	
	// doesn't require authentication
	public boolean friendshipExists(String userA, String userB) {
		requireUserAuthorization();
		Map<String, String> params = new TreeMap<String, String>();
		params.put("user_a", userA);
		params.put("user_b", userB);
		return restTemplate.getForObject(buildUri("friendships/exists.json", params), boolean.class);
	}

	public List<Long> getIncomingFriendships() {
		requireUserAuthorization();
		return restTemplate.getForObject(buildUri("friendships/incoming.json"), LongIdsList.class).getList();
	}

	public List<Long> getOutgoingFriendships() {
		requireUserAuthorization();
		return restTemplate.getForObject(buildUri("friendships/outgoing.json"), LongIdsList.class).getList();
	}

	private static final MultiValueMap<String, Object> EMPTY_DATA = new LinkedMultiValueMap<String, Object>();
	
	@SuppressWarnings("serial")
	private static class LongList extends ArrayList<Long> {}
}
