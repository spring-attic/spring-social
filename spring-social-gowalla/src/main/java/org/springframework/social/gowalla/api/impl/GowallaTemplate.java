/*
 * Copyright 2010 the original author or authors.
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
package org.springframework.social.gowalla.api.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.social.gowalla.api.Checkin;
import org.springframework.social.gowalla.api.GowallaApi;
import org.springframework.social.gowalla.api.GowallaProfile;
import org.springframework.social.oauth2.AbstractOAuth2ApiTemplate;
import org.springframework.social.oauth2.OAuth2Version;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

/**
 * The central class for interacting with the Gowalla API.
 * @author Craig Walls
 */
public class GowallaTemplate extends AbstractOAuth2ApiTemplate implements GowallaApi {

	/**
	 * Constructs a GowallaTemplate with the minimal amount of information
	 * required to sign requests with an OAuth <code>Authorization</code> header.
	 * @param accessToken An access token granted to the application after OAuth authentication.
	 */
	public GowallaTemplate(String accessToken) {
		super(accessToken);
	}
	
	@Override
	protected OAuth2Version getOAuth2Version() {
		return OAuth2Version.DRAFT_8;
	}
	
	public String getProfileId() {
		return getUserProfile().getId();
	}

	public GowallaProfile getUserProfile() {
		return getUserProfile("me");
	}

	public GowallaProfile getUserProfile(String userId) {
		HttpEntity<?> requestEntity = new HttpEntity<String>(buildBaseHeaders());
		@SuppressWarnings("rawtypes")
		ResponseEntity<Map> response = getRestTemplate().exchange(PROFILE_URL, HttpMethod.GET, requestEntity, Map.class, userId);
		@SuppressWarnings("unchecked")
		Map<String, ?> profileInfo = response.getBody();
		int pinsCount = Integer.valueOf(String.valueOf(profileInfo.get("pins_count")));
		int stampsCount = Integer.valueOf(String.valueOf(profileInfo.get("stamps_count")));
		GowallaProfile profile = new GowallaProfile(String.valueOf(profileInfo.get("username")),
				String.valueOf(profileInfo.get("first_name")),
				String.valueOf(profileInfo.get("last_name")), String.valueOf(profileInfo.get("hometown")), pinsCount,
				stampsCount,
				(String) profileInfo.get("large_image_url"));
		return profile;
	}

	public String getProfileUrl() {
		return "http://www.gowalla.com/users/" + getProfileId();
	}
	
	public List<Checkin> getTopCheckins(String userId) {
		HttpEntity<?> requestEntity = new HttpEntity<String>(buildBaseHeaders());
		@SuppressWarnings("rawtypes")
		ResponseEntity<Map> response = getRestTemplate().exchange(TOP_CHECKINS_URL, HttpMethod.GET, requestEntity, Map.class, userId);
		@SuppressWarnings("unchecked")
		List<Map<String, ?>> checkinsList = (List<Map<String, ?>>) response.getBody().get("top_spots");
		List<Checkin> checkins = new ArrayList<Checkin>();
		for (Map<String, ?> checkinMap : checkinsList) {
			checkins.add(new Checkin((String) checkinMap.get("name"), (Integer) checkinMap.get("user_checkins_count")));
		}
		return checkins;
	}

	// internal helpers

	private MultiValueMap<String, String> buildBaseHeaders() {
		MultiValueMap<String, String> headers = new LinkedMultiValueMap<String, String>();
		headers.add("Accept", "application/json");
		return headers;
	}

	static final String PROFILE_URL = "https://api.gowalla.com/users/{userId}";
	static final String TOP_CHECKINS_URL = "https://api.gowalla.com/users/{userId}/top_spots";
}
