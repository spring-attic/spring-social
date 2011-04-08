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
		return lowLevelApi.fetchObjects(userId + "/lists.json", "lists", userListExtractor);
	}

	public List<UserList> getLists(String screenName) {
		return lowLevelApi.fetchObjects(screenName + "/lists.json", "lists", userListExtractor);
	}

	public UserList getList(long userId, long listId) {
		return lowLevelApi.fetchObject(userId + "/lists/" + listId + ".json", userListExtractor);
	}

	public UserList getList(String screenName, String listSlug) {
		return lowLevelApi.fetchObject(screenName + "/lists/" + listSlug + ".json", userListExtractor);
	}

	public List<Tweet> getListStatuses(long userId, long listId) {
		return lowLevelApi.fetchObjects("{userId}/lists/{listId}/statuses.json", tweetExtractor);
	}

	public List<Tweet> getListStatuses(String screenName, String listSlug) {
		return lowLevelApi.fetchObjects("{screenName}/lists/{screenName}/statuses.json", tweetExtractor);
	}

	public UserList createList(String name, String description, boolean isPublic) {	
		MultiValueMap<String, Object> request = buildListDataMap(name, description, isPublic);
		return lowLevelApi.publish(userApi.getProfileId() + "/lists.json", request, userListExtractor);
	}

	public UserList updateList(long listId, String name, String description, boolean isPublic) {
		MultiValueMap<String, Object> request = buildListDataMap(name, description, isPublic);
		return lowLevelApi.publish(userApi.getProfileId() + "/lists/" + listId + ".json", request, userListExtractor);
	}

	public void deleteList(long listId) {
		lowLevelApi.delete(userApi.getProfileId() + "/lists/" + listId + ".json");
	}

	public List<TwitterProfile> getListMembers(long userId, long listId) {
		return lowLevelApi.fetchObjects(userId + "/" + listId + "/members.json", "users", profileExtractor);
	}
	
	public List<TwitterProfile> getListMembers(String screenName, String listSlug) {
		return lowLevelApi.fetchObjects(screenName + "/" + listSlug + "/members.json", "users", profileExtractor);
	}

	public UserList addToList(long listId, long... newMemberIds) {
		MultiValueMap<String, Object> request = new LinkedMultiValueMap<String, Object>();
		request.set("user_id", ArrayUtils.join(newMemberIds));		
		return lowLevelApi.publish(userApi.getProfileId() + "/" + listId + "/members/create_all.json", request, userListExtractor);
	}

	public UserList addToList(String listSlug, String... newMemberScreenNames) {
		MultiValueMap<String, Object> request = new LinkedMultiValueMap<String, Object>();
		request.set("screen_name", ArrayUtils.join(newMemberScreenNames));		
		return lowLevelApi.publish(userApi.getProfileId() + "/" + listSlug + "/members/create_all.json", request, userListExtractor);
	}

	public void removeFromList(long listId, long memberId) {
		lowLevelApi.delete(userApi.getProfileId() + "/" + listId + "/members.json", Collections.singletonMap("id", String.valueOf(memberId)));
	}

	public void removeFromList(String listSlug, String memberScreenName) {
		lowLevelApi.delete(userApi.getProfileId() + "/" + listSlug + "/members.json", Collections.singletonMap("id", memberScreenName));
	}

	public List<TwitterProfile> getListSubscribers(long userId, long listId) {
		return lowLevelApi.fetchObjects(userId + "/" + listId + "/subscribers.json", "users", profileExtractor);
	}

	public List<TwitterProfile> getListSubscribers(String screenName, String listSlug) {
		return lowLevelApi.fetchObjects(screenName + "/" + listSlug + "/subscribers.json", "users", profileExtractor);
	}

	public UserList subscribe(long ownerId, long listId) {
		MultiValueMap<String, Object> data = new LinkedMultiValueMap<String, Object>();
		return lowLevelApi.publish(ownerId + "/" + listId + "/subscribers.json", data, userListExtractor);
	}

	public UserList subscribe(String ownerScreenName, String listSlug) {
		MultiValueMap<String, Object> data = new LinkedMultiValueMap<String, Object>();
		return lowLevelApi.publish(ownerScreenName + "/" + listSlug + "/subscribers.json", data, userListExtractor);
	}

	public void unsubscribe(long ownerId, long listId) {
		lowLevelApi.delete(ownerId + "/" + listId + "/subscribers.json");
	}

	public void unsubscribe(String ownerScreenName, String listSlug) {
		lowLevelApi.delete(ownerScreenName + "/" + listSlug + "/subscribers.json");
	}

	public List<UserList> getMemberships(long userId) {
		return lowLevelApi.fetchObjects(userId + "/lists/memberships.json", "lists", userListExtractor);
	}

	public List<UserList> getMemberships(String screenName) {
		return lowLevelApi.fetchObjects(screenName + "/lists/memberships.json", "lists", userListExtractor);
	}

	public List<UserList> getSubscriptions(long userId) {
		return lowLevelApi.fetchObjects(userId + "/lists/subscriptions.json", "lists", userListExtractor);
	}

	public List<UserList> getSubscriptions(String screenName) {
		return lowLevelApi.fetchObjects(screenName + "/lists/subscriptions.json", "lists", userListExtractor);
	}

	public boolean isMember(long userId, long listId, long memberId) {
		return checkListConnection(userId + "/" + listId + "/members/" + memberId + ".json");
	}

	public boolean isMember(String screenName, String listSlug, String memberScreenName) {
		return checkListConnection(screenName + "/" + listSlug + "/members/" + memberScreenName + ".json");
	}

	public boolean isSubscriber(long userId, long listId, long subscriberId) {
		return checkListConnection(userId + "/" + listId + "/subscribers/" + subscriberId + ".json");
	}

	public boolean isSubscriber(String screenName, String listSlug, String subscriberScreenName) {
		return checkListConnection(screenName + "/" + listSlug + "/subscribers/" + subscriberScreenName + ".json");
	}

	// private helpers

	private boolean checkListConnection(String path) {
		try {
			lowLevelApi.fetchObject(path, new NoOpExtractor());
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
