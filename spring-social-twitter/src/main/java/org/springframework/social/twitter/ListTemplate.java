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
import org.springframework.social.twitter.support.extractors.AbstractResponseExtractor;
import org.springframework.social.twitter.support.extractors.TweetResponseExtractor;
import org.springframework.social.twitter.support.extractors.TwitterProfileResponseExtractor;
import org.springframework.social.twitter.support.extractors.UserListResponseExtractor;
import org.springframework.social.twitter.types.Tweet;
import org.springframework.social.twitter.types.TwitterProfile;
import org.springframework.social.twitter.types.UserList;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;

/**
 * Implementation of {@link ListOperations}, providing a binding to Twitter's list-oriented REST resources.
 * @author Craig Walls
 */
class ListTemplate implements ListOperations {

	private final UserOperations userApi;
	
	private TwitterProfileResponseExtractor profileExtractor;
	
	private TweetResponseExtractor tweetExtractor;
	
	private UserListResponseExtractor userListExtractor;
	
	private final LowLevelTwitterApi lowLevelApi;
	
	public ListTemplate(LowLevelTwitterApi lowLevelApi, UserOperations userApi) {
		this.lowLevelApi = lowLevelApi;
		this.userApi = userApi;
		this.profileExtractor = new TwitterProfileResponseExtractor();
		this.tweetExtractor = new TweetResponseExtractor();
		this.userListExtractor = new UserListResponseExtractor();
	}

	public List<UserList> getLists(long userId) {
		return lowLevelApi.fetchObjects("{userId}/lists.json", "lists", userListExtractor, userId);
	}

	public List<UserList> getLists(String screenName) {
		return lowLevelApi.fetchObjects("{screenName}/lists.json", "lists", userListExtractor, screenName);
	}

	public UserList getList(long userId, long listId) {
		return lowLevelApi.fetchObject("{userId}/lists/{listId}.json", userListExtractor, userId, listId);
	}

	public UserList getList(String screenName, String listSlug) {
		return lowLevelApi.fetchObject("{screenName}/lists/{listSlug}.json", userListExtractor, screenName, listSlug);
	}

	public List<Tweet> getListStatuses(long userId, long listId) {
		return lowLevelApi.fetchObjects("{userId}/lists/{listId}/statuses.json", tweetExtractor, userId, listId);
	}

	public List<Tweet> getListStatuses(String screenName, String listSlug) {
		return lowLevelApi.fetchObjects("{screenName}/lists/{screenName}/statuses.json", tweetExtractor, screenName, screenName);
	}

	public UserList createList(String name, String description, boolean isPublic) {	
		MultiValueMap<String, Object> request = buildListDataMap(name, description, isPublic);
		return lowLevelApi.publish("{userId}/lists.json", request, userListExtractor, userApi.getProfileId());
	}

	public UserList updateList(long listId, String name, String description, boolean isPublic) {
		MultiValueMap<String, Object> request = buildListDataMap(name, description, isPublic);
		return lowLevelApi.publish("{userId}/lists/{listId}.json", request, userListExtractor, (Long) userApi.getProfileId(), listId);
	}

	public void deleteList(long listId) {
		lowLevelApi.delete("{userId}/lists/{listId}.json", userApi.getProfileId(), listId);
	}

	public List<TwitterProfile> getListMembers(long userId, long listId) {
		return lowLevelApi.fetchObjects("{userId}/{listId}/members.json", "users", profileExtractor, userId, listId);
	}
	
	public List<TwitterProfile> getListMembers(String screenName, String listSlug) {
		return lowLevelApi.fetchObjects("{screenName}/{listSlug}/members.json", "users", profileExtractor, screenName, listSlug);
	}

	public UserList addToList(long listId, long... newMemberIds) {
		MultiValueMap<String, Object> request = new LinkedMultiValueMap<String, Object>();
		request.set("user_id", ArrayUtils.join(newMemberIds));		
		return lowLevelApi.publish("{userId}/{listId}/members/create_all.json", request, userListExtractor, userApi.getProfileId(), listId);
	}

