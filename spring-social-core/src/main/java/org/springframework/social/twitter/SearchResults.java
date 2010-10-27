package org.springframework.social.twitter;

import java.util.List;

/**
 * Represents the results of a Twitter search, including matching {@link Tweet}s
 * and any metadata associated with that search.
 * 
 * @author Craig Walls
 * 
 * @see TwitterOperations.search()
 */
public class SearchResults {
	private List<Tweet> tweets;
	private long maxId;
	private long sinceId;
	private boolean lastPage;

	public SearchResults(List<Tweet> tweets, long maxId, long sinceId, boolean lastPage) {
		this.tweets = tweets;
		this.maxId = maxId;
		this.sinceId = sinceId;
		this.lastPage = lastPage;
	}

	/**
	 * Returns the list of matching {@link Tweet}s
	 */
	public List<Tweet> getTweets() {
		return tweets;
	}

	/**
	 * Returns the maximum {@link Tweet} ID in the search results
	 */
	public long getMaxId() {
		return maxId;
	}

	/**
	 * Returns the {@link Tweet} ID after which all of the matching
	 * {@link Tweet}s were created
	 */
	public long getSinceId() {
		return sinceId;
	}

	/**
	 * Returns <code>true</code> if this is the last page of matching
	 * {@link Tweet}s; <code>false</code> if there are more pages that follow
	 * this one.
	 */
	public boolean isLastPage() {
		return lastPage;
	}
}
