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
	private final TwitterRequestApi requestApi;
	
	public ListsApiTemplate(TwitterRequestApi requestApi, RestTemplate restTemplate, UserApi userApi) {
		this.requestApi = requestApi;
		this.restTemplate = restTemplate;
		this.userApi = userApi;
		this.profileExtractor = new TwitterProfileResponseExtractor();
		this.tweetExtractor = new TweetResponseExtractor();
		this.userListExtractor = new UserListResponseExtractor();
	}

	public List<UserList> getLists(long userId) {
		return requestApi.fetchObjects("{userId}/lists.json", "lists", userListExtractor, userId);
	}

	public List<UserList> getLists(String screenName) {
		return requestApi.fetchObjects("{screenName}/lists.json", "lists", userListExtractor, screenName);
	}

	public UserList getList(long userId, long listId) {
		return requestApi.fetchObject("{userId}/lists/{listId}.json", userListExtractor, userId, listId);
	}

	public UserList getList(String screenName, String listSlug) {
		return requestApi.fetchObject("{screenName}/lists/{listSlug}.json", userListExtractor, screenName, listSlug);
	}

	public List<Tweet> getListStatuses(long userId, long listId) {
		return requestApi.fetchObjects("{userId}/lists/{listId}/statuses.json", tweetExtractor, userId, listId);
	}

	public List<Tweet> getListStatuses(String screenName, String listSlug) {
		return requestApi.fetchObjects("{screenName}/lists/{screenName}/statuses.json", tweetExtractor, screenName, screenName);
	}

	public UserList createList(String name, String description, boolean isPublic) {	
		MultiValueMap<String, Object> request = buildListDataMap(name, description, isPublic);
		return requestApi.publish("{userId}/lists.json", request, userListExtractor, userApi.getProfileId());
	}

	public UserList updateList(long listId, String name, String description, boolean isPublic) {
		MultiValueMap<String, Object> request = buildListDataMap(name, description, isPublic);
		return requestApi.publish("{userId}/lists/{listId}.json", request, userListExtractor, (Long) userApi.getProfileId(), listId);
	}

	public void deleteList(long listId) {
		requestApi.delete("{userId}/lists/{listId}.json", userApi.getProfileId(), listId);
	}

	public List<TwitterProfile> getListMembers(long userId, long listId) {
		return requestApi.fetchObjects("{userId}/{listId}/members.json", "users", profileExtractor, userId, listId);
	}
	
	public List<TwitterProfile> getListMembers(String screenName, String listSlug) {
		return requestApi.fetchObjects("{screenName}/{listSlug}/members.json", "users", profileExtractor, screenName, listSlug);
	}

	public UserList addToList(long listId, long... newMemberIds) {
		MultiValueMap<String, Object> request = new LinkedMultiValueMap<String, Object>();
		request.set("user_id", ArrayUtils.join(newMemberIds));		
		return requestApi.publish("{userId}/{listId}/members/create_all.json", request, userListExtractor, userApi.getProfileId(), listId);
	}

	public UserList addToList(String listSlug, String... newMemberScreenNames) {
		MultiValueMap<String, Object> request = new LinkedMultiValueMap<String, Object>();
		request.set("screen_name", ArrayUtils.join(newMemberScreenNames));		
		return requestApi.publish("{userId}/{listSlug}/members/create_all.json", request, userListExtractor, userApi.getProfileId(), listSlug);
	}

	public void removeFromList(long listId, long memberId) {
		requestApi.delete("{userId}/{listId}/members.json?id={memberId}", userApi.getProfileId(), listId, memberId);
	}

	public void removeFromList(String listSlug, String memberScreenName) {
		requestApi.delete("{userId}/{listSlug}/members.json?id={memberScreenName}", userApi.getProfileId(), listSlug, memberScreenName);
	}

	public List<TwitterProfile> getListSubscribers(long userId, long listId) {
		return requestApi.fetchObjects("{userId}/{listId}/subscribers.json", "users", profileExtractor, userId, listId);
	}

	public List<TwitterProfile> getListSubscribers(String screenName, String listSlug) {
		return requestApi.fetchObjects("{screenName}/{listSlug}/subscribers.json", "users", profileExtractor, screenName, listSlug);
	}

	public UserList subscribe(long ownerId, long listId) {
		MultiValueMap<String, Object> data = new LinkedMultiValueMap<String, Object>();
		return requestApi.publish("{userId}/{listId}/subscribers.json", data, userListExtractor, ownerId, listId);
	}

	public UserList subscribe(String ownerScreenName, String listSlug) {
		MultiValueMap<String, Object> data = new LinkedMultiValueMap<String, Object>();
		return requestApi.publish("{screenName}/{listSlug}/subscribers.json", data, userListExtractor, ownerScreenName, listSlug);
	}

	public void unsubscribe(long ownerId, long listId) {
		requestApi.delete("{userId}/{listId}/subscribers.json", ownerId, listId);
	}

	public void unsubscribe(String ownerScreenName, String listSlug) {
		requestApi.delete("{screenName}/{listSlug}/subscribers.json", ownerScreenName, listSlug);
	}

	public List<UserList> getMemberships(long userId) {
		return requestApi.fetchObjects("{screenName}/lists/memberships.json", "lists", userListExtractor, userId);
	}

	public List<UserList> getMemberships(String screenName) {
		return requestApi.fetchObjects("{screenName}/lists/memberships.json", "lists", userListExtractor, screenName);
	}

	public List<UserList> getSubscriptions(long userId) {
		return requestApi.fetchObjects("{screenName}/lists/subscriptions.json", "lists", userListExtractor, userId);
	}

	public List<UserList> getSubscriptions(String screenName) {
		return requestApi.fetchObjects("{screenName}/lists/subscriptions.json", "lists", userListExtractor, screenName);
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

	// private helpers

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


	private MultiValueMap<String, Object> buildListDataMap(String name,
			String description, boolean isPublic) {
		MultiValueMap<String, Object> request = new LinkedMultiValueMap<String, Object>();
		request.set("name", name);
		request.set("description", description);
		request.set("mode", isPublic ? "public" : "private");
		return request;
	}

	static final String CHECK_MEMBER_URL = TwitterTemplate.API_URL_BASE + "{user_id}/{list_id}/members/{member_id}.json";
	static final String CHECK_SUBSCRIBER_URL = TwitterTemplate.API_URL_BASE + "{user_id}/{list_id}/subscribers/{subscriber_id}.json";

}
