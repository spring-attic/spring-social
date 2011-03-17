package org.springframework.social.twitter;

import java.util.List;

public interface UserApi {

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
	 * Retrieves a list of Twitter profiles for the given list of user IDs.
	 */
	List<TwitterProfile> getUsers(long... userIds);

	/**
	 * Retrieves a list of Twitter profiles for the given list of screen names.
	 */
	List<TwitterProfile> getUsers(String... screenNames);
	
	/**
	 * Searches for users that match a given query.
	 */
	List<TwitterProfile> searchForUsers(String query);
	
	/**
	 * Retrieves a list of categories from which suggested users to follow may be found.
	 */
	List<SuggestionCategory> getSuggestionCategories();

	/**
	 * Retrieves a list of suggestions of users to follow for a given category.
	 * @param slug the category's slug
	 */
	List<TwitterProfile> getSuggestions(String slug);

}
