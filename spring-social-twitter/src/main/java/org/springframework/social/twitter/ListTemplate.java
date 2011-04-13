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

import org.springframework.http.HttpStatus;
import org.springframework.social.twitter.support.json.TweetList;
import org.springframework.social.twitter.support.json.TwitterProfileUsersList;
import org.springframework.social.twitter.support.json.UserListList;
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
class ListTemplate extends AbstractTwitterOperations implements ListOperations {

	private final UserOperations userApi;
					
	public ListTemplate(LowLevelTwitterApi lowLevelApi, UserOperations userApi) {
		// TODO : Get user ID sooner and stash it for later use so that we don't
		//        fetch it for every operation that needs it. This is an easy thing to do,
		//        but makes the testing a bit tricky.
		super(lowLevelApi);
		this.userApi = userApi;
	}

	public List<UserList> getLists(long userId) {
		requireUserAuthorization();
		return getLowLevelTwitterApi().fetchObject(userId + "/lists.json", UserListList.class).getList();
	}

	public List<UserList> getLists(String screenName) {
		requireUserAuthorization();
		return getLowLevelTwitterApi().fetchObject(screenName + "/lists.json", UserListList.class).getList();
	}

	public UserList getList(long userId, long listId) {
		requireUserAuthorization();
		return getLowLevelTwitterApi().fetchObject(userId + "/lists/" + listId + ".json", UserList.class);
	}

	public UserList getList(String screenName, String listSlug) {
		requireUserAuthorization();
		return getLowLevelTwitterApi().fetchObject(screenName + "/lists/" + listSlug + ".json", UserList.class);
	}

	public List<Tweet> getListStatuses(long userId, long listId) {
		return getLowLevelTwitterApi().fetchObject("{userId}/lists/{listId}/statuses.json", TweetList.class).getList();
	}

	public List<Tweet> getListStatuses(String screenName, String listSlug) {
		return getLowLevelTwitterApi().fetchObject("{screenName}/lists/{screenName}/statuses.json", TweetList.class).getList();
	}

	public UserList createList(String name, String description, boolean isPublic) {	
		requireUserAuthorization();
		MultiValueMap<String, Object> request = buildListDataMap(name, description, isPublic);
		return getLowLevelTwitterApi().publish(userApi.getProfileId() + "/lists.json", request, UserList.class);
	}

	public UserList updateList(long listId, String name, String description, boolean isPublic) {
		requireUserAuthorization();
		MultiValueMap<String, Object> request = buildListDataMap(name, description, isPublic);
		return getLowLevelTwitterApi().publish(userApi.getProfileId() + "/lists/" + listId + ".json", request, UserList.class);
	}

	public void deleteList(long listId) {
		requireUserAuthorization();
		getLowLevelTwitterApi().delete(userApi.getProfileId() + "/lists/" + listId + ".json");
	}

	public List<TwitterProfile> getListMembers(long userId, long listId) {
		requireUserAuthorization();
		return getLowLevelTwitterApi().fetchObject(userId + "/" + listId + "/members.json", TwitterProfileUsersList.class).getList();
	}
	
	public List<TwitterProfile> getListMembers(String screenName, String listSlug) {
		requireUserAuthorization();
		return getLowLevelTwitterApi().fetchObject(screenName + "/" + listSlug + "/members.json", TwitterProfileUsersList.class).getList();
	}

	public UserList addToList(long listId, long... newMemberIds) {
		requireUserAuthorization();
		MultiValueMap<String, Object> request = new LinkedMultiValueMap<String, Object>();
		request.set("user_id", ArrayUtils.join(newMemberIds));		
		return getLowLevelTwitterApi().publish(userApi.getProfileId() + "/" + listId + "/members/create_all.json", request, UserList.class);
	}

	public UserList addToList(String listSlug, String... newMemberScreenNames) {
		requireUserAuthorization();
		MultiValueMap<String, Object> request = new LinkedMultiValueMap<String, Object>();
		request.set("screen_name", ArrayUtils.join(newMemberScreenNames));		
		return getLowLevelTwitterApi().publish(userApi.getProfileId() + "/" + listSlug + "/members/create_all.json", request, UserList.class);
	}

