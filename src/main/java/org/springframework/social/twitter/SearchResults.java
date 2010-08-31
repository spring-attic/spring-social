package org.springframework.social.twitter;

import java.util.List;

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

	public List<Tweet> getTweets() {
		return tweets;
	}

	public long getMaxId() {
		return maxId;
	}

	public long getSinceId() {
		return sinceId;
	}

	public boolean isLastPage() {
		return lastPage;
	}
}
