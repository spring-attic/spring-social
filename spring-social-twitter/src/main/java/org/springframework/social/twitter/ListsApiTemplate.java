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

import org.springframework.http.HttpStatus;
import org.springframework.social.twitter.support.extractors.TweetResponseExtractor;
import org.springframework.social.twitter.support.extractors.TwitterProfileResponseExtractor;
import org.springframework.social.twitter.support.extractors.UserListResponseExtractor;
import org.springframework.social.twitter.types.Tweet;
import org.springframework.social.twitter.types.TwitterProfile;
import org.springframework.social.twitter.types.UserList;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

/**
 * Implementation of {@link ListsApi}, providing a binding to Twitter's list-oriented REST resources.
 * @author Craig Walls
 */
public class ListsApiTemplate implements ListsApi {

	private final RestTemplate restTemplate;
	private final UserApi userApi;
	private TwitterProfileResponseExtractor profileExtractor;
	private TweetResponseExtractor tweetExtractor;
	private UserListResponseExtractor userListExtractor;
	
	public ListsApiTemplate(RestTemplate restTemplate, UserApi userApi) {
		this.restTemplate = restTemplate;
		this.userApi = userApi;
		this.profileExtractor = new TwitterProfileResponseExtractor();
		this.tweetExtractor = new TweetResponseExtractor();
		this.userListExtractor = new UserListResponseExtractor();
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

	@SuppressWarnings("unchecked")
	public List<Tweet> getListStatuses(long userId, long listId) {
		return tweetExtractor.extractObjects(restTemplate.getForObject(LIST_STATUSES_URL, List.class, userId, listId));
	}

	@SuppressWarnings("unchecked")
	public List<Tweet> getListStatuses(String screenName, String listSlug) {
		return tweetExtractor.extractObjects(restTemplate.getForObject(LIST_STATUSES_URL, List.class, screenName, listSlug));
	}

	public UserList createList(String name, String description, boolean isPublic) {
		return saveList(USER_LISTS_URL, name, description, isPublic, userApi.getProfileId());
	}

	public UserList updateList(long listId, String name, String description, boolean isPublic) {
		return saveList(USER_LIST_URL, name, description, isPublic, userApi.getProfileId(), listId);
	}

	public void deleteList(long listId) {
		deleteTwitterList(userApi.getProfileId(), listId);
	}

	public List<TwitterProfile> getListMembers(long userId, long listId) {
		return getListConnections(LIST_MEMBERS_URL, userId, listId);
	}
	
	public List<TwitterProfile> getListMembers(String screenName, String listSlug) {
		return getListConnections(LIST_MEMBERS_URL, screenName, listSlug);
	}

	public UserList addToList(long listId, long... newMemberIds) {
		return addMembersToList("user_id", ArrayUtils.join(newMemberIds), userApi.getProfileId(), listId);
	}

	public UserList addToList(String listSlug, String... newMemberScreenNames) {
		return addMembersToList("screen_name", ArrayUtils.join(newMemberScreenNames), userApi.getProfileId(), listSlug);
	}

	public void removeFromList(long listId, long memberId) {
		restTemplate.delete(LIST_MEMBERS_URL + "?id={memberId}", userApi.getProfileId(), listId, memberId);
	}

	public void removeFromList(String listSlug, String memberScreenName) {
		restTemplate.delete(LIST_MEMBERS_URL + "?id={memberId}", userApi.getProfileId(), listSlug, memberScreenName);
	}

	public List<TwitterProfile> getListSubscribers(long userId, long listId) {
		return getListConnections(LIST_SUBSCRIBERS_URL, userId, listId);
	}

	public List<TwitterProfile> getListSubscribers(String screenName, String listSlug) {
		return getListConnections(LIST_SUBSCRIBERS_URL, screenName, listSlug);
	}

	@SuppressWarnings("unchecked")
	public UserList subscribe(long ownerId, long listId) {
		Map<String, Object> response  = restTemplate.postForObject(LIST_SUBSCRIBERS_URL, "", Map.class, ownerId, listId);
		return userListExtractor.extractObject(response);
	}

	@SuppressWarnings("unchecked")
	public UserList subscribe(String ownerScreenName, String listSlug) {
		Map<String, Object> response  = restTemplate.postForObject(LIST_SUBSCRIBERS_URL, "", Map.class, ownerScreenName, listSlug);
		return userListExtractor.extractObject(response);
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

	@SuppressWarnings("unchecked")
	private List<UserList> getTwitterLists(String url, Object... urlArgs) {
		Map<String, Object> response = restTemplate.getForObject(url, Map.class, urlArgs);
		List<Map<String, Object>> listsList = (List<Map<String, Object>>) response.get("lists");
		return userListExtractor.extractObjects(listsList);
	}

	@SuppressWarnings("unchecked")
	private UserList getTwitterList(Object... urlArgs) {
		Map<String, Object> response = restTemplate.getForObject(USER_LIST_URL, Map.class, urlArgs);
		return userListExtractor.extractObject(response);
	}

	@SuppressWarnings("unchecked")
	private UserList saveList(String url, String name, String description, boolean isPublic, Object... urlArgs) {
		MultiValueMap<String, String> request = new LinkedMultiValueMap<String, String>();
		request.set("name", name);
		request.set("description", description);
		request.set("mode", isPublic ? "public" : "private");
		Map<String, Object> response = restTemplate.postForObject(url, request, Map.class, urlArgs);
		return userListExtractor.extractObject(response);
	}

	private void deleteTwitterList(Object... urlArgs) {
		restTemplate.delete(USER_LIST_URL, urlArgs);
	}

	@SuppressWarnings("unchecked")
	private List<TwitterProfile> getListConnections(String relationshipUrl, Object... urlArgs) {
		Map<String, Object> response = restTemplate.getForObject(relationshipUrl, Map.class, urlArgs);
		List<Map<String, Object>> profileMapList = (List<Map<String, Object>>) response.get("users");
		return profileExtractor.extractObjects(profileMapList);
	}

	@SuppressWarnings("unchecked")
	private UserList addMembersToList(String fieldName, String joinedIds, Object... urlArgs) {
		MultiValueMap<String, String> request = new LinkedMultiValueMap<String, String>();
		request.set(fieldName, joinedIds);
		Map<String, Object> response = restTemplate.postForObject(CREATE_ALL_URL, request, Map.class, urlArgs);
		return userListExtractor.extractObject(response);
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
