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
package org.springframework.social.twitter.friend;

import java.util.List;

import org.springframework.social.twitter.TwitterProfile;

/**
 * Interface defining the operations for working with a user's friends and followers.
 * @author Craig Walls
 */
public interface FriendOperations {

	/**
	 * Retrieves a list of users that the given user follows.
	 * @param userId The user's Twitter ID
	 * @return a list of TwitterProfiles
	 */
	List<TwitterProfile> getFriends(long userId);

	/**
	 * Retrieves a list of users that the given user follows.
	 * @param screenName The user's Twitter screen name
	 * @return a list of TwitterProfiles
	 */
	List<TwitterProfile> getFriends(String screenName);

	/**
	 * Retrieves a list of IDs for the Twitter users that the given user follows.
	 * @param userId the user's Twitter ID
	 * @return a list of user IDs
	 */
	List<Long> getFriendIds(long userId);

	/**
	 * Retrieves a list of IDs for the Twitter users that the given user follows.
	 * @param screenName the user's Twitter screen name
	 * @return a list of user IDs
	 */
	List<Long> getFriendIds(String screenName);

	/**
	 * Retrieves a list of users that the given user is being followed by
	 * @param userId The user's Twitter ID
	 * @return a list of TwitterProfiles
	 */
	List<TwitterProfile> getFollowers(long userId);
	
	/**
	 * Retrieves a list of users that the given user is being followed by
	 * @param screenName The user's Twitter screen name
	 * @return a list of TwitterProfiles
	 */
	List<TwitterProfile> getFollowers(String screenName);
	
	/**
	 * Retrieves a list of IDs for the Twitter users that follow the given user.
	 * @param userId the user's Twitter ID
	 * @return a list of user IDs
	 */
	List<Long> getFollowerIds(long userId);

	/**
	 * Retrieves a list of IDs for the Twitter users that follow the given user.
	 * @param screenName the user's Twitter screen name
	 * @return a list of user IDs
	 */
	List<Long> getFollowerIds(String screenName);

	/**
	 * Allows the authenticated user to follow (create a friendship) with another user.
	 * @param userId The Twitter ID of the user to follow
	 * @return the name of the followed user if successful
	 */
	String follow(long userId);
	
	/**
	 * Allows the authenticated user to follow (create a friendship) with another user.
	 * @param screenName The screen name of the user to follow
	 * @return the name of the followed user if successful
	 */
	String follow(String screenName);

	/**
	 * Allows the authenticated use to unfollow (destroy a friendship) with another user
	 * @param userId the Twitter ID of the user to unfollow 
	 * @return the name of the unfolloed user if successful 
	 */
	String unfollow(long userId);
	
	/**
	 * Allows the authenticated use to unfollow (destroy a friendship) with another user
	 * @param screenName the screen name of the user to unfollow 
	 * @return the name of the unfolloed user if successful 
	 */
	String unfollow(String screenName);
	
	/**
	 * Checks for a friendship between two users. Returns true if userA follows userB.
	 * @param userA the screen name of userA
	 * @param userB the screen name of userB
	 */
	boolean friendshipExists(String userA, String userB);

	/**
	 * Returns an array of numeric IDs for every user who has a pending request to follow the authenticating user.
	 */
	List<Long> getIncomingFriendships();

	/**
	 * Returns an array of numeric IDs for every protected user for whom the authenticating user has a pending follow request.
	 */
	List<Long> getOutgoingFriendships();
}
