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

public interface SearchApi {
	
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
}
