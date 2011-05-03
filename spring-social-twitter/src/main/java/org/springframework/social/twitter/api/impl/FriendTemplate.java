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

import java.util.List;
import java.util.Map;

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

	public List<TwitterProfile> getFriends() {
		return restTemplate.getForObject(buildUri("statuses/friends.json", "cursor", "-1"), TwitterProfileUsersList.class).getList();
	}

	public List<TwitterProfile> getFriends(long userId) {
		LinkedMultiValueMap<String, String> parameters = new LinkedMultiValueMap<String, String>();
		parameters.set("cursor", "-1");
		parameters.set("user_id", String.valueOf(userId));
		return restTemplate.getForObject(buildUri("statuses/friends.json", parameters), TwitterProfileUsersList.class).getList();
	}

	public List<TwitterProfile> getFriends(String screenName) {
		LinkedMultiValueMap<String, String> parameters = new LinkedMultiValueMap<String, String>();
		parameters.set("cursor", "-1");
		parameters.set("screen_name", screenName);
		return restTemplate.getForObject(buildUri("statuses/friends.json", parameters), TwitterProfileUsersList.class).getList();
	}
	
	public List<Long> getFriendIds() {
		return restTemplate.getForObject(buildUri("friends/ids.json", "cursor", "-1"), LongIdsList.class).getList();
	}

	public List<Long> getFriendIds(long userId) {
		LinkedMultiValueMap<String, String> parameters = new LinkedMultiValueMap<String, String>();
		parameters.set("cursor", "-1");
		parameters.set("user_id", String.valueOf(userId));
		return restTemplate.getForObject(buildUri("friends/ids.json", parameters), LongIdsList.class).getList();
	}

	public List<Long> getFriendIds(String screenName) {
		LinkedMultiValueMap<String, String> parameters = new LinkedMultiValueMap<String, String>();
		parameters.set("cursor", "-1");
		parameters.set("screen_name", screenName);
		return restTemplate.getForObject(buildUri("friends/ids.json", parameters), LongIdsList.class).getList();
	}

	public List<TwitterProfile> getFollowers() {
		return restTemplate.getForObject(buildUri("statuses/followers.json", "cursor", "-1"), TwitterProfileUsersList.class).getList();
	}

	public List<TwitterProfile> getFollowers(long userId) {
		LinkedMultiValueMap<String, String> parameters = new LinkedMultiValueMap<String, String>();
		parameters.set("cursor", "-1");
		parameters.set("user_id", String.valueOf(userId));
		return restTemplate.getForObject(buildUri("statuses/followers.json", parameters), TwitterProfileUsersList.class).getList();
	}

	public List<TwitterProfile> getFollowers(String screenName) {
		LinkedMultiValueMap<String, String> parameters = new LinkedMultiValueMap<String, String>();
		parameters.set("cursor", "-1");
		parameters.set("screen_name", screenName);
		return restTemplate.getForObject(buildUri("statuses/followers.json", parameters), TwitterProfileUsersList.class).getList();
	}

	public List<Long> getFollowerIds() {
		return restTemplate.getForObject(buildUri("followers/ids.json", "cursor", "-1"), LongIdsList.class).getList();
	}

	public List<Long> getFollowerIds(long userId) {
		LinkedMultiValueMap<String, String> parameters = new LinkedMultiValueMap<String, String>();
		parameters.set("cursor", "-1");
		parameters.set("user_id", String.valueOf(userId));
		return restTemplate.getForObject(buildUri("followers/ids.json", parameters), LongIdsList.class).getList();
	}

	public List<Long> getFollowerIds(String screenName) {
		LinkedMultiValueMap<String, String> parameters = new LinkedMultiValueMap<String, String>();
		parameters.set("cursor", "-1");
		parameters.set("screen_name", screenName);
		return restTemplate.getForObject(buildUri("followers/ids.json", parameters), LongIdsList.class).getList();
	}

	public String follow(long userId) {
		requireUserAuthorization();
		return (String) restTemplate.postForObject(buildUri("friendships/create.json", "user_id", String.valueOf(userId)), EMPTY_DATA, Map.class).get("screen_name");
	}

	public String follow(String screenName) {
		requireUserAuthorization();
		return (String) restTemplate.postForObject(buildUri("friendships/create.json", "screen_name", screenName), EMPTY_DATA, Map.class).get("screen_name");
	}
	
	public String unfollow(long userId) {
		requireUserAuthorization();
		return (String) restTemplate.postForObject(buildUri("friendships/destroy.json", "user_id", String.valueOf(userId)), EMPTY_DATA, Map.class).get("screen_name");
	}

	public String unfollow(String screenName) {
		requireUserAuthorization();
		return (String) restTemplate.postForObject(buildUri("friendships/destroy.json", "screen_name", screenName), EMPTY_DATA, Map.class).get("screen_name");
	}
	
	// doesn't require authentication
	public boolean friendshipExists(String userA, String userB) {
		requireUserAuthorization();
		LinkedMultiValueMap<String, String> params = new LinkedMultiValueMap<String, String>();
		params.set("user_a", userA);
		params.set("user_b", userB);
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
	
}
