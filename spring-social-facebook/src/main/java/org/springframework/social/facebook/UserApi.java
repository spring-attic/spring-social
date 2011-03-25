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
	 * Retrieves a list of things that the authenticated user has liked.
	 * Requires "user_likes" permission.
	 * Returns an empty list if permission isn't granted.
	 * @return a list of {@link UserLike} objects
	 */
	List<UserLike> getLikes();
	
	/**
	 * Retrieves a list of things that the given user has liked.
	 * Requires "user_likes" permission for the authenticated user and "friends_likes" for the authenticated user's friends.
	 * Returns an empty list if permission isn't granted.
	 * @param userId the ID of the user
	 * @return a list of {@link UserLike} objects
	 */
	List<UserLike> getLikes(String userId);
	
	/**
	 * Retrieves a list of books that the authenticated user likes.
	 * Requires "user_likes" permission.
	 * Returns an empty list if permission isn't granted.
	 * @return a list of {@link UserLike} objects
	 */
	List<UserLike> getBooks();

	/**
	 * Retrieves a list of books that the given user has liked.
	 * Requires "user_likes" permission for the authenticated user and "friends_likes" for the authenticated user's friends.
	 * Returns an empty list if permission isn't granted.
	 * @param userId the ID of the user
	 * @return a list of {@link UserLike} objects
	 */
	List<UserLike> getBooks(String userId);

	/**
	 * Retrieves a list of movies that the authenticated user likes.
	 * Requires "user_likes" permission.
	 * Returns an empty list if permission isn't granted.
	 * @return a list of {@link UserLike} objects
	 */
	List<UserLike> getMovies();

	/**
	 * Retrieves a list of movies that the given user has liked.
	 * Requires "user_likes" permission for the authenticated user and "friends_likes" for the authenticated user's friends.
	 * Returns an empty list if permission isn't granted.
	 * @param userId the ID of the user
	 * @return a list of {@link UserLike} objects
	 */
	List<UserLike> getMovies(String userId);

	/**
	 * Retrieves a list of music that the authenticated user likes.
	 * Requires "user_likes" permission.
	 * Returns an empty list if permission isn't granted.
	 * @return a list of {@link UserLike} objects
	 */
	List<UserLike> getMusic();

	/**
	 * Retrieves a list of music that the given user has liked.
	 * Requires "user_likes" permission for the authenticated user and "friends_likes" for the authenticated user's friends.
	 * Returns an empty list if permission isn't granted.
	 * @param userId the ID of the user
	 * @return a list of {@link UserLike} objects
	 */
	List<UserLike> getMusic(String userId);

	/**
	 * Retrieves a list of television shows that the authenticated user likes.
	 * Requires "user_likes" permission.
	 * Returns an empty list if permission isn't granted.
	 * @return a list of {@link UserLike} objects
	 */
	List<UserLike> getTelevision();

	/**
	 * Retrieves a list of television shows that the given user has liked.
	 * Requires "user_likes" permission for the authenticated user and "friends_likes" for the authenticated user's friends.
	 * Returns an empty list if permission isn't granted.
	 * @param userId the ID of the user
	 * @return a list of {@link UserLike} objects
	 */
	List<UserLike> getTelevision(String userId);

	/**
	 * Retrieves a list of activities that the authenticated user likes.
	 * Requires "user_activities" permission.
	 * Returns an empty list if permission isn't granted.
	 * @return a list of {@link UserLike} objects
	 */
	List<UserLike> getActivities();

	/**
	 * Retrieves a list of activities that the given user likes.
	 * Requires "user_activities" permission for the authenticated user and "friends_activities" for the authenticated user's friends.
	 * Returns an empty list if permission isn't granted.
	 * @param userId the ID of the user
	 * @return a list of {@link UserLike} objects
	 */
	List<UserLike> getActivities(String userId);

	/**
	 * Retrieves a list of interests that the authenticated user likes.
	 * Requires "user_interests" permission.
	 * Returns an empty list if permission isn't granted.
	 * @return a list of {@link UserLike} objects
	 */
	List<UserLike> getInterests();

	/**
	 * Retrieves a list of interests that the given user likes.
	 * Requires "user_interests" permission for the authenticated user and "friends_interests" for the authenticated user's friends.
	 * Returns an empty list if permission isn't granted.
	 * @param userId the ID of the user
	 * @return a list of {@link UserLike} objects
	 */
	List<UserLike> getInterests(String userId);

	List<Checkin> getCheckins();

	List<Checkin> getCheckins(String userId);

	List<UserEvent> getEvents();

	List<UserEvent> getEvents(String userId);

	List<Album> getAlbums();

	List<Album> getAlbums(String userId);
}
