package org.springframework.social.twitter;

import java.util.List;

import org.springframework.social.oauth.AccessTokenProvider;

public interface TwitterOperations {

	String getScreenName(AccessTokenProvider<?> tokenProvider);
	
	List<String> getFriends(String screenName);
	
	void updateStatus(String message, AccessTokenProvider<?> tokenProvider);

	SearchResults search(String query, AccessTokenProvider<?> tokenProvider);

	SearchResults search(String query, int page, int pageSize);

	SearchResults search(String query, int page, int pageSize, AccessTokenProvider<?> tokenProvider);

	SearchResults search(String query, int page, int resultsPerPage, int sinceId, int maxId,
			AccessTokenProvider<?> tokenProvider);
}