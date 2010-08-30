package org.springframework.social.twitter;

import java.util.List;

public interface TwitterOperations {

	String getScreenName();
	
	List<String> getFollowed(String screenName);
	
	void tweet(String message);

	void retweet(long tweetId);

	SearchResults search(String query);

	SearchResults search(String query, int page, int pageSize);

	SearchResults search(String query, int page, int resultsPerPage, int sinceId, int maxId);
}