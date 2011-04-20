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
package org.springframework.social.twitter.api.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.social.twitter.api.SavedSearch;
import org.springframework.social.twitter.api.SearchOperations;
import org.springframework.social.twitter.api.SearchResults;
import org.springframework.social.twitter.api.Trends;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

/**
 * Implementation of {@link SearchOperations}, providing a binding to Twitter's search and trend-oriented REST resources.
 * @author Craig Walls
 */
class SearchTemplate extends AbstractTwitterOperations implements SearchOperations {

	private final RestTemplate restTemplate;
		
	public SearchTemplate(RestTemplate restTemplate, boolean isAuthorizedForUser) {
		super(isAuthorizedForUser);
		this.restTemplate = restTemplate;
	}

	public SearchResults search(String query) {
		return search(query, 1, DEFAULT_RESULTS_PER_PAGE, 0, 0);
	}

	public SearchResults search(String query, int page, int resultsPerPage) {
		return search(query, page, resultsPerPage, 0, 0);
	}

	public SearchResults search(String query, int page, int resultsPerPage, int sinceId, int maxId) {
		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put("query", query);
		parameters.put("rpp", String.valueOf(resultsPerPage));
		parameters.put("page", String.valueOf(page));
		String searchUrl = SEARCH_URL;
		if (sinceId > 0) {
			searchUrl += "&since_id={since}";
			parameters.put("since", String.valueOf(sinceId));
		}
		if (maxId > 0) {
			searchUrl += "&max_id={max}";
			parameters.put("max", String.valueOf(maxId));
		}
		return restTemplate.getForObject(searchUrl, SearchResults.class, parameters);
	}

	public List<SavedSearch> getSavedSearches() {
		requireUserAuthorization();
		return restTemplate.getForObject(buildUri("saved_searches.json"), SavedSearchList.class);
	}

	public SavedSearch getSavedSearch(long searchId) {
		requireUserAuthorization();
		return restTemplate.getForObject(buildUri("saved_searches/show/" + searchId + ".json"), SavedSearch.class);
	}

	public void createSavedSearch(String query) {		
		requireUserAuthorization();
		MultiValueMap<String, Object> data = new LinkedMultiValueMap<String, Object>();
		data.set("query", query);
		restTemplate.postForObject(buildUri("saved_searches/create.json"), data, String.class);
	}

	public void deleteSavedSearch(long searchId) {
		requireUserAuthorization();
		restTemplate.delete(buildUri("saved_searches/destroy/" + searchId + ".json"));
	}
	
	// Trends

	public Trends getCurrentTrends() {
		return getCurrentTrends(false);
	}

	public Trends getCurrentTrends(boolean excludeHashtags) {
		String path = makeTrendPath("trends/current.json", excludeHashtags, null);
		return restTemplate.getForObject(buildUri(path), DailyTrendsList.class).getList().get(0);
	}

	public List<Trends> getDailyTrends() {
		return getDailyTrends(false, null);
	}

	public List<Trends> getDailyTrends(boolean excludeHashtags) {
		return getDailyTrends(excludeHashtags, null);
	}

	public List<Trends> getDailyTrends(boolean excludeHashtags, String startDate) {
		String path = makeTrendPath("trends/daily.json", excludeHashtags, startDate);
		return restTemplate.getForObject(buildUri(path), DailyTrendsList.class).getList();
	}
	
	public List<Trends> getWeeklyTrends() {
		return getWeeklyTrends(false, null);
	}
	
	public List<Trends> getWeeklyTrends(boolean excludeHashtags) {
		return getWeeklyTrends(excludeHashtags, null);
	}
	
	public List<Trends> getWeeklyTrends(boolean excludeHashtags, String startDate) {
		String path = makeTrendPath("trends/weekly.json", excludeHashtags, startDate);
		return restTemplate.getForObject(buildUri(path), WeeklyTrendsList.class).getList();
	}

	public Trends getLocalTrends(long whereOnEarthId) {
		return getLocalTrends(whereOnEarthId, false);
	}

	public Trends getLocalTrends(long whereOnEarthId, boolean excludeHashtags) {
		Map<String, String> params = new HashMap<String, String>();
		if(excludeHashtags) {
			params.put("exclude", "hashtags");
		}
		return restTemplate.getForObject(buildUri("trends/" + whereOnEarthId + ".json", params), LocalTrendsHolder.class).getTrends();
	}

	private String makeTrendPath(String basePath, boolean excludeHashtags, String startDate) {
		String url = basePath + (excludeHashtags || startDate != null ? "?" : "");
		url += excludeHashtags ? "exclude=hashtags" : "";
		url += excludeHashtags && startDate != null ? "&" : "";
		url += startDate != null ? "date=" + startDate : "";
		return url;
	}

	static final int DEFAULT_RESULTS_PER_PAGE = 50;

	private static final String SEARCH_API_URL_BASE = "https://search.twitter.com";
	private static final String SEARCH_URL = SEARCH_API_URL_BASE + "/search.json?q={query}&rpp={rpp}&page={page}";
}
