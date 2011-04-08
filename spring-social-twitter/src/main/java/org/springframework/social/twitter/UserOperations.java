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
package org.springframework.social.twitter;

import java.util.List;

import org.springframework.social.twitter.types.SuggestionCategory;
import org.springframework.social.twitter.types.TwitterProfile;

/**
 * Interface defining the operations for retrieving information about Twitter users.
 * @author Craig Walls
 */
public interface UserOperations {

	/**
	 * Retrieves the authenticated user's Twitter ID.
	 * @return the user's ID at Twitter
	 */
	long getProfileId();
	
	/**
	 * Retrieves the authenticated user's Twitter screen name
	 * @return the user's screen name
	 */
	String getScreenName();

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
