package org.springframework.social.twitter;

import java.util.List;

/**
 * Interface specifying a basic set of operations for interacting with Twitter.
 * Implemented by TwitterTemplate. Not often used directly, but a useful option
 * to enhance testability, as it can easily be mocked or stubbed.
 * 
 * @author Craig Walls
 * 
 * @see TwitterTemplate
 */
public interface TwitterOperations {

	/**
	 * Retrieves the user's Twitter screen name.
	 * 
	 * @return the user's screen name at Twitter
	 */
	String getScreenName();
	
	/**
	 * Retrieves a list of users that the given user follows.
	 * 
	 * @param screenName
	 *            The user's Twitter screen name
	 * @return a list of user screen names
	 */
	List<String> getFollowed(String screenName);
	
	/**
	 * Updates the user's status.
	 * 
	 * @param message
	 *            The status message
	 */
	void tweet(String message);

	/**
	 * Posts a retweet of an existing tweet.
	 * 
	 * @param tweetId
	 *            The ID of the tweet to be retweeted
	 */
	void retweet(long tweetId);

	/**
	 * Searches Twitter, returning the first 50 matching {@link Tweet}s
	 * 
	 * @param query
	 *            The search query string
	 * @return a {@link SearchResults} containing the search results metadata
	 *         and a list of matching {@link Tweet}s
	 * 
	 * @see SearchResults, {@link Tweet}
	 */
	SearchResults search(String query);

	/**
	 * Searches Twitter, returning a specific page out of the complete set of
	 * results.
	 * 
	 * @param query
	 *            The search query string
	 * @param page
	 *            The page to return
	 * @param pageSize
	 *            The number of {@link Tweet}s per page
	 * 
	 * @return a {@link SearchResults} containing the search results metadata
	 *         and a list of matching {@link Tweet}s
	 * 
	 * @see SearchResults, {@link Tweet}
	 */
	SearchResults search(String query, int page, int pageSize);

	/**
	 * Searches Twitter, returning a specific page out of the complete set of
	 * results. Results are filtered to those whose ID falls between sinceId and
	 * maxId
	 * 
	 * @param query
	 *            The search query string
	 * @param page
	 *            The page to return
	 * @param pageSize
	 *            The number of {@link Tweet}s per page
	 * @param sinceId
	 *            The minimum {@link Tweet} ID to return in the results
	 * @param maxId
	 *            The maximum {@link Tweet} ID to return in the results
	 * 
	 * @return a {@link SearchResults} containing the search results metadata
	 *         and a list of matching {@link Tweet}s
	 * 
	 * @see SearchResults, {@link Tweet}
	 */
	SearchResults search(String query, int page, int resultsPerPage, int sinceId, int maxId);
}