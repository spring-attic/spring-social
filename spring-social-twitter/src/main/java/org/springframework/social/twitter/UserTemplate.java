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

import org.springframework.social.twitter.support.extractors.SuggestionCategoryResponseExtractor;
import org.springframework.social.twitter.support.extractors.TwitterProfileResponseExtractor;
import org.springframework.social.twitter.types.SuggestionCategory;
import org.springframework.social.twitter.types.TwitterProfile;

/**
 * Implementation of the {@link UserOperations} interface providing binding to Twitters' user-oriented REST resources.
 * @author Craig Walls
 */
class UserTemplate implements UserOperations {

	private final LowLevelTwitterApi lowLevelApi;
	
	private final TwitterProfileResponseExtractor profileExtractor;

	private final SuggestionCategoryResponseExtractor suggestionCategoryExtractor;

	public UserTemplate(LowLevelTwitterApi lowLevelApi) {
		this.lowLevelApi = lowLevelApi;
		this.profileExtractor = new TwitterProfileResponseExtractor();
		this.suggestionCategoryExtractor = new SuggestionCategoryResponseExtractor();
	}

	public long getProfileId() {
		return lowLevelApi.fetchObject("account/verify_credentials.json", profileExtractor).getId();
	}

	public String getScreenName() {
		return lowLevelApi.fetchObject("account/verify_credentials.json", profileExtractor).getScreenName();
	}

	public TwitterProfile getUserProfile() {
		return lowLevelApi.fetchObject("account/verify_credentials.json", profileExtractor);
	}

	public TwitterProfile getUserProfile(String screenName) {
		return lowLevelApi.fetchObject("users/show.json?screen_name={screenName}", profileExtractor, screenName);
	}
	
	public TwitterProfile getUserProfile(long userId) {
		return lowLevelApi.fetchObject("users/show.json?user_id={userId}", profileExtractor, userId);
	}

	public List<TwitterProfile> getUsers(long... userIds) {
		return lowLevelApi.fetchObjects("users/lookup.json?user_id={userId}", profileExtractor, ArrayUtils.join(userIds));
	}

	public List<TwitterProfile> getUsers(String... screenNames) {
		return lowLevelApi.fetchObjects("users/lookup.json?screen_name={screenName}", profileExtractor, ArrayUtils.join(screenNames));
	}

	public List<TwitterProfile> searchForUsers(String query) {
		return lowLevelApi.fetchObjects("users/search.json?q={query}", profileExtractor, query);
	}
	
	public List<SuggestionCategory> getSuggestionCategories() {
		return lowLevelApi.fetchObjects("users/suggestions.json", suggestionCategoryExtractor);
	}

	public List<TwitterProfile> getSuggestions(String slug) {
		return lowLevelApi.fetchObjects("users/suggestions/{slug}.json", "users", profileExtractor, slug);
	}

	static final String SUGGESTIONS_URL = TwitterTemplate.API_URL_BASE + "users/suggestions/{slug}.json";
}
