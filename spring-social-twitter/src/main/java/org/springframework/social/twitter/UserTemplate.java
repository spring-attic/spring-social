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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.social.twitter.support.json.SuggestionCategoryList;
import org.springframework.social.twitter.support.json.TwitterProfileList;
import org.springframework.social.twitter.support.json.TwitterProfileUsersList;
import org.springframework.social.twitter.types.SuggestionCategory;
import org.springframework.social.twitter.types.TwitterProfile;

/**
 * Implementation of the {@link UserOperations} interface providing binding to Twitters' user-oriented REST resources.
 * @author Craig Walls
 */
class UserTemplate extends AbstractTwitterOperations implements UserOperations {
	
	public UserTemplate(LowLevelTwitterApi lowLevelApi) {
		super(lowLevelApi);
	}

	public long getProfileId() {
		requireUserAuthorization();
		return getLowLevelTwitterApi().fetchObject("account/verify_credentials.json", TwitterProfile.class).getId();
	}

	public String getScreenName() {
		requireUserAuthorization();
		return getLowLevelTwitterApi().fetchObject("account/verify_credentials.json", TwitterProfile.class).getScreenName();
	}

	public TwitterProfile getUserProfile() {
		requireUserAuthorization();
		return getLowLevelTwitterApi().fetchObject("account/verify_credentials.json", TwitterProfile.class);
	}

	public TwitterProfile getUserProfile(String screenName) {
		return getLowLevelTwitterApi().fetchObject("users/show.json", TwitterProfile.class, Collections.singletonMap("screen_name", screenName));
	}
	
	public TwitterProfile getUserProfile(long userId) {
		return getLowLevelTwitterApi().fetchObject("users/show.json", TwitterProfile.class, Collections.singletonMap("user_id", String.valueOf(userId)));
	}
	
	public byte[] getUserProfileImage(String screenName) {
		return getUserProfileImage(screenName, ImageSize.NORMAL);
	}
	
	public byte[] getUserProfileImage(String screenName, ImageSize size) {
		Map<String, String> params = new HashMap<String, String>();
		params.put("screen_name", screenName);
		params.put("size", size.toString().toLowerCase());
		return getLowLevelTwitterApi().fetchImage("users/profile_image", params);
	}

	public List<TwitterProfile> getUsers(long... userIds) {
		requireUserAuthorization();
		String joinedIds = ArrayUtils.join(userIds);
		return getLowLevelTwitterApi().fetchObject("users/lookup.json", TwitterProfileList.class, Collections.singletonMap("user_id", joinedIds) ).getList();
	}

	public List<TwitterProfile> getUsers(String... screenNames) {
		requireUserAuthorization();
		String joinedScreenNames = ArrayUtils.join(screenNames);
		return getLowLevelTwitterApi().fetchObject("users/lookup.json", TwitterProfileList.class, Collections.singletonMap("screen_name", joinedScreenNames)).getList();
	}

	public List<TwitterProfile> searchForUsers(String query) {
		requireUserAuthorization();
		return getLowLevelTwitterApi().fetchObject("users/search.json", TwitterProfileList.class, Collections.singletonMap("q", query)).getList();
	}
	
	public List<SuggestionCategory> getSuggestionCategories() {
		return getLowLevelTwitterApi().fetchObject("users/suggestions.json", SuggestionCategoryList.class).getList();
	}

	public List<TwitterProfile> getSuggestions(String slug) {
		return getLowLevelTwitterApi().fetchObject("users/suggestions/" + slug + ".json", TwitterProfileUsersList.class).getList();
	}

}
