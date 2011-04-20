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
package org.springframework.social.facebook.api;

import java.util.List;


/**
 * Defines operations for interacting with a user's friends and friend lists.
 * @author Craig Walls
 */
public interface FriendOperations {

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

	/**
	 * Retrieves a reference to the specified friend list.
	 * @param friendListId the friend list ID.
	 * @return a {@link Reference} to the requested friend list.
	 */
	Reference getFriendList(String friendListId);
	
	/**
	 * Retrieves references for all users who are members of the specified friend list.
	 * @param friendListId the friend list ID.
	 * @return a list of {@link Reference}, each representing a member of the friend list.
	 */
	List<Reference> getFriendListMembers(String friendListId);

	/**
	 * Creates a new friend list for the authenticated user.
	 * @param name the name of the friend list.
	 * @return a {@link Reference} to the newly created friend list.
	 */
	Reference createFriendList(String name);

	/**
	 * Creates a new friend list.
	 * @param userId the user ID to create the friend list for.
	 * @param name the name of the friend list.
	 * @return a {@link Reference} to the newly created friend list.
	 */
	Reference createFriendList(String userId, String name);
	
	/**
	 * Deletes a friend list.
	 * @param friendListId the ID of the friend list to remove.
	 */
	void deleteFriendList(String friendListId);

	/**
	 * Adds a friend to a friend list.
	 * @param friendListId the friend list ID
	 * @param friendId The ID of the user to add to the list. The user must be a friend of the list's owner.
	 */
	void addToFriendList(String friendListId, String friendId);
	
	/**
	 * Removes a friend from a friend list.
	 * @param friendListId the friend list ID
	 * @param friendId The ID of the user to add to the list.
	 */
	void removeFromFriendList(String friendListId, String friendId);
	
	/**
	 * Retrieves a list of user references for the authenticated user's friends.
	 * @return a list {@link Reference}s, each representing a friend of the user, or an empty list if not available.
	 */
	List<Reference> getFriends();
	
	/**
	 * Retrieves a list of the authenticating user's friends' IDs.
	 * @return a list of Strings, where each entry is the ID of one of the user's friends.
	 */
	List<String> getFriendIds();
	
	/**
	 * Retrieves profile data for the authenticated user's friends.
	 * @return a list {@link FacebookProfile}s, each representing a friend of the user, or an empty list if not available.
	 */
	List<FacebookProfile> getFriendProfiles();

	/**
	 * Retrieves a list of user references for the specified user's friends.
	 * @param userId the user's ID
	 * @return a list {@link Reference}s, each representing a friend of the user, or an empty list if not available.
	 */
	List<Reference> getFriends(String userId);

	/**
	 * Retrieves a list of the authenticating user's friends' IDs.
	 * @param userId the user's ID
	 * @return a list of Strings, where each entry is the ID of one of the user's friends.
	 */
	List<String> getFriendIds(String userId);
	
	/**
	 * Retrieves profile data for the specified user's friends.
	 * @param userId the user's ID
	 * @return a list {@link FacebookProfile}s, each representing a friend of the user, or an empty list if not available.
	 */
	List<FacebookProfile> getFriendProfiles(String userId);
}
