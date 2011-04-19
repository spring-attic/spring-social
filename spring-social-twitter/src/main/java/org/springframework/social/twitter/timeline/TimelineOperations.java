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
package org.springframework.social.twitter.timeline;

import java.util.List;

import org.springframework.social.twitter.Tweet;
import org.springframework.social.twitter.TwitterProfile;

/**
 * Interface defining the operations for sending and retrieving tweets. 
 * @author Craig Walls
 */
public interface TimelineOperations {

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
	 * 
	 * @return a collection of {@link Tweet}s in the public timeline.
	 */
	List<Tweet> getPublicTimeline();

	/**
	 * Retrieves the 20 most recently posted tweets, including retweets, from
	 * the authenticating user's home timeline. The home timeline includes
	 * tweets from the user's timeline and the timeline of anyone that they
	 * follow.
	 * 
	 * @return a collection of {@link Tweet}s in the authenticating user's home
	 *         timeline.
	 */
	List<Tweet> getHomeTimeline();

	/**
	 * <p>
	 * Retrieves the 20 most recently posted tweets, excluding retweets, from
	 * the authenticating user's home timeline. The friends timeline includes
	 * tweets from the user's timeline and the timeline of anyone that they
	 * follow, with the exception of any retweets.
	 * </p>
	 * 
	 * @return a collection of {@link Tweet}s in the authenticating user's
	 *         friends timeline.
	 */
	List<Tweet> getFriendsTimeline();

	/**
	 * Retrieves the 20 most recent tweets posted by the authenticating user.
	 * 
	 * @return a collection of {@link Tweet}s that have been posted by the
	 *         authenticating user.
	 */
	List<Tweet> getUserTimeline();

	/**
	 * Retrieves the 20 most recent tweets posted by the given user.
	 * 
	 * @param screenName
	 *            The screen name of the user whose timeline is being requested.
	 * @return a collection of {@link Tweet}s from the specified user's
	 *         timeline.
	 */
	List<Tweet> getUserTimeline(String screenName);

	/**
	 * Retrieves the 20 most recent tweets posted by the given user.
	 * 
	 * @param userId
	 *            The user ID of the user whose timeline is being requested.
	 * @return a collection of {@link Tweet}s from the specified user's
	 *         timeline.
	 */
	List<Tweet> getUserTimeline(long userId);

	/**
	 * Retrieve the 20 most recent tweets that mention the authenticated user.
	 * @return a collection of {@link Tweet} objects that mention the authenticated user.
	 */
	List<Tweet> getMentions();

	/**
	 * Retrieve the 20 most recent retweets posted by the authenticated user.
	 */
	List<Tweet> getRetweetedByMe();

	/**
	 * Retrieve the 20 most recent retweets posted by users the authenticating user follow.
	 */
	List<Tweet> getRetweetedToMe();

	/**
	 * Retrieve the 20 most recent tweets of the authenticated user that have been retweeted by others.
	 */
	List<Tweet> getRetweetsOfMe();

	/**
	 * Returns a single tweet.
	 * @param tweetId the tweet's ID
	 */
	Tweet getStatus(long tweetId);

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
	 * Removes a status entry.
	 * @param tweetId the tweet's ID
	 */
	void deleteStatus(long tweetId);

	/**
	 * Posts a retweet of an existing tweet.
	 * @param tweetId The ID of the tweet to be retweeted
	 */
	void retweet(long tweetId);

	/**
	 * Retrieves up to 100 retweets of a specific tweet.
	 * @param tweetId the tweet's ID
	 */
	List<Tweet> getRetweets(long tweetId);

	/**
	 * Retrieves the profiles of up to 100 users how have retweeted a specific tweet.
	 * @param id the tweet's ID
	 */
	List<TwitterProfile> getRetweetedBy(long id);

	/**
	 * Retrieves the IDs of up to 100 users who have retweeted a specific tweet.
	 * @param id the tweet's ID.
	 */
	List<Long> getRetweetedByIds(long id);

	/**
	 * Retrieves the 20 most recent tweets favorited by the given user.
	 * 
	 * @return a collection of {@link Tweet}s from the specified user's favorite
	 *         timeline.
	 */
	List<Tweet> getFavorites();

	/**
	 * Adds a tweet to the user's collection of favorite tweets.
	 * @param id the tweet's ID
	 */
	void addToFavorites(long id);

	/**
	 * Removes a tweet from the user's collection of favorite tweets.
	 * @param id the tweet's ID
	 */
	void removeFromFavorites(long id);

}
