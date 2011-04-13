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
import java.util.TreeMap;

import org.springframework.social.twitter.support.json.LongIdsList;
import org.springframework.social.twitter.support.json.LongList;
import org.springframework.social.twitter.support.json.TwitterProfileList;
import org.springframework.social.twitter.types.TwitterProfile;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

/**
 * Implementation of {@link FriendTemplate}, providing a binding to Twitter's friends and followers-oriented REST resources.
 * @author Craig Walls
 */
class FriendTemplate extends AbstractTwitterOperations implements FriendOperations {
	
	public FriendTemplate(LowLevelTwitterApi lowLevelApi) {
		super(lowLevelApi);
	}

	public List<TwitterProfile> getFriends(long userId) {
		return getLowLevelTwitterApi().fetchObject("statuses/friends.json", TwitterProfileList.class, Collections.singletonMap("user_id", String.valueOf(userId))).getList();
	}

	public List<TwitterProfile> getFriends(String screenName) {
		return getLowLevelTwitterApi().fetchObject("statuses/friends.json", TwitterProfileList.class, Collections.singletonMap("screen_name", screenName)).getList();
	}
	
	public List<Long> getFriendIds(long userId) {
		return getLowLevelTwitterApi().fetchObject("friends/ids.json", LongList.class, Collections.singletonMap("user_id", String.valueOf(userId))).getList();
	}

	public List<Long> getFriendIds(String screenName) {
		return getLowLevelTwitterApi().fetchObject("friends/ids.json", LongList.class, Collections.singletonMap("screen_name", screenName)).getList();
	}

	public List<TwitterProfile> getFollowers(long userId) {
		return getLowLevelTwitterApi().fetchObject("statuses/followers.json", TwitterProfileList.class, Collections.singletonMap("user_id", String.valueOf(userId))).getList();
	}

	public List<TwitterProfile> getFollowers(String screenName) {
		return getLowLevelTwitterApi().fetchObject("statuses/followers.json", TwitterProfileList.class, Collections.singletonMap("screen_name", screenName)).getList();
	}

	public List<Long> getFollowerIds(long userId) {
		return getLowLevelTwitterApi().fetchObject("followers/ids.json", LongList.class, Collections.singletonMap("user_id", String.valueOf(userId))).getList();
	}

	public List<Long> getFollowerIds(String screenName) {
		return getLowLevelTwitterApi().fetchObject("followers/ids.json", LongList.class, Collections.singletonMap("screen_name", screenName)).getList();
	}

	public String follow(long userId) {
		requireUserAuthorization();
		return (String) getLowLevelTwitterApi().publish("friendships/create.json", EMPTY_DATA, Map.class, Collections.singletonMap("user_id", String.valueOf(userId))).get("screen_name");
	}

	public String follow(String screenName) {
		requireUserAuthorization();
		return (String) getLowLevelTwitterApi().publish("friendships/create.json", EMPTY_DATA, Map.class, Collections.singletonMap("screen_name", screenName)).get("screen_name");
	}
	
	public String unfollow(long userId) {
		requireUserAuthorization();
		return (String) getLowLevelTwitterApi().publish("friendships/destroy.json", EMPTY_DATA, Map.class, Collections.singletonMap("user_id", String.valueOf(userId))).get("screen_name");
	}

	public String unfollow(String screenName) {
		requireUserAuthorization();
		return (String) getLowLevelTwitterApi().publish("friendships/destroy.json", EMPTY_DATA, Map.class, Collections.singletonMap("screen_name", screenName)).get("screen_name");
	}
	
	// doesn't require authentication
	public boolean friendshipExists(String userA, String userB) {
		requireUserAuthorization();
		Map<String, String> params = new TreeMap<String, String>();
		params.put("user_a", userA);
		params.put("user_b", userB);
		return getLowLevelTwitterApi().fetchObject("friendships/exists.json", boolean.class, params);
	}

	public List<Long> getIncomingFriendships() {
		requireUserAuthorization();
		return getLowLevelTwitterApi().fetchObject("friendships/incoming.json", LongIdsList.class).getList();
	}

	public List<Long> getOutgoingFriendships() {
		requireUserAuthorization();
		return getLowLevelTwitterApi().fetchObject("friendships/outgoing.json", LongIdsList.class).getList();
	}

	private static final MultiValueMap<String, Object> EMPTY_DATA = new LinkedMultiValueMap<String, Object>();
	
}
