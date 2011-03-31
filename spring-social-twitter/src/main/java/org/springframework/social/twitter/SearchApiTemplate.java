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

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.social.twitter.support.extractors.SavedSearchResponseExtractor;
import org.springframework.social.twitter.types.SavedSearch;
import org.springframework.social.twitter.types.SearchResults;
import org.springframework.social.twitter.types.Trend;
import org.springframework.social.twitter.types.Trends;
import org.springframework.social.twitter.types.Tweet;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.NumberUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.web.client.RestTemplate;

/**
 * Implementation of {@link SearchApi}, providing a binding to Twitter's search and trend-oriented REST resources.
 * @author Craig Walls
 */
public class SearchApiTemplate implements SearchApi {

	private final RestTemplate restTemplate;
	private SavedSearchResponseExtractor savedSearchExtractor;

	public SearchApiTemplate(RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
		this.savedSearchExtractor = new SavedSearchResponseExtractor();
	}

	public SearchResults search(String query) {
		return search(query, 1, DEFAULT_RESULTS_PER_PAGE, 0, 0);
	}

	public SearchResults search(String query, int page, int resultsPerPage) {
		return search(query, page, resultsPerPage, 0, 0);
	}

	@SuppressWarnings("unchecked")
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
		@SuppressWarnings("rawtypes")
		ResponseEntity<Map> response = restTemplate.getForEntity(searchUrl, Map.class, parameters);
		// handleResponseErrors(response);
		Map<String, Object> resultsMap = response.getBody();
		List<Map<String, Object>> items = (List<Map<String, Object>>) resultsMap.get("results");
		List<Tweet> tweets = new ArrayList<Tweet>(resultsMap.size());
		for (Map<String, Object> item : items) {
			tweets.add(populateTweetFromSearchResults(item));
		}
		return buildSearchResults(resultsMap, tweets);
	}

	@SuppressWarnings("unchecked")
	public List<SavedSearch> getSavedSearches() {
		List<Map<String, Object>> response = restTemplate.getForObject(SAVED_SEARCHES_URL, List.class);
		return savedSearchExtractor.extractObjects(response);
	}

	@SuppressWarnings("unchecked")
	public SavedSearch getSavedSearch(long searchId) {
		return savedSearchExtractor.extractObject(restTemplate.getForObject(SAVED_SEARCH_URL, Map.class, searchId));
	}

	public void createSavedSearch(String query) {
		MultiValueMap<String, String> request = new LinkedMultiValueMap<String, String>();
		request.set("query", query);
		restTemplate.postForObject(CREATE_SAVED_SEARCH_URL, request, String.class);
	}

	public void deleteSavedSearch(long searchId) {
		restTemplate.delete(DELETE_SAVED_SEARCH_URL, searchId);
	}
	
	// Trends

	public Trends getCurrentTrends() {
		return getCurrentTrends(false);
	}
	
	@SuppressWarnings("unchecked")
	public Trends getCurrentTrends(boolean excludeHashtags) {
		String url = makeTrendUrl(CURRENT_TRENDS_URL, excludeHashtags, null);
		Map<String, Object> response = restTemplate.getForObject(url, Map.class);
		List<Trends> trendsList = extractTrendsListFromResponse(response, longTrendDateFormat);
		return trendsList.get(0);
	}

	public List<Trends> getDailyTrends() {
		return getDailyTrends(false, null);
	}

	public List<Trends> getDailyTrends(boolean excludeHashtags) {
		return getDailyTrends(excludeHashtags, null);
	}

	@SuppressWarnings("unchecked")
	public List<Trends> getDailyTrends(boolean excludeHashtags, String startDate) {
		String url = makeTrendUrl(DAILY_TRENDS_URL, excludeHashtags, startDate);
		Map<String, Object> response = restTemplate.getForObject(url, Map.class);
		return extractTrendsListFromResponse(response, longTrendDateFormat);
	}
	
	public List<Trends> getWeeklyTrends() {
		return getWeeklyTrends(false, null);
	}
	
	public List<Trends> getWeeklyTrends(boolean excludeHashtags) {
		return getWeeklyTrends(excludeHashtags, null);
	}
	
	@SuppressWarnings("unchecked")
	public List<Trends> getWeeklyTrends(boolean excludeHashtags, String startDate) {
		String url = makeTrendUrl(WEEKLY_TRENDS_URL, excludeHashtags, startDate);
		Map<String, Object> response = restTemplate.getForObject(url, Map.class);
		return extractTrendsListFromResponse(response, simpleTrendDateFormat);
	}

	public Trends getLocalTrends(long whereOnEarthId) {
		return getLocalTrends(whereOnEarthId, false);
	}

	@SuppressWarnings("unchecked")
	public Trends getLocalTrends(long whereOnEarthId, boolean excludeHashtags) {
		String url = makeTrendUrl(LOCAL_TRENDS_URL, excludeHashtags, null);
		List<Map<String, Object>> response = restTemplate.getForObject(url, List.class, whereOnEarthId);
		List<Map<String, String>> trendMapList = (List<Map<String, String>>) response.get(0).get("trends");
		List<Trend> trendList = new ArrayList<Trend>(trendMapList.size());
		for (Map<String, String> trendMap : trendMapList) {
			trendList.add(new Trend(trendMap.get("name"), trendMap.get("query")));
		}
		String dateString = String.valueOf(response.get(0).get("created_at"));
		return new Trends(toDate(dateString, localTrendDateFormat), trendList);
	}

	private String makeTrendUrl(String baseUrl, boolean excludeHashtags, String startDate) {
		String url = baseUrl + (excludeHashtags || startDate != null ? "?" : "");
		url += excludeHashtags ? "exclude=hashtags" : "";
		url += excludeHashtags && startDate != null ? "&" : "";
		url += startDate != null ? "date=" + startDate : "";
		return url;
	}

	@SuppressWarnings("unchecked")
	private List<Trends> extractTrendsListFromResponse(Map<String, Object> response, DateFormat dateFormat) {
		Map<String, Object> trendsMap = (Map<String, Object>) response.get("trends");
		List<Trends> trendsList = new ArrayList<Trends>(trendsMap.keySet().size());
		for (String trendDate : trendsMap.keySet()) {
			List<Map<String, String>> trendsMapList = (List<Map<String, String>>) trendsMap.get(trendDate);
			List<Trend> trendList = new ArrayList<Trend>(trendsMapList.size());
			for (Map<String, String> trendMap : trendsMapList) {
				trendList.add(new Trend(trendMap.get("name"), trendMap.get("query")));
			}
			trendsList.add(new Trends(toDate(trendDate, dateFormat), trendList));
		}
		Collections.sort(trendsList, new Comparator<Trends>() {
			public int compare(Trends t1, Trends t2) {
				return t1.getTime().getTime() > t2.getTime().getTime() ? -1 : 1;
			}
		});
		return trendsList;
	}

	private SearchResults buildSearchResults(Map<String, Object> response, List<Tweet> tweets) {
		Number maxId = response.containsKey("max_id") ? (Number) response.get("max_id") : 0;
		Number sinceId = response.containsKey("since_id") ? (Number) response.get("since_id") : 0;
		return new SearchResults(tweets, maxId.longValue(), sinceId.longValue(), response.get("next_page") == null);
	}

	private Tweet populateTweetFromSearchResults(Map<String, Object> item) {
		Tweet tweet = new Tweet();
		tweet.setId(NumberUtils.parseNumber(ObjectUtils.nullSafeToString(item.get("id")), Long.class));
		tweet.setFromUser(ObjectUtils.nullSafeToString(item.get("from_user")));
		tweet.setText(ObjectUtils.nullSafeToString(item.get("text")));
		tweet.setCreatedAt(toDate(ObjectUtils.nullSafeToString(item.get("created_at")), searchDateFormat));
		tweet.setFromUserId(NumberUtils.parseNumber(ObjectUtils.nullSafeToString(item.get("from_user_id")), Long.class));
		Object toUserId = item.get("to_user_id");
		if (toUserId != null) {
			tweet.setToUserId(NumberUtils.parseNumber(ObjectUtils.nullSafeToString(toUserId), Long.class));
		}
		tweet.setLanguageCode(ObjectUtils.nullSafeToString(item.get("iso_language_code")));
		tweet.setProfileImageUrl(ObjectUtils.nullSafeToString(item.get("profile_image_url")));
		tweet.setSource(ObjectUtils.nullSafeToString(item.get("source")));
		return tweet;
	}

	private static final DateFormat searchDateFormat = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z", Locale.ENGLISH);
	private static final DateFormat simpleTrendDateFormat = new SimpleDateFormat("yyyy-MM-dd");
	private static final DateFormat longTrendDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private static final DateFormat localTrendDateFormat = new SimpleDateFormat("yyyy-mm-dd'T'HH:mm:ss'Z'");

	// 2011-03-18T16:45:33Z

	private static Date toDate(String dateString, DateFormat dateFormat) {
		try {
			return dateFormat.parse(dateString);
		} catch (ParseException e) {
			return null;
		}
	}

	static final int DEFAULT_RESULTS_PER_PAGE = 50;

	static final String SEARCH_API_URL_BASE = "https://search.twitter.com";
	static final String SEARCH_URL = SEARCH_API_URL_BASE + "/search.json?q={query}&rpp={rpp}&page={page}";
	static final String SAVED_SEARCHES_URL = TwitterTemplate.API_URL_BASE + "saved_searches.json";
	static final String SAVED_SEARCH_URL = TwitterTemplate.API_URL_BASE + "saved_searches/show/{searchId}.json";
	static final String CREATE_SAVED_SEARCH_URL = TwitterTemplate.API_URL_BASE + "saved_searches/create.json";
	static final String DELETE_SAVED_SEARCH_URL = TwitterTemplate.API_URL_BASE + "saved_searches/destroy/{searchId}.json";
	static final String CURRENT_TRENDS_URL = TwitterTemplate.API_URL_BASE + "trends/current.json";
	static final String DAILY_TRENDS_URL = TwitterTemplate.API_URL_BASE + "trends/daily.json";
	static final String WEEKLY_TRENDS_URL = TwitterTemplate.API_URL_BASE + "trends/weekly.json";
	static final String LOCAL_TRENDS_URL = TwitterTemplate.API_URL_BASE + "trends/{whereOnEarth_id}.json";
}
