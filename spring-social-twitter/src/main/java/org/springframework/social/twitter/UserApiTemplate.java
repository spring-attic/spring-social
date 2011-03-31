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
import java.util.Map;

import org.springframework.social.twitter.support.extractors.SuggestionCategoryResponseExtractor;
import org.springframework.social.twitter.support.extractors.TwitterProfileResponseExtractor;
import org.springframework.social.twitter.types.SuggestionCategory;
import org.springframework.social.twitter.types.TwitterProfile;
import org.springframework.web.client.RestTemplate;

/**
 * Implementation of the {@link UserApi} interface providing binding to Twitters' user-oriented REST resources.
 * @author Craig Walls
 */
public class UserApiTemplate implements UserApi {

	private final RestTemplate restTemplate;
	
	private TwitterProfileResponseExtractor profileExtractor;

	private SuggestionCategoryResponseExtractor suggestionCategoryExtractor;

	public UserApiTemplate(RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
		this.profileExtractor = new TwitterProfileResponseExtractor();
		this.suggestionCategoryExtractor = new SuggestionCategoryResponseExtractor();
	}

	public long getProfileId() {
		Map<?, ?> response = restTemplate.getForObject(VERIFY_CREDENTIALS_URL, Map.class);
		return Long.valueOf(String.valueOf(response.get("id")));
	}

	public String getScreenName() {
		Map<?, ?> response = restTemplate.getForObject(VERIFY_CREDENTIALS_URL, Map.class);
		return (String) response.get("screen_name");
	}

	public TwitterProfile getUserProfile() {
		return getUserProfile(getProfileId());
	}

	@SuppressWarnings("unchecked")
	public TwitterProfile getUserProfile(String screenName) {
		Map<String, Object> response = restTemplate.getForObject(USER_PROFILE_URL + "?screen_name={screenName}", Map.class, screenName);
		return profileExtractor.extractObject(response);
	}

	@SuppressWarnings("unchecked")
	public TwitterProfile getUserProfile(long userId) {
		Map<String, Object> response = restTemplate.getForObject(USER_PROFILE_URL + "?user_id={userId}", Map.class, userId);
		return profileExtractor.extractObject(response);
	}

	public List<TwitterProfile> getUsers(long... userIds) {
		return lookupUsers(USER_LOOKUP_URL + "?user_id={user_id}", ArrayUtils.join(userIds));
	}

	public List<TwitterProfile> getUsers(String... screenNames) {
		return lookupUsers(USER_LOOKUP_URL + "?screen_name={screen_name}", ArrayUtils.join(screenNames));
	}

	public List<TwitterProfile> searchForUsers(String query) {
		return lookupUsers(USER_SEARCH_URL + "?q={query}", query);
	}
	
	@SuppressWarnings("unchecked")
	public List<SuggestionCategory> getSuggestionCategories() {
		return suggestionCategoryExtractor.extractObjects((List<Map<String, Object>>) restTemplate.getForObject(SUGGESTION_CATEGORIES_URL, List.class));
	}

	@SuppressWarnings("unchecked")
	public List<TwitterProfile> getSuggestions(String slug) {
		Map<String, List<Map<String, Object>>> suggestionsMap = restTemplate.getForObject(SUGGESTIONS_URL, Map.class, slug);
		return profileExtractor.extractObjects(suggestionsMap.get("users"));
	}

	@SuppressWarnings("unchecked")
	private List<TwitterProfile> lookupUsers(String url, String... urlArgs) {
		return profileExtractor.extractObjects((List<Map<String, Object>>) restTemplate.getForObject(url, List.class, (Object[]) urlArgs));
	}

	static final String VERIFY_CREDENTIALS_URL = TwitterTemplate.API_URL_BASE + "account/verify_credentials.json";
	static final String USER_PROFILE_URL = TwitterTemplate.API_URL_BASE + "users/show.json";
	static final String USER_LOOKUP_URL = TwitterTemplate.API_URL_BASE + "users/lookup.json";
	static final String USER_SEARCH_URL = TwitterTemplate.API_URL_BASE + "users/search.json";
	static final String SUGGESTION_CATEGORIES_URL = TwitterTemplate.API_URL_BASE + "users/suggestions.json";
	static final String SUGGESTIONS_URL = TwitterTemplate.API_URL_BASE + "users/suggestions/{slug}.json";
}
