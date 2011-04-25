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

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.springframework.http.HttpStatus;
import org.springframework.social.twitter.api.ListOperations;
import org.springframework.social.twitter.api.Tweet;
import org.springframework.social.twitter.api.TwitterProfile;
import org.springframework.social.twitter.api.UserList;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

/**
 * Implementation of {@link ListOperations}, providing a binding to Twitter's list-oriented REST resources.
 * @author Craig Walls
 */
class ListTemplate extends AbstractTwitterOperations implements ListOperations {
	
	private final RestTemplate restTemplate;
					
	public ListTemplate(RestTemplate restTemplate, boolean isAuthorizedForUser) {
		super(isAuthorizedForUser);
		this.restTemplate = restTemplate;
	}

	public List<UserList> getLists() {
		requireUserAuthorization();
		return restTemplate.getForObject(buildUri("lists.json"), UserListList.class).getList();
	}

	public List<UserList> getLists(long userId) {
		requireUserAuthorization();
		return restTemplate.getForObject(buildUri("lists.json", Collections.singletonMap("user_id", String.valueOf(userId))), UserListList.class).getList();
	}

	public List<UserList> getLists(String screenName) {
		requireUserAuthorization();
		return restTemplate.getForObject(buildUri("lists.json", Collections.singletonMap("screen_name", screenName)), UserListList.class).getList();
	}

	public UserList getList(long listId) {
		requireUserAuthorization();
		return restTemplate.getForObject(buildUri("lists/show.json", Collections.singletonMap("list_id", String.valueOf(listId))), UserList.class);
	}

	public UserList getList(String screenName, String listSlug) {
		requireUserAuthorization();
		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put("owner_screen_name", screenName);
		parameters.put("slug", listSlug);
		return restTemplate.getForObject(buildUri("lists/show.json", parameters), UserList.class);
	}

	public List<Tweet> getListStatuses(long listId) {
		return restTemplate.getForObject(buildUri("lists/statuses.json", Collections.singletonMap("list_id", String.valueOf(listId))), TweetList.class);
	}

	public List<Tweet> getListStatuses(String screenName, String listSlug) {
		Map<String, String> parameters = new TreeMap<String, String>();
		parameters.put("owner_screen_name", screenName);
		parameters.put("slug", listSlug);
		return restTemplate.getForObject(buildUri("lists/statuses.json", parameters), TweetList.class);
	}

	public UserList createList(String name, String description, boolean isPublic) {	
		requireUserAuthorization();
		MultiValueMap<String, Object> request = buildListDataMap(name, description, isPublic);
		return restTemplate.postForObject(buildUri("lists/create.json"), request, UserList.class);
	}

	public UserList updateList(long listId, String name, String description, boolean isPublic) {
		requireUserAuthorization();
		MultiValueMap<String, Object> request = buildListDataMap(name, description, isPublic);
		request.set("list_id", String.valueOf(listId));
		return restTemplate.postForObject(buildUri("lists/update.json"), request, UserList.class);
	}

	public void deleteList(long listId) {
		requireUserAuthorization();
		restTemplate.delete(buildUri("lists/destroy.json", Collections.singletonMap("list_id", String.valueOf(listId))));
	}

	public List<TwitterProfile> getListMembers(long listId) {
		requireUserAuthorization();
		return restTemplate.getForObject(buildUri("lists/members.json", Collections.singletonMap("list_id", String.valueOf(listId))), TwitterProfileUsersList.class).getList();
	}
	
	public List<TwitterProfile> getListMembers(String screenName, String listSlug) {
		requireUserAuthorization();
		Map<String, String> parameters = new TreeMap<String, String>();
		parameters.put("owner_screen_name", screenName);
		parameters.put("slug", listSlug);
		return restTemplate.getForObject(buildUri("lists/members.json", parameters), TwitterProfileUsersList.class).getList();
	}

	public UserList addToList(long listId, long... newMemberIds) {
		requireUserAuthorization();
		MultiValueMap<String, Object> request = new LinkedMultiValueMap<String, Object>();
		request.set("user_id", ArrayUtils.join(newMemberIds));
		request.set("list_id", String.valueOf(listId));
		return restTemplate.postForObject(buildUri("lists/members/create_all.json"), request, UserList.class);
	}

	public UserList addToList(long listId, String... newMemberScreenNames) {
		requireUserAuthorization();
		MultiValueMap<String, Object> request = new LinkedMultiValueMap<String, Object>();
		request.set("screen_name", ArrayUtils.join(newMemberScreenNames));
		request.set("list_id", String.valueOf(listId));
		return restTemplate.postForObject(buildUri("lists/members/create_all.json"), request, UserList.class);
	}

	public void removeFromList(long listId, long memberId) {
		requireUserAuthorization();
		MultiValueMap<String, Object> request = new LinkedMultiValueMap<String, Object>();
		request.set("user_id", String.valueOf(memberId)); 
		request.set("list_id", String.valueOf(listId));
		restTemplate.postForObject(buildUri("lists/members/destroy.json"), request, String.class);
	}

