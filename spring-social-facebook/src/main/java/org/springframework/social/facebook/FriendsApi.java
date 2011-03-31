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
package org.springframework.social.facebook;

import java.util.List;

import org.springframework.social.facebook.types.Reference;

public interface FriendsApi {

	/**
	 * Retrieves a list of custom friend lists belonging to the authenticated user.
	 * Requires "read_friendlists" permission.
	 * @return a list {@link Reference}s, each representing a friends list for the user, or an empty list if not available.
	 */
	List<Reference> getFriendLists();

	/**
	 * Retrieves a list of custom friend lists belonging to the specified user.
	 * Requires "read_friendlists" permission.
	 * @param userId the user's ID
	 * @return a list {@link Reference}s, each representing a friends list for the user, or an empty list if not available.
	 */
	List<Reference> getFriendLists(String userId);

	Reference getFriendList(String friendListId);
	
	List<Reference> getFriendListMembers(String friendListId);
	
	Reference createFriendList(String userId, String name);
	
	void deleteFriendList(String friendListId);

	void addToFriendList(String friendListId, String friendId);
	
	void removeFromFriendList(String friendListId, String friendId);
	
	/**
	 * Retrieves a list of user references for the authenticated user's friends.
	 * @return a list {@link Reference}s, each representing a friend of the user, or an empty list if not available.
	 */
	List<Reference> getFriends();

	/**
	 * Retrieves a list of user references for the authenticated user's friends.
	 * @param userId the user's ID
	 * @return a list {@link Reference}s, each representing a friend of the user, or an empty list if not available.
	 */
	List<Reference> getFriends(String userId);

}