	public void removeFromList(long listId, long memberId) {
		requireUserAuthorization();
		getLowLevelTwitterApi().delete(userApi.getProfileId() + "/" + listId + "/members.json", Collections.singletonMap("id", String.valueOf(memberId)));
	}

	public void removeFromList(String listSlug, String memberScreenName) {
		requireUserAuthorization();
		getLowLevelTwitterApi().delete(userApi.getProfileId() + "/" + listSlug + "/members.json", Collections.singletonMap("id", memberScreenName));
	}

	public List<TwitterProfile> getListSubscribers(long userId, long listId) {
		requireUserAuthorization();
		return getLowLevelTwitterApi().fetchObject(userId + "/" + listId + "/subscribers.json", TwitterProfileUsersList.class).getList();
	}

	public List<TwitterProfile> getListSubscribers(String screenName, String listSlug) {
		requireUserAuthorization();
		return getLowLevelTwitterApi().fetchObject(screenName + "/" + listSlug + "/subscribers.json", TwitterProfileUsersList.class).getList();
	}

	public UserList subscribe(long ownerId, long listId) {
		requireUserAuthorization();
		MultiValueMap<String, Object> data = new LinkedMultiValueMap<String, Object>();
		return getLowLevelTwitterApi().publish(ownerId + "/" + listId + "/subscribers.json", data, UserList.class);
	}

	public UserList subscribe(String ownerScreenName, String listSlug) {
		requireUserAuthorization();
		MultiValueMap<String, Object> data = new LinkedMultiValueMap<String, Object>();
		return getLowLevelTwitterApi().publish(ownerScreenName + "/" + listSlug + "/subscribers.json", data, UserList.class);
	}

	public void unsubscribe(long ownerId, long listId) {
		requireUserAuthorization();
		getLowLevelTwitterApi().delete(ownerId + "/" + listId + "/subscribers.json");
	}

	public void unsubscribe(String ownerScreenName, String listSlug) {
		requireUserAuthorization();
		getLowLevelTwitterApi().delete(ownerScreenName + "/" + listSlug + "/subscribers.json");
	}

	public List<UserList> getMemberships(long userId) {
		requireUserAuthorization();
		return getLowLevelTwitterApi().fetchObject(userId + "/lists/memberships.json", UserListList.class).getList();
	}

	public List<UserList> getMemberships(String screenName) {
		requireUserAuthorization();
		return getLowLevelTwitterApi().fetchObject(screenName + "/lists/memberships.json", UserListList.class).getList();
	}

	public List<UserList> getSubscriptions(long userId) {
		requireUserAuthorization();
		return getLowLevelTwitterApi().fetchObject(userId + "/lists/subscriptions.json", UserListList.class).getList();
	}

	public List<UserList> getSubscriptions(String screenName) {
		requireUserAuthorization();
		return getLowLevelTwitterApi().fetchObject(screenName + "/lists/subscriptions.json", UserListList.class).getList();
	}

	public boolean isMember(long userId, long listId, long memberId) {
		requireUserAuthorization();
		return checkListConnection(userId + "/" + listId + "/members/" + memberId + ".json");
	}

	public boolean isMember(String screenName, String listSlug, String memberScreenName) {
		requireUserAuthorization();
		return checkListConnection(screenName + "/" + listSlug + "/members/" + memberScreenName + ".json");
	}

	public boolean isSubscriber(long userId, long listId, long subscriberId) {
		requireUserAuthorization();
		return checkListConnection(userId + "/" + listId + "/subscribers/" + subscriberId + ".json");
	}

	public boolean isSubscriber(String screenName, String listSlug, String subscriberScreenName) {
		requireUserAuthorization();
		return checkListConnection(screenName + "/" + listSlug + "/subscribers/" + subscriberScreenName + ".json");
	}

	// private helpers

	private boolean checkListConnection(String path) {
		try {
			getLowLevelTwitterApi().fetchObject(path, String.class);
			return true;
		} catch (HttpClientErrorException e) {
			if(e.getStatusCode() == HttpStatus.NOT_FOUND) {
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

}
