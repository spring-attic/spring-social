package org.springframework.social.twitter;

import java.util.List;

public interface FriendsApi {

	/**
	 * Retrieves a list of users that the given user follows.
	 * @param screenName The user's Twitter screen name
	 * @return a list of user screen names
	 */
	List<String> getFriends(String screenName);

}
