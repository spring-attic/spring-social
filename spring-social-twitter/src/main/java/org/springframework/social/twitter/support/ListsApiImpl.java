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
package org.springframework.social.twitter.support;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.social.twitter.ListsApi;
import org.springframework.social.twitter.Tweet;
import org.springframework.social.twitter.TwitterProfile;
import org.springframework.social.twitter.TwitterTemplate;
import org.springframework.social.twitter.UserList;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

/**
 * Implementation of {@link ListsApi}, providing a binding to Twitter's list-oriented REST resources.
 * @author Craig Walls
 */
public class ListsApiImpl implements ListsApi {

	private final RestTemplate restTemplate;

	public ListsApiImpl(RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
	}

	public List<UserList> getLists(long userId) {
		return getTwitterLists(USER_LISTS_URL, userId);
	}

	public List<UserList> getLists(String screenName) {
		return getTwitterLists(USER_LISTS_URL, screenName);
	}

	public UserList getList(long userId, long listId) {
		return getTwitterList(userId, listId);
	}

	public UserList getList(String screenName, String listSlug) {
		return getTwitterList(screenName, listSlug);
	}

	public List<Tweet> getListStatuses(long userId, long listId) {
		return TwitterResponseHelper.extractTimelineTweetsFromResponse(restTemplate.getForObject(LIST_STATUSES_URL, List.class, userId, listId));
	}

	public List<Tweet> getListStatuses(String screenName, String listSlug) {
		return TwitterResponseHelper.extractTimelineTweetsFromResponse(restTemplate.getForObject(LIST_STATUSES_URL, List.class, screenName, listSlug));
	}

	public UserList createList(long userId, String name, String description, boolean isPublic) {
		return saveList(USER_LISTS_URL, name, description, isPublic, userId);
	}

	public UserList createList(String screenName, String name, String description, boolean isPublic) {
		return saveList(USER_LISTS_URL, name, description, isPublic, screenName);
	}

	public UserList updateList(long userId, long listId, String name, String description, boolean isPublic) {
		return saveList(USER_LIST_URL, name, description, isPublic, userId, listId);
	}

	public UserList updateList(String screenName, String listSlug, String name, String description, boolean isPublic) {
		return saveList(USER_LIST_URL, name, description, isPublic, screenName, listSlug);
	}

	public void deleteList(long userId, long listId) {
		deleteTwitterList(userId, listId);
	}

	public void deleteList(String screenName, String listSlug) {
		deleteTwitterList(screenName, listSlug);
	}

	public List<TwitterProfile> getListMembers(long userId, long listId) {
		return getListConnections(LIST_MEMBERS_URL, userId, listId);
	}
	
	public List<TwitterProfile> getListMembers(String screenName, String listSlug) {
		return getListConnections(LIST_MEMBERS_URL, screenName, listSlug);
	}

	public UserList addToList(long userId, long listId, long... newMemberIds) {
		return addMembersToList("user_id", ArrayUtils.join(newMemberIds), userId, listId);
	}

	public UserList addToList(String screenName, String listSlug, String... newMemberScreenNames) {
		return addMembersToList("screen_name", ArrayUtils.join(newMemberScreenNames), screenName, listSlug);
	}

	public void removeFromList(long userId, long listId, long memberId) {
		restTemplate.delete(LIST_MEMBERS_URL + "?id={memberId}", userId, listId, memberId);
	}

	public void removeFromList(String screenName, String listSlug, String memberScreenName) {
		restTemplate.delete(LIST_MEMBERS_URL + "?id={memberId}", screenName, listSlug, memberScreenName);
	}

	public List<TwitterProfile> getListSubscribers(long userId, long listId) {
		return getListConnections(LIST_SUBSCRIBERS_URL, userId, listId);
	}

	public List<TwitterProfile> getListSubscribers(String screenName, String listSlug) {
		return getListConnections(LIST_SUBSCRIBERS_URL, screenName, listSlug);
	}

	public UserList subscribe(long ownerId, long listId) {
		Map<String, Object> response  = restTemplate.postForObject(LIST_SUBSCRIBERS_URL, "", Map.class, ownerId, listId);
		return extractedTwitterListFromResponseMap(response);
	}

	public UserList subscribe(String ownerScreenName, String listSlug) {
		Map<String, Object> response  = restTemplate.postForObject(LIST_SUBSCRIBERS_URL, "", Map.class, ownerScreenName, listSlug);
		return extractedTwitterListFromResponseMap(response);
	}

	public void unsubscribe(long ownerId, long listId) {
		restTemplate.delete(LIST_SUBSCRIBERS_URL, ownerId, listId);
	}

	public void unsubscribe(String ownerScreenName, String listSlug) {
		restTemplate.delete(LIST_SUBSCRIBERS_URL, ownerScreenName, listSlug);
	}

	public List<UserList> getMemberships(long userId) {
		return getTwitterLists(MEMBERSHIPS_URL, userId);
	}

	public List<UserList> getMemberships(String screenName) {
		return getTwitterLists(MEMBERSHIPS_URL, screenName);
	}

	public List<UserList> getSubscriptions(long userId) {
		return getTwitterLists(SUBSCRIPTIONS_URL, userId);
	}

	public List<UserList> getSubscriptions(String screenName) {
		return getTwitterLists(SUBSCRIPTIONS_URL, screenName);
	}

	public boolean isMember(long userId, long listId, long memberId) {
		return checkListConnection(CHECK_MEMBER_URL, userId, listId, memberId);
	}

