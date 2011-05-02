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

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.social.twitter.api.ImageSize;
import org.springframework.social.twitter.api.SuggestionCategory;
import org.springframework.social.twitter.api.TwitterProfile;
import org.springframework.social.twitter.api.UserOperations;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestTemplate;

/**
 * Implementation of the {@link UserOperations} interface providing binding to Twitters' user-oriented REST resources.
 * @author Craig Walls
 */
class UserTemplate extends AbstractTwitterOperations implements UserOperations {
	
	private final RestTemplate restTemplate;

	public UserTemplate(RestTemplate restTemplate, boolean isAuthorizedForUser) {
		super(isAuthorizedForUser);
		this.restTemplate = restTemplate;
	}

	public long getProfileId() {
		requireUserAuthorization();
		return getUserProfile().getId();
	}

	public String getScreenName() {
		requireUserAuthorization();
		return getUserProfile().getScreenName();
	}

	public TwitterProfile getUserProfile() {
		requireUserAuthorization();
		return restTemplate.getForObject(buildUri("account/verify_credentials.json"), TwitterProfile.class);
	}

	public TwitterProfile getUserProfile(String screenName) {
		return restTemplate.getForObject(buildUri("users/show.json", "screen_name", screenName), TwitterProfile.class);
	}
	
	public TwitterProfile getUserProfile(long userId) {
		return restTemplate.getForObject(buildUri("users/show.json", "user_id", String.valueOf(userId)), TwitterProfile.class);
	}
	
	public byte[] getUserProfileImage(String screenName) {
		return getUserProfileImage(screenName, ImageSize.NORMAL);
	}
	
	public byte[] getUserProfileImage(String screenName, ImageSize size) {
		LinkedMultiValueMap<String, String> parameters = new LinkedMultiValueMap<String, String>();
		parameters.set("screen_name", screenName);
		parameters.set("size", size.toString().toLowerCase());
		ResponseEntity<byte[]> response = restTemplate.getForEntity(buildUri("users/profile_image", parameters), byte[].class);
		if(response.getStatusCode() == HttpStatus.FOUND) {
			throw new UnsupportedOperationException("Attempt to fetch image resulted in a redirect which could not be followed. Add Apache HttpComponents HttpClient to the classpath " +
					"to be able to follow redirects.");
		}
		return response.getBody();
	}

	public List<TwitterProfile> getUsers(long... userIds) {
		requireUserAuthorization();
		String joinedIds = ArrayUtils.join(userIds);
		return restTemplate.getForObject(buildUri("users/lookup.json", "user_id", joinedIds), TwitterProfileList.class);
	}

	public List<TwitterProfile> getUsers(String... screenNames) {
		requireUserAuthorization();
		String joinedScreenNames = ArrayUtils.join(screenNames);
		return restTemplate.getForObject(buildUri("users/lookup.json", "screen_name", joinedScreenNames), TwitterProfileList.class);
	}

	public List<TwitterProfile> searchForUsers(String query) {
		requireUserAuthorization();
		return restTemplate.getForObject(buildUri("users/search.json", "q", query), TwitterProfileList.class);
	}
	
	public List<SuggestionCategory> getSuggestionCategories() {
		return restTemplate.getForObject(buildUri("users/suggestions.json"), SuggestionCategoryList.class);
	}

	public List<TwitterProfile> getSuggestions(String slug) {
		return restTemplate.getForObject(buildUri("users/suggestions/" + slug + ".json"), TwitterProfileUsersList.class).getList();
	}

}
