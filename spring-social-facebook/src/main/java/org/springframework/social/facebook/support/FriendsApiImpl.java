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
package org.springframework.social.facebook.support;

import java.util.List;
import java.util.Map;

import org.springframework.social.facebook.FriendsApi;
import org.springframework.social.facebook.types.Reference;
import org.springframework.web.client.RestTemplate;

public class FriendsApiImpl extends AbstractFacebookApi implements FriendsApi {
	
	private final RestTemplate restTemplate;

	public FriendsApiImpl(RestTemplate restTemplate) {
		super(restTemplate);
		this.restTemplate = restTemplate;
	}
	
	public List<Reference> getFriendLists() {
		return getFriendLists("me");
	}

	public List<Reference> getFriendLists(String userId) {
		return getObjectConnection(userId, "friendlists", referenceExtractor);
	}
	
	public Reference getFriendList(String friendListId) {
		return getObject(friendListId, referenceExtractor);
	}
	
	public List<Reference> getFriendListMembers(String friendListId) {
		return getObjectConnection(friendListId, "members", referenceExtractor);
	}
	
	public Reference createFriendList(String name) {
		return createFriendList("me", name);
	}
	
	public Reference createFriendList(String userId, String name) {
		@SuppressWarnings("unchecked")
		Map<String, Object> friendListMap = restTemplate.postForObject(CONNECTION_URL + "?name={name}", "", Map.class, userId, "friendlists", name);
		return referenceExtractor.extractObject(friendListMap);
	}
	
	public void deleteFriendList(String friendListId) {
		delete(friendListId);
	}

	public void addToFriendList(String friendListId, String friendId) {
		restTemplate.postForObject(CONNECTION_URL + "/{friendId}", "", String.class, friendListId, "members", friendId);
	}
	
	public void removeFromFriendList(String friendListId, String friendId) {
		restTemplate.delete(CONNECTION_URL + "/{friendId}", friendListId, "members", friendId);
	}
	
	public List<Reference> getFriends() {
		return getFriends("me");
	}

	public List<Reference> getFriends(String userId) {
		return getObjectConnection(userId, "friends", referenceExtractor);
	}

}