	public boolean isMember(String screenName, String listSlug, String memberScreenName) {
		return checkListConnection(CHECK_MEMBER_URL, screenName, listSlug, memberScreenName);
	}

	public boolean isSubscriber(long userId, long listId, long subscriberId) {
		return checkListConnection(CHECK_SUBSCRIBER_URL, userId, listId, subscriberId);
	}

	public boolean isSubscriber(String screenName, String listSlug, String subscriberScreenName) {
		return checkListConnection(CHECK_SUBSCRIBER_URL, screenName, listSlug, subscriberScreenName);
	}

	private boolean checkListConnection(String url, Object... urlArgs) {
		try {
			restTemplate.getForObject(url, String.class, urlArgs);
			return true;
		} catch (HttpClientErrorException e) {
			if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
				return false;
			}
			throw e;
		}
	}

	// private helpers

	private List<UserList> getTwitterLists(String url, Object... urlArgs) {
		Map<String, Object> response = restTemplate.getForObject(url, Map.class, urlArgs);
		List<Map<String, Object>> listsList = (List<Map<String, Object>>) response.get("lists");
		List<UserList> lists = new ArrayList<UserList>(listsList.size());
		for (Map<String, Object> listMap : listsList) {
			lists.add(extractedTwitterListFromResponseMap(listMap));
		}
		return lists;
	}

	private UserList getTwitterList(Object... urlArgs) {
		Map<String, Object> response = restTemplate.getForObject(USER_LIST_URL, Map.class, urlArgs);
		return extractedTwitterListFromResponseMap(response);
	}

	private UserList saveList(String url, String name, String description, boolean isPublic, Object... urlArgs) {
		MultiValueMap<String, String> request = new LinkedMultiValueMap<String, String>();
		request.set("name", name);
		request.set("description", description);
		request.set("mode", isPublic ? "public" : "private");
		Map<String, Object> response = restTemplate.postForObject(url, request, Map.class, urlArgs);
		return extractedTwitterListFromResponseMap(response);
	}

	private void deleteTwitterList(Object... urlArgs) {
		restTemplate.delete(USER_LIST_URL, urlArgs);
	}

	private List<TwitterProfile> getListConnections(String relationshipUrl, Object... urlArgs) {
		Map<String, Object> response = restTemplate.getForObject(relationshipUrl, Map.class, urlArgs);
		List<Map<String, Object>> profileMapList = (List<Map<String, Object>>) response.get("users");
		List<TwitterProfile> members = new ArrayList<TwitterProfile>();
		for (Map<String, Object> profileMap : profileMapList) {
			members.add(TwitterResponseHelper.getProfileFromResponseMap(profileMap));
		}
		return members;
	}

	private UserList addMembersToList(String fieldName, String joinedIds, Object... urlArgs) {
		MultiValueMap<String, String> request = new LinkedMultiValueMap<String, String>();
		request.set(fieldName, joinedIds);
		Map<String, Object> response = restTemplate.postForObject(CREATE_ALL_URL, request, Map.class, urlArgs);
		return extractedTwitterListFromResponseMap(response);
	}

	private UserList extractedTwitterListFromResponseMap(Map<String, Object> listMap) {
		long id = Long.valueOf(String.valueOf(listMap.get("id")));
		String fullName = String.valueOf(listMap.get("full_name"));
		String name = String.valueOf(listMap.get("name"));
		String description = String.valueOf(listMap.get("description"));
		String slug = String.valueOf(listMap.get("slug"));
		boolean isPublic = String.valueOf(listMap.get("mode")).equals("public");
		boolean isFollowing = Boolean.valueOf(String.valueOf(listMap.get("following")));
		int memberCount = Integer.valueOf(String.valueOf(listMap.get("member_count")));
		int subscriberCount = Integer.valueOf(String.valueOf(listMap.get("subscriber_count")));
		String uriPath = String.valueOf(listMap.get("uri"));
		UserList twitterList = new UserList(id, name, fullName, uriPath, description, slug, isPublic, isFollowing, memberCount, subscriberCount);
		return twitterList;
	}

	static final String USER_LISTS_URL = TwitterTemplate.API_URL_BASE + "{user_id}/lists.json";
	static final String USER_LIST_URL = TwitterTemplate.API_URL_BASE + "{user_id}/lists/{list_id}.json";
	static final String LIST_STATUSES_URL = TwitterTemplate.API_URL_BASE + "{user_id}/lists/{list_id}/statuses.json";
	static final String LIST_MEMBERS_URL = TwitterTemplate.API_URL_BASE + "{user_id}/{list_id}/members.json";
	static final String LIST_SUBSCRIBERS_URL = TwitterTemplate.API_URL_BASE + "{user_id}/{list_id}/subscribers.json";
	static final String CREATE_ALL_URL = TwitterTemplate.API_URL_BASE + "{user_id}/{list_id}/members/create_all.json";
	static final String CHECK_MEMBER_URL = TwitterTemplate.API_URL_BASE + "{user_id}/{list_id}/members/{member_id}.json";
	static final String CHECK_SUBSCRIBER_URL = TwitterTemplate.API_URL_BASE + "{user_id}/{list_id}/subscribers/{subscriber_id}.json";
	static final String MEMBERSHIPS_URL = TwitterTemplate.API_URL_BASE + "{user_id}/lists/memberships.json";
	static final String SUBSCRIPTIONS_URL = TwitterTemplate.API_URL_BASE + "{user_id}/lists/subscriptions.json";

}
