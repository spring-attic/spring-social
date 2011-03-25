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

public interface UserApi {
	
	/**
	 * Retrieves the profile for the authenticated user.
	 * @return the user's profile information.
	 */
	FacebookProfile getUserProfile();
	
	/**
	 * Retrieves the profile for the authenticated user.
	 * @param username the Facebook username to retrieve profile data for.
	 * @return the user's profile information.
	 */
	FacebookProfile getUserProfile(String username);

	/**
	 * Retrieves a list of checkins for the authenticated user.
	 * Requires "user_checkins" or "friends_checkins" permission.
	 * @return a list {@link Checkin}s for the user, or an empty list if not available.
	 */
	List<Checkin> getCheckins();

	/**
	 * Retrieves a list of checkins for the specified user.
	 * Requires "user_checkins" or "friends_checkins" permission.
	 * @param userId the user's ID
	 * @return a list {@link Checkin}s for the user, or an empty list if not available.
	 */
	List<Checkin> getCheckins(String userId);

	// TODO: Consider moving events methods to an EventsApi
	/**
	 * Retrieves a list of events that the authenticated user has been invited to.
	 * Requires "user_events" or "friends_events" permission.
	 * @return a list {@link UserEvent}s for the user, or an empty list if not available.
	 */
	List<UserEvent> getEvents();

	/**
	 * Retrieves a list of events that the specified user has been invited to.
	 * Requires "user_events" or "friends_events" permission.
	 * @param userId the user's ID
	 * @return a list {@link UserEvent}s for the user, or an empty list if not available.
	 */
	List<UserEvent> getEvents(String userId);

	// TODO: Consider moving album methods to a PhotoApi or MediaApi or some such thing
	/**
	 * Retrieves a list of albums belonging to the authenticated user.
	 * Requires "user_photos" or "friends_photos" permission.
	 * @return a list {@link Album}s for the user, or an empty list if not available.
	 */
	List<Album> getAlbums();

	/**
	 * Retrieves a list of albums belonging to the specified user.
	 * Requires "user_photos" or "friends_photos" permission.
	 * @param userId the user's ID
	 * @return a list {@link Album}s for the user, or an empty list if not available.
	 */
	List<Album> getAlbums(String userId);

	// TODO: Consider moving friend methods to a FriendApi
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
