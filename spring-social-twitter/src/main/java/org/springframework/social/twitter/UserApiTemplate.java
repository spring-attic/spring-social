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

	private final TwitterRequestApi requestApi;

	private final RestTemplate restTemplate;
	
	private final TwitterProfileResponseExtractor profileExtractor;

	private final SuggestionCategoryResponseExtractor suggestionCategoryExtractor;

	public UserApiTemplate(TwitterRequestApi requestApi, RestTemplate restTemplate) {
		this.requestApi = requestApi;
		this.restTemplate = restTemplate;
		this.profileExtractor = new TwitterProfileResponseExtractor();
		this.suggestionCategoryExtractor = new SuggestionCategoryResponseExtractor();
	}

	public long getProfileId() {
		return requestApi.fetchObject("account/verify_credentials.json", profileExtractor).getId();
	}

	public String getScreenName() {
		return requestApi.fetchObject("account/verify_credentials.json", profileExtractor).getScreenName();
	}

	public TwitterProfile getUserProfile() {
		return requestApi.fetchObject("account/verify_credentials.json", profileExtractor);
	}

	@SuppressWarnings("unchecked")
	public TwitterProfile getUserProfile(String screenName) {
		return requestApi.fetchObject("users/show.json?screen_name={screenName}", profileExtractor);
	}
	
	@SuppressWarnings("unchecked")
	public TwitterProfile getUserProfile(long userId) {
		return requestApi.fetchObject("users/show.json?user_id={userId}", profileExtractor, userId);
	}

	public List<TwitterProfile> getUsers(long... userIds) {
		return requestApi.fetchObjects("users/lookup.json?user_id={userId}", profileExtractor, ArrayUtils.join(userIds));
	}

	public List<TwitterProfile> getUsers(String... screenNames) {
		return requestApi.fetchObjects("users/lookup.json?screen_name={screenName}", profileExtractor, ArrayUtils.join(screenNames));
	}

	// TODO Remove lookupUsers...user fetch
	public List<TwitterProfile> searchForUsers(String query) {
		return requestApi.fetchObjects("users/search.json?q={query}", profileExtractor, query);
	}
	
	@SuppressWarnings("unchecked")
	public List<SuggestionCategory> getSuggestionCategories() {
		return requestApi.fetchObjects("users/suggestions.json", suggestionCategoryExtractor);
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

	static final String SUGGESTIONS_URL = TwitterTemplate.API_URL_BASE + "users/suggestions/{slug}.json";
}
