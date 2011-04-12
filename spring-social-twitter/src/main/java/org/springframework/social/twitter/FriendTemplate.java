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

import org.springframework.social.twitter.support.extractors.AbstractResponseExtractor;
import org.springframework.social.twitter.support.extractors.ListOfLongExtractor;
import org.springframework.social.twitter.support.extractors.TwitterProfileResponseExtractor;
import org.springframework.social.twitter.types.TwitterProfile;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

/**
 * Implementation of {@link FriendTemplate}, providing a binding to Twitter's friends and followers-oriented REST resources.
 * @author Craig Walls
 */
class FriendTemplate extends AbstractTwitterOperations implements FriendOperations {
		
	private TwitterProfileResponseExtractor profileExtractor;
	
	private final MapExtractor mapExtractor;

	public FriendTemplate(LowLevelTwitterApi lowLevelApi) {
		super(lowLevelApi);
		this.profileExtractor = new TwitterProfileResponseExtractor();
		this.mapExtractor = new MapExtractor();
	}

	public List<TwitterProfile> getFriends(long userId) {
		return getLowLevelTwitterApi().fetchObjects("statuses/friends.json", profileExtractor, Collections.singletonMap("user_id", String.valueOf(userId)));
	}

	public List<TwitterProfile> getFriends(String screenName) {
		return getLowLevelTwitterApi().fetchObjects("statuses/friends.json", profileExtractor, Collections.singletonMap("screen_name", screenName));
	}
	
	@SuppressWarnings("unchecked")
	public List<Long> getFriendIds(long userId) {
		return getLowLevelTwitterApi().fetchObject("friends/ids.json", List.class, Collections.singletonMap("user_id", String.valueOf(userId)));
	}

	@SuppressWarnings("unchecked")
	public List<Long> getFriendIds(String screenName) {
		return getLowLevelTwitterApi().fetchObject("friends/ids.json", List.class, Collections.singletonMap("screen_name", screenName));
	}

	public List<TwitterProfile> getFollowers(long userId) {
		return getLowLevelTwitterApi().fetchObjects("statuses/followers.json", profileExtractor, Collections.singletonMap("user_id", String.valueOf(userId)));
	}

	public List<TwitterProfile> getFollowers(String screenName) {
		return getLowLevelTwitterApi().fetchObjects("statuses/followers.json", profileExtractor, Collections.singletonMap("screen_name", screenName));
	}

	@SuppressWarnings("unchecked")
	public List<Long> getFollowerIds(long userId) {
		return getLowLevelTwitterApi().fetchObject("followers/ids.json", List.class, Collections.singletonMap("user_id", String.valueOf(userId)));
	}

	@SuppressWarnings("unchecked")
	public List<Long> getFollowerIds(String screenName) {
		return getLowLevelTwitterApi().fetchObject("followers/ids.json", List.class, Collections.singletonMap("screen_name", screenName));
	}

	public String follow(long userId) {
		requireUserAuthorization();
		return (String) getLowLevelTwitterApi().publish("friendships/create.json", EMPTY_DATA, mapExtractor, Collections.singletonMap("user_id", String.valueOf(userId))).get("screen_name");
	}

	public String follow(String screenName) {
		requireUserAuthorization();
		return (String) getLowLevelTwitterApi().publish("friendships/create.json", EMPTY_DATA, mapExtractor, Collections.singletonMap("screen_name", screenName)).get("screen_name");
	}
	
	public String unfollow(long userId) {
		requireUserAuthorization();
		return (String) getLowLevelTwitterApi().publish("friendships/destroy.json", EMPTY_DATA, mapExtractor, Collections.singletonMap("user_id", String.valueOf(userId))).get("screen_name");
	}

	public String unfollow(String screenName) {
		requireUserAuthorization();
		return (String) getLowLevelTwitterApi().publish("friendships/destroy.json", EMPTY_DATA, mapExtractor, Collections.singletonMap("screen_name", screenName)).get("screen_name");
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
		return getLowLevelTwitterApi().fetchObject("friendships/incoming.json", new ListOfLongExtractor("ids"));
	}

	public List<Long> getOutgoingFriendships() {
		requireUserAuthorization();
		return getLowLevelTwitterApi().fetchObject("friendships/outgoing.json", new ListOfLongExtractor("ids"));
	}

	private static final MultiValueMap<String, Object> EMPTY_DATA = new LinkedMultiValueMap<String, Object>();
	
	private static class MapExtractor extends AbstractResponseExtractor<Map<String, Object>> {
		public Map<String, Object> extractObject(Map<String, Object> responseMap) {
			return responseMap;
		}
	}
	
}
