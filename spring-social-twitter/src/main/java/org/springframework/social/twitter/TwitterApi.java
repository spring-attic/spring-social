/*
 * Copyright 2010 the original author or authors.
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

import java.util.List;

/**
 * Interface specifying a basic set of operations for interacting with Twitter.
 * Implemented by TwitterTemplate. Not often used directly, but a useful option
 * to enhance testability, as it can easily be mocked or stubbed.
 *
 * @author Craig Walls
 */
public interface TwitterApi {

	/**
	 * Retrieves the user's Twitter screen name.
	 * @return the user's screen name at Twitter
	 */
	String getProfileId();

	/**
	 * Retrieves the authenticated user's Twitter profile details.
	 * @return a {@link TwitterProfile} object representing the user's profile.
	 */
	TwitterProfile getUserProfile();

	/**
	 * Retrieves a specific user's Twitter profile details.
	 * Note that this method does not require authentication.
	 * @param screenName the screen name for the user whose details are to be retrieved.
	 * @return a {@link TwitterProfile} object representing the user's profile.
	 */
	TwitterProfile getUserProfile(String screenName);

	/**
	 * Retrieves a specific user's Twitter profile details.
	 * Note that this method does not require authentication.
	 * @param userId the user ID for the user whose details are to be retrieved.
	 * @return a {@link TwitterProfile} object representing the user's profile.
	 */
	TwitterProfile getUserProfile(long userId);

	/**
	 * Retrieves a list of users that the given user follows.
	 * @param screenName The user's Twitter screen name
	 * @return a list of user screen names
	 */
	List<String> getFriends(String screenName);
	
	/**
	 * Retrieves a list of users that the given user is being followed by
	 * @param screenName The user's Twitter screen name
	 * @return a list of user screen names
	 */
	List<String> getFollowers(String screenName);
	
	/**
	 * Allows the authenticated user to follow (create a friendship) with another user.
	 * @param screenName The screen name of the user to follow
	 * @return the name of the followed user if successful
	 */
	String follow(String screenName);

	/**
	 * Allows the authenticated use to unfollow (destroy a friendship) with another user
	 * @param screenName the screen name of the use to unfollow 
	 * @return the name of the unfolloed user if successful 
	 */
	String unfollow(String screenName);
	
	/**
	 * Updates the user's status.
	 * @param status The status message
	 */
	void updateStatus(String status);

	/**
	 * Updates the user's status, including additional metadata concerning the status.
	 * @param status The status message
	 * @param details Metadata pertaining to the status
	 */
	void updateStatus(String status, StatusDetails details);

	/**
	 * Posts a retweet of an existing tweet.
	 * @param tweetId The ID of the tweet to be retweeted
	 */
	void retweet(long tweetId);

	/**
	 * Retrieve the 20 most recent tweets that mention the authenticated user.
	 * @return a collection of {@link Tweet} objects that mention the authenticated user.
	 */
	List<Tweet> getMentions();

	/**
	 * Retrieve the 20 most recently received direct messages for the authenticating user.
	 * @return a collection of {@link DirectMessage} with the authenticating user as the recipient.
	 */
	List<DirectMessage> getDirectMessagesReceived();

	/**
	 * Sends a direct message to another Twitter user.
	 * The recipient of the message must follow the authenticated user in order
	 * for the message to be delivered. If the recipient is not following the
	 * authenticated user, an {@link InvalidMessageRecipientException} will be thrown.
	 * @param toScreenName the screen name of the recipient of the messages.
	 * @param text the message text.
	 * @throws InvalidMessageRecipientException if the recipient is not following the authenticating user.
	 * @throws DuplicateTweetException if the message duplicates a previously sent message.
	 */
	void sendDirectMessage(String toScreenName, String text);

