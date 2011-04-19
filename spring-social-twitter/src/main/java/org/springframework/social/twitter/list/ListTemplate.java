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
package org.springframework.social.twitter.list;

import java.util.Collections;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.social.twitter.AbstractTwitterOperations;
import org.springframework.social.twitter.Tweet;
import org.springframework.social.twitter.TwitterProfile;
import org.springframework.social.twitter.json.TweetList;
import org.springframework.social.twitter.user.TwitterProfileUsersList;
import org.springframework.social.twitter.util.ArrayUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

/**
 * Implementation of {@link ListOperations}, providing a binding to Twitter's list-oriented REST resources.
 * @author Craig Walls
 */
public class ListTemplate extends AbstractTwitterOperations implements ListOperations {
	
	private final RestTemplate restTemplate;

	private final Long userProfileId;
					
	public ListTemplate(RestTemplate restTemplate, Long userProfileId) {
		super(userProfileId != null);
		this.restTemplate = restTemplate;
		this.userProfileId = userProfileId;
	}

	public List<UserList> getLists(long userId) {
		requireUserAuthorization();
		return restTemplate.getForObject(buildUri(userId + "/lists.json"), UserListList.class).getList();
	}

	public List<UserList> getLists(String screenName) {
		requireUserAuthorization();
		return restTemplate.getForObject(buildUri(screenName + "/lists.json"), UserListList.class).getList();
	}

	public UserList getList(long userId, long listId) {
		requireUserAuthorization();
		return restTemplate.getForObject(buildUri(userId + "/lists/" + listId + ".json"), UserList.class);
	}

	public UserList getList(String screenName, String listSlug) {
		requireUserAuthorization();
		return restTemplate.getForObject(buildUri(screenName + "/lists/" + listSlug + ".json"), UserList.class);
	}

	public List<Tweet> getListStatuses(long userId, long listId) {
		return restTemplate.getForObject(buildUri(userId + "/lists/" + listId + "/statuses.json"), TweetList.class);
	}

	public List<Tweet> getListStatuses(String screenName, String listSlug) {
		return restTemplate.getForObject(buildUri(screenName + "/lists/" + listSlug + "/statuses.json"), TweetList.class);
	}

	public UserList createList(String name, String description, boolean isPublic) {	
		requireUserAuthorization();
		MultiValueMap<String, Object> request = buildListDataMap(name, description, isPublic);
		return restTemplate.postForObject(buildUri(userProfileId + "/lists.json"), request, UserList.class);
	}

	public UserList updateList(long listId, String name, String description, boolean isPublic) {
		requireUserAuthorization();
		MultiValueMap<String, Object> request = buildListDataMap(name, description, isPublic);
		return restTemplate.postForObject(buildUri(userProfileId + "/lists/" + listId + ".json"), request, UserList.class);
	}

	public void deleteList(long listId) {
		requireUserAuthorization();
		restTemplate.delete(buildUri(userProfileId + "/lists/" + listId + ".json"));
	}

	public List<TwitterProfile> getListMembers(long userId, long listId) {
		requireUserAuthorization();
		return restTemplate.getForObject(buildUri(userId + "/" + listId + "/members.json"), TwitterProfileUsersList.class).getList();
	}
	
	public List<TwitterProfile> getListMembers(String screenName, String listSlug) {
		requireUserAuthorization();
		return restTemplate.getForObject(buildUri(screenName + "/" + listSlug + "/members.json"), TwitterProfileUsersList.class).getList();
	}

	public UserList addToList(long listId, long... newMemberIds) {
		requireUserAuthorization();
		MultiValueMap<String, Object> request = new LinkedMultiValueMap<String, Object>();
		request.set("user_id", ArrayUtils.join(newMemberIds));
		return restTemplate.postForObject(buildUri(userProfileId + "/" + listId + "/members/create_all.json"), request, UserList.class);
	}

	public UserList addToList(String listSlug, String... newMemberScreenNames) {
		requireUserAuthorization();
		MultiValueMap<String, Object> request = new LinkedMultiValueMap<String, Object>();
		request.set("screen_name", ArrayUtils.join(newMemberScreenNames));		
		return restTemplate.postForObject(buildUri(userProfileId + "/" + listSlug + "/members/create_all.json"), request, UserList.class);
	}

	public void removeFromList(long listId, long memberId) {
		requireUserAuthorization();
		restTemplate.delete(buildUri(userProfileId + "/" + listId + "/members.json", Collections.singletonMap("id", String.valueOf(memberId))));
	}

	public void removeFromList(String listSlug, String memberScreenName) {
		requireUserAuthorization();
		restTemplate.delete(buildUri(userProfileId + "/" + listSlug + "/members.json", Collections.singletonMap("id", String.valueOf(memberScreenName))));
	}

	public List<TwitterProfile> getListSubscribers(long userId, long listId) {
		requireUserAuthorization();
		return restTemplate.getForObject(buildUri(userId + "/" + listId + "/subscribers.json"), TwitterProfileUsersList.class).getList();
	}

	public List<TwitterProfile> getListSubscribers(String screenName, String listSlug) {
		requireUserAuthorization();
		return restTemplate.getForObject(buildUri(screenName + "/" + listSlug + "/subscribers.json"), TwitterProfileUsersList.class).getList();
	}

	public UserList subscribe(long ownerId, long listId) {
		requireUserAuthorization();
		MultiValueMap<String, Object> data = new LinkedMultiValueMap<String, Object>();
		return restTemplate.postForObject(buildUri(ownerId + "/" + listId + "/subscribers.json"), data, UserList.class);
	}

	public UserList subscribe(String ownerScreenName, String listSlug) {
		requireUserAuthorization();
		MultiValueMap<String, Object> data = new LinkedMultiValueMap<String, Object>();
		return restTemplate.postForObject(buildUri(ownerScreenName + "/" + listSlug + "/subscribers.json"), data, UserList.class);
	}

	public void unsubscribe(long ownerId, long listId) {
		requireUserAuthorization();
		restTemplate.delete(buildUri(ownerId + "/" + listId + "/subscribers.json"));
	}

	public void unsubscribe(String ownerScreenName, String listSlug) {
		requireUserAuthorization();
		restTemplate.delete(buildUri(ownerScreenName + "/" + listSlug + "/subscribers.json"));
	}

	public List<UserList> getMemberships(long userId) {
		requireUserAuthorization();
		return restTemplate.getForObject(buildUri(userId + "/lists/memberships.json"), UserListList.class).getList();
	}

	public List<UserList> getMemberships(String screenName) {
		requireUserAuthorization();
		return restTemplate.getForObject(buildUri(screenName + "/lists/memberships.json"), UserListList.class).getList();
	}

	public List<UserList> getSubscriptions(long userId) {
		requireUserAuthorization();
		return restTemplate.getForObject(buildUri(userId + "/lists/subscriptions.json"), UserListList.class).getList();
	}

	public List<UserList> getSubscriptions(String screenName) {
		requireUserAuthorization();
		return restTemplate.getForObject(buildUri(screenName + "/lists/subscriptions.json"), UserListList.class).getList();
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
			restTemplate.getForObject(buildUri(path), String.class);
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
