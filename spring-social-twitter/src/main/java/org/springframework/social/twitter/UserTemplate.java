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

import java.util.Collections;
import java.util.List;

import org.springframework.social.twitter.support.extractors.SuggestionCategoryResponseExtractor;
import org.springframework.social.twitter.support.extractors.TwitterProfileResponseExtractor;
import org.springframework.social.twitter.types.SuggestionCategory;
import org.springframework.social.twitter.types.TwitterProfile;

/**
 * Implementation of the {@link UserOperations} interface providing binding to Twitters' user-oriented REST resources.
 * @author Craig Walls
 */
class UserTemplate extends AbstractTwitterOperations implements UserOperations {
	
	private final TwitterProfileResponseExtractor profileExtractor;

	private final SuggestionCategoryResponseExtractor suggestionCategoryExtractor;

	public UserTemplate(LowLevelTwitterApi lowLevelApi) {
		super(lowLevelApi);
		this.profileExtractor = new TwitterProfileResponseExtractor();
		this.suggestionCategoryExtractor = new SuggestionCategoryResponseExtractor();
	}

	public long getProfileId() {
		requireUserAuthorization();
		return getLowLevelTwitterApi().fetchObject("account/verify_credentials.json", profileExtractor).getId();
	}

	public String getScreenName() {
		requireUserAuthorization();
		return getLowLevelTwitterApi().fetchObject("account/verify_credentials.json", profileExtractor).getScreenName();
	}

	public TwitterProfile getUserProfile() {
		requireUserAuthorization();
		return getLowLevelTwitterApi().fetchObject("account/verify_credentials.json", profileExtractor);
	}

	public TwitterProfile getUserProfile(String screenName) {
		return getLowLevelTwitterApi().fetchObject("users/show.json", profileExtractor, Collections.singletonMap("screen_name", screenName));
	}
	
	public TwitterProfile getUserProfile(long userId) {
		return getLowLevelTwitterApi().fetchObject("users/show.json", profileExtractor, Collections.singletonMap("user_id", String.valueOf(userId)));
	}

	public List<TwitterProfile> getUsers(long... userIds) {
		requireUserAuthorization();
		String joinedIds = ArrayUtils.join(userIds);
		return getLowLevelTwitterApi().fetchObjects("users/lookup.json", profileExtractor, Collections.singletonMap("user_id", joinedIds) );
	}

	public List<TwitterProfile> getUsers(String... screenNames) {
		requireUserAuthorization();
		String joinedScreenNames = ArrayUtils.join(screenNames);
		return getLowLevelTwitterApi().fetchObjects("users/lookup.json", profileExtractor, Collections.singletonMap("screen_name", joinedScreenNames));
	}

	public List<TwitterProfile> searchForUsers(String query) {
		requireUserAuthorization();
		return getLowLevelTwitterApi().fetchObjects("users/search.json", profileExtractor, Collections.singletonMap("q", query));
	}
	
	public List<SuggestionCategory> getSuggestionCategories() {
		return getLowLevelTwitterApi().fetchObjects("users/suggestions.json", suggestionCategoryExtractor);
	}

	public List<TwitterProfile> getSuggestions(String slug) {
		return getLowLevelTwitterApi().fetchObjects("users/suggestions/" + slug + ".json", "users", profileExtractor);
	}

	static final String SUGGESTIONS_URL = TwitterTemplate.API_URL_BASE + "users/suggestions/{slug}.json";
}
