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
package org.springframework.social.twitter.search;

import java.util.List;

import org.springframework.social.twitter.Tweet;

/**
 * Interface defining the operations for searching Twitter and retrieving trending data.
 * @author Craig Walls
 */
public interface SearchOperations {
	
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
	
	/**
	 * Retrieves the authenticating user's saved searches.
	 * @return a list of SavedSearch items
	 */
	List<SavedSearch> getSavedSearches();
	
	/**
	 * Retrieves a single saved search by the saved search's ID.
	 * @param searchId the ID of the saved search
	 * @return a SavedSearch
	 */
	SavedSearch getSavedSearch(long searchId);

	/**
	 * Creates a new saved search for the authenticating user.
	 * @param query the search query to save
	 */
	void createSavedSearch(String query);

	/**
	 * Deletes a saved search
	 * @param searchId the ID of the saved search
	 */
	void deleteSavedSearch(long searchId);

	/**
	 * Retrieves the current top 10 trending topics on Twitter, including hashtagged topics.
	 * @return a Trends object containing a list of trending topics and the date/time that the list was created.
	 */
	Trends getCurrentTrends();

	/**
	 * Retrieves the current top 10 trending topics on Twitter.
	 * @param excludeHashtags if true, hashtagged topics will be excluded from the trends list.
	 * @return a Trends object containing a list of trending topics and the date/time that the list was created.
	 */
	Trends getCurrentTrends(boolean excludeHashtags);

	/**
	 * Retrieves the top 20 trending topics, hourly for the past 24 hours.
	 * This list includes hashtagged topics.
	 * @return a list of Trends objects, one for each hour in the past 24 hours, ordered with the most recent hour first.
	 */
	List<Trends> getDailyTrends();

	/**
	 * Retrieves the top 20 trending topics, hourly for the past 24 hours.
	 * @param excludeHashtags if true, hashtagged topics will be excluded from the trends list.
	 * @return a list of Trends objects, one for each hour in the past 24 hours, ordered with the most recent hour first.
	 */
	List<Trends> getDailyTrends(boolean excludeHashtags);
	
	/**
	 * Retrieves the top 20 trending topics, hourly for a 24-hour period starting at the specified date.
	 * @param excludeHashtags if true, hashtagged topics will be excluded from the trends list.
	 * @param startDate the date to start retrieving trending data for, in MM-DD-YYYY format.
	 * @return a list of Trends objects, one for each hour in the given 24 hours, ordered with the most recent hour first.
	 */
	List<Trends> getDailyTrends(boolean excludeHashtags, String startDate);

	/**
	 * Retrieves the top 30 trending topics for each day in the past week.
	 * This list includes hashtagged topics.
	 * @return a list of Trends objects, one for each day in the past week, ordered with the most recent day first.
	 */
	List<Trends> getWeeklyTrends();

	/**
	 * Retrieves the top 30 trending topics for each day in the past week.
	 * @param excludeHashtags if true, hashtagged topics will be excluded from the trends list.
	 * @return a list of Trends objects, one for each day in the past week, ordered with the most recent day first.
	 */
	List<Trends> getWeeklyTrends(boolean excludeHashtags);
	
	/**
	 * Retrieves the top 30 trending topics for each day in a given week.
	 * @param excludeHashtags if true, hashtagged topics will be excluded from the trends list.
	 * @param startDate the date to start retrieving trending data for, in MM-DD-YYYY format.
	 * @return a list of Trends objects, one for each day in the given week, ordered with the most recent day first.
	 */
	List<Trends> getWeeklyTrends(boolean excludeHashtags, String startDate);

	/**
	 * Retrieves the top 10 trending topics for a given location, identified by its "Where on Earth" (WOE) ID.
	 * This includes hashtagged topics.
	 * See http://developer.yahoo.com/geo/geoplanet/guide/concepts.html for more information on WOE.
	 * @param whereOnEarthId the Where on Earth ID for the location to retrieve trend data.
	 * @return A Trends object with the top 10 trending topics for the location.
	 */
	Trends getLocalTrends(long whereOnEarthId);

	/**
	 * Retrieves the top 10 trending topics for a given location, identified by its "Where on Earth" (WOE) ID.
	 * See http://developer.yahoo.com/geo/geoplanet/guide/concepts.html for more information on WOE.
	 * @param whereOnEarthId the Where on Earth ID for the location to retrieve trend data.
	 * @param excludeHashtags if true, hashtagged topics will be excluded from the trends list.
	 * @return A Trends object with the top 10 trending topics for the given location.
	 */
	Trends getLocalTrends(long whereOnEarthId, boolean excludeHashtags);

}