	public UserList addToList(String listSlug, String... newMemberScreenNames) {
		MultiValueMap<String, Object> request = new LinkedMultiValueMap<String, Object>();
		request.set("screen_name", ArrayUtils.join(newMemberScreenNames));		
		return lowLevelApi.publish("{userId}/{listSlug}/members/create_all.json", request, userListExtractor, userApi.getProfileId(), listSlug);
	}

	public void removeFromList(long listId, long memberId) {
		lowLevelApi.delete("{userId}/{listId}/members.json?id={memberId}", userApi.getProfileId(), listId, memberId);
	}

	public void removeFromList(String listSlug, String memberScreenName) {
		lowLevelApi.delete("{userId}/{listSlug}/members.json?id={memberScreenName}", userApi.getProfileId(), listSlug, memberScreenName);
	}

	public List<TwitterProfile> getListSubscribers(long userId, long listId) {
		return lowLevelApi.fetchObjects("{userId}/{listId}/subscribers.json", "users", profileExtractor, userId, listId);
	}

	public List<TwitterProfile> getListSubscribers(String screenName, String listSlug) {
		return lowLevelApi.fetchObjects("{screenName}/{listSlug}/subscribers.json", "users", profileExtractor, screenName, listSlug);
	}

	public UserList subscribe(long ownerId, long listId) {
		MultiValueMap<String, Object> data = new LinkedMultiValueMap<String, Object>();
		return lowLevelApi.publish("{userId}/{listId}/subscribers.json", data, userListExtractor, ownerId, listId);
	}

	public UserList subscribe(String ownerScreenName, String listSlug) {
		MultiValueMap<String, Object> data = new LinkedMultiValueMap<String, Object>();
		return lowLevelApi.publish("{screenName}/{listSlug}/subscribers.json", data, userListExtractor, ownerScreenName, listSlug);
	}

	public void unsubscribe(long ownerId, long listId) {
		lowLevelApi.delete("{userId}/{listId}/subscribers.json", ownerId, listId);
	}

	public void unsubscribe(String ownerScreenName, String listSlug) {
		lowLevelApi.delete("{screenName}/{listSlug}/subscribers.json", ownerScreenName, listSlug);
	}

	public List<UserList> getMemberships(long userId) {
		return lowLevelApi.fetchObjects("{screenName}/lists/memberships.json", "lists", userListExtractor, userId);
	}

	public List<UserList> getMemberships(String screenName) {
		return lowLevelApi.fetchObjects("{screenName}/lists/memberships.json", "lists", userListExtractor, screenName);
	}

	public List<UserList> getSubscriptions(long userId) {
		return lowLevelApi.fetchObjects("{screenName}/lists/subscriptions.json", "lists", userListExtractor, userId);
	}

	public List<UserList> getSubscriptions(String screenName) {
		return lowLevelApi.fetchObjects("{screenName}/lists/subscriptions.json", "lists", userListExtractor, screenName);
	}

	public boolean isMember(long userId, long listId, long memberId) {
		return checkListConnection("{user_id}/{list_id}/members/{member_id}.json", userId, listId, memberId);
	}

	public boolean isMember(String screenName, String listSlug, String memberScreenName) {
		return checkListConnection("{user_id}/{list_id}/members/{member_id}.json", screenName, listSlug, memberScreenName);
	}

	public boolean isSubscriber(long userId, long listId, long subscriberId) {
		return checkListConnection("{user_id}/{list_id}/subscribers/{subscriber_id}.json", userId, listId, subscriberId);
	}

	public boolean isSubscriber(String screenName, String listSlug, String subscriberScreenName) {
		return checkListConnection("{user_id}/{list_id}/subscribers/{subscriber_id}.json", screenName, listSlug, subscriberScreenName);
	}

	// private helpers

	private boolean checkListConnection(String path, Object... urlArgs) {
		try {
			lowLevelApi.fetchObject(path, new NoOpExtractor(), urlArgs);
			return true;
		} catch (HttpClientErrorException e) {
			if(e.getStatusCode() == HttpStatus.NOT_FOUND) {
				return false;
			} 
			throw e;
		}
	}

	private static class NoOpExtractor extends AbstractResponseExtractor<Object> {
		public Object extractObject(Map<String, Object> responseMap) {
			return null;
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