	public void removeFromList(long listId, String memberScreenName) {
		requireUserAuthorization();
		MultiValueMap<String, Object> request = new LinkedMultiValueMap<String, Object>();
		request.set("screen_name", String.valueOf(memberScreenName)); 
		request.set("list_id", String.valueOf(listId));
		restTemplate.postForObject(buildUri("lists/members/destroy.json"), request, String.class);
	}

	public List<TwitterProfile> getListSubscribers(long userId, long listId) {
		requireUserAuthorization();
		return restTemplate.getForObject(buildUri("lists/subscribers.json", Collections.singletonMap("list_id", String.valueOf(listId))), TwitterProfileUsersList.class).getList();
	}

	public List<TwitterProfile> getListSubscribers(String screenName, String listSlug) {
		requireUserAuthorization();
		Map<String, String> parameters = new TreeMap<String, String>();
		parameters.put("owner_screen_name", screenName);
		parameters.put("slug", listSlug);
		return restTemplate.getForObject(buildUri("lists/subscribers.json", parameters), TwitterProfileUsersList.class).getList();
	}

	
	public UserList subscribe(long listId) {
		requireUserAuthorization();
		MultiValueMap<String, Object> request = new LinkedMultiValueMap<String, Object>();
		request.set("list_id", String.valueOf(listId));
		return restTemplate.postForObject(buildUri("lists/subscribers/create.json"), request, UserList.class);
	}

	public UserList subscribe(String ownerScreenName, String listSlug) {
		requireUserAuthorization();
		MultiValueMap<String, Object> request = new LinkedMultiValueMap<String, Object>();
		request.set("owner_screen_name", ownerScreenName);
		request.set("slug", listSlug);
		return restTemplate.postForObject(buildUri("lists/subscribers/create.json"), request, UserList.class);
	}

	public UserList unsubscribe(long listId) {
		requireUserAuthorization();
		MultiValueMap<String, Object> request = new LinkedMultiValueMap<String, Object>();
		request.set("list_id", String.valueOf(listId));
		return restTemplate.postForObject(buildUri("lists/subscribers/destroy.json"), request, UserList.class);
	}

	public UserList unsubscribe(String ownerScreenName, String listSlug) {
		requireUserAuthorization();
		MultiValueMap<String, Object> request = new LinkedMultiValueMap<String, Object>();
		request.set("owner_screen_name", ownerScreenName);
		request.set("slug", listSlug);
		return restTemplate.postForObject(buildUri("lists/subscribers/destroy.json"), request, UserList.class);
	}

	public List<UserList> getMemberships(long userId) {
		requireUserAuthorization();
		return restTemplate.getForObject(buildUri("lists/memberships.json", Collections.singletonMap("user_id", String.valueOf(userId))), UserListList.class).getList();
	}

	public List<UserList> getMemberships(String screenName) {
		requireUserAuthorization();
		return restTemplate.getForObject(buildUri("lists/memberships.json", Collections.singletonMap("screen_name", screenName)), UserListList.class).getList();
	}

	public List<UserList> getSubscriptions(long userId) {
		requireUserAuthorization();
		return restTemplate.getForObject(buildUri("lists/subscriptions.json", Collections.singletonMap("user_id", String.valueOf(userId))), UserListList.class).getList();
	}

	public List<UserList> getSubscriptions(String screenName) {
		requireUserAuthorization();
		return restTemplate.getForObject(buildUri("lists/subscriptions.json", Collections.singletonMap("screen_name", screenName)), UserListList.class).getList();
	}

	public boolean isMember(long listId, long memberId) {
		requireUserAuthorization();
		Map<String, String> parameters = new TreeMap<String, String>();
		parameters.put("list_id", String.valueOf(listId));
		parameters.put("user_id", String.valueOf(memberId));
		return checkListConnection(buildUri("lists/members/show.json", parameters));
	}

	public boolean isMember(String screenName, String listSlug, String memberScreenName) {
		requireUserAuthorization();
		Map<String, String> parameters = new TreeMap<String, String>();
		parameters.put("owner_screen_name", screenName);
		parameters.put("slug", listSlug);
		parameters.put("screen_name", memberScreenName);
		return checkListConnection(buildUri("lists/members/show.json", parameters));
	}

	public boolean isSubscriber(long listId, long subscriberId) {
		Map<String, String> parameters = new TreeMap<String, String>();
		parameters.put("list_id", String.valueOf(listId));
		parameters.put("user_id", String.valueOf(subscriberId));
		return checkListConnection(buildUri("lists/subscribers/show.json", parameters));
	}

	public boolean isSubscriber(String screenName, String listSlug, String subscriberScreenName) {
		requireUserAuthorization();
		Map<String, String> parameters = new TreeMap<String, String>();
		parameters.put("owner_screen_name", screenName);
		parameters.put("slug", listSlug);
		parameters.put("screen_name", subscriberScreenName);
		return checkListConnection(buildUri("lists/subscribers/show.json", parameters));
	}

	// private helpers

	private boolean checkListConnection(URI uri) {
		try {
			restTemplate.getForObject(uri, String.class);
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

	@SuppressWarnings("serial")
	private static class TweetList extends ArrayList<Tweet> {}

}