	/**
	 * Sends a direct message to another Twitter user.
	 * The recipient of the message must follow the authenticated user in order
	 * for the message to be delivered. If the recipient is not following the
	 * authenticated user, an {@link InvalidMessageRecipientException} will be thrown.
	 * @param toUserId the Twitter user ID of the recipient of the messages.
	 * @param text the message text.
	 * @throws InvalidMessageRecipientException if the recipient is not following the authenticating user.
	 * @throws DuplicateTweetException if the message duplicates a previously sent message.
	 */
	void sendDirectMessage(long toUserId, String text);

	/**
	 * Retrieves the 20 most recently posted tweets from the public timeline.
	 * The public timeline is the timeline containing tweets from all Twitter
	 * users. As this is the public timeline, authentication is not required to
	 * use this method.
	 * <p>
	 * Note that Twitter caches public timeline results for 60 seconds. Calling
	 * this method more frequently than that will count against rate limits and
	 * will not return any new results.
	 * </p>
	 * @return a collection of {@link Tweet}s in the public timeline.
	 */
	List<Tweet> getPublicTimeline();

	/**
	 * Retrieves the 20 most recently posted tweets, including retweets, from
	 * the authenticating user's home timeline. The home timeline includes
	 * tweets from the user's timeline and the timeline of anyone that they follow.
	 * @return a collection of {@link Tweet}s in the authenticating user's home timeline.
	 */
	List<Tweet> getHomeTimeline();

	/**
	 * <p>
	 * Retrieves the 20 most recently posted tweets, excluding retweets, from
	 * the authenticating user's home timeline. The friends timeline includes
	 * tweets from the user's timeline and the timeline of anyone that they
	 * follow, with the exception of any retweets.
	 * </p>
	 * @return a collection of {@link Tweet}s in the authenticating user's friends timeline.
	 */
	List<Tweet> getFriendsTimeline();

	/**
	 * Retrieves the 20 most recent tweets posted by the authenticating user.
	 * @return a collection of {@link Tweet}s that have been posted by the authenticating user.
	 */
	List<Tweet> getUserTimeline();

	/**
	 * Retrieves the 20 most recent tweets posted by the given user.
	 * @param screenName The screen name of the user whose timeline is being requested.
	 * @return a collection of {@link Tweet}s from the specified user's timeline.
	 */
	List<Tweet> getUserTimeline(String screenName);

	/**
	 * Retrieves the 20 most recent tweets posted by the given user.
	 * @param userId The user ID of the user whose timeline is being requested.
	 * @return a collection of {@link Tweet}s from the specified user's timeline.
	 */
	List<Tweet> getUserTimeline(long userId);

	/**
	 * Retrieves the 20 most recent tweets favorited by the given user.
	 * @return a collection of {@link Tweet}s from the specified user's favorite timeline.
	 */
	List<Tweet> getFavorites();

	/**
	 * Searches Twitter, returning the first 50 matching {@link Tweet}s
	 * @param query The search query string
	 * @return a {@link SearchResults} containing the search results metadata and a list of matching {@link Tweet}s
	 * @see SearchResults
	 * @see Tweet
	 */
	SearchResults search(String query);

	/**
	 * Searches Twitter, returning a specific page out of the complete set of results.
	 * @param query The search query string
	 * @param page The page to return
	 * @param pageSize The number of {@link Tweet}s per page
	 * @return a {@link SearchResults} containing the search results metadata and a list of matching {@link Tweet}s
	 * @see SearchResults
	 * @see Tweet
	 */
	SearchResults search(String query, int page, int pageSize);

	/**
	 * Searches Twitter, returning a specific page out of the complete set of
	 * results. Results are filtered to those whose ID falls between sinceId and maxId.
	 * @param query The search query string
	 * @param page The page to return
	 * @param pageSize The number of {@link Tweet}s per page
	 * @param sinceId The minimum {@link Tweet} ID to return in the results
	 * @param maxId The maximum {@link Tweet} ID to return in the results
	 * @return a {@link SearchResults} containing the search results metadata and a list of matching {@link Tweet}s
	 * @see SearchResults
	 * @see Tweet
	 */
	SearchResults search(String query, int page, int pageSize, int sinceId, int maxId);
}