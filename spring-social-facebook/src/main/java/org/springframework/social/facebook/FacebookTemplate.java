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
package org.springframework.social.facebook;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.CommonsClientHttpRequestFactory;
import org.springframework.http.converter.json.MappingJacksonHttpMessageConverter;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;

/**
 * This is the central class for interacting with Facebook.
 * 
 * <p>
 * All operations through Facebook require OAuth 2-based authentication.
 * Therefore, FacebookTemplate must be given an access token at construction
 * time.
 * </p>
 * 
 * <p>
 * The easiest way to get an access token is to use the XFBML
 * &lt;fb:login-button&gt; tag to require the user to signin to Facebook. Then,
 * after a successful signin, the access token can be found in the cookie whose
 * name is "fbs_{application key}". In Spring MVC, the
 * {@link FacebookWebArgumentResolver} can extract the access token from the
 * cookie and make it available as a String argument to the controller method.
 * </p>
 * 
 * @author Craig Walls
 * @see FacebookWebArgumentResolver
 */
public class FacebookTemplate implements FacebookOperations {
	RestOperations restOperations;
	private final String accessToken;

	/**
	 * Create a new instance of FacebookTemplate.
	 * 
	 * This constructor creates the FacebookTemplate using a given access token.
	 * 
	 * @param accessToken
	 *            An access token given by Facebook after a successful OAuth 2
	 *            authentication (or through Facebook's JS library).
	 */
	public FacebookTemplate(String accessToken) {
		this.accessToken = accessToken;
		RestTemplate restTemplate = new RestTemplate();
		// must be CommonsClientHttpRequestFactory or else the location header
		// in an HTTP 302 won't be followed
		restTemplate.setRequestFactory(new CommonsClientHttpRequestFactory());
		MappingJacksonHttpMessageConverter json = new MappingJacksonHttpMessageConverter();
		json.setSupportedMediaTypes(Arrays.asList(new MediaType("text", "javascript")));
		restTemplate.getMessageConverters().add(json);
		this.restOperations = restTemplate;
	}

	public String getProfileId() {
		return Long.toString(getUserProfile().getId());
	}

	public String getProfileUrl() {
		return "http://www.facebook.com/profile.php?id=" + getProfileId();
	}

	public FacebookProfile getUserProfile() {
		return getUserProfile(CURRENT_USER);
	}

	public FacebookProfile getUserProfile(String facebookId) {
		return restOperations.getForObject(OBJECT_URL + "?access_token={accessToken}", FacebookProfile.class,
				facebookId, accessToken);
    }

	public List<String> getFriendIds() {
		@SuppressWarnings("rawtypes")
		ResponseEntity<Map> response = restOperations.getForEntity(CONNECTION_URL, Map.class, CURRENT_USER, FRIENDS,
				accessToken);

		@SuppressWarnings("unchecked")
		Map<String, List<Map<String, String>>> resultsMap = response.getBody();
		List<Map<String, String>> friends = resultsMap.get("data");
		
		List<String> friendIds = new ArrayList<String>();
		for (Map<String, String> friendData : friends) {
	        friendIds.add(friendData.get("id"));
        }
	    return friendIds;
    }
	
	public void updateStatus(String message) {
		MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
		map.set("message", message);
		publish(CURRENT_USER, FEED, map);
	}
	
	public void updateStatus(String message, FacebookLink link) {
		MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
		map.set("link", link.getLink());
		map.set("name", link.getName());
		map.set("caption", link.getCaption());
		map.set("description", link.getDescription());
		map.set("message", message);
		publish(CURRENT_USER, FEED, map);
	}
	
	public void publish(String object, String connection, MultiValueMap<String, String> data) {
		MultiValueMap<String, String> requestData = new LinkedMultiValueMap<String, String>(data);
		restOperations.postForLocation(CONNECTION_URL, requestData, object, connection, accessToken);
	}
	
	public byte[] getProfilePicture() {
		return getProfilePicture(CURRENT_USER);
	}

	public byte[] getProfilePicture(String profileId) {
		ResponseEntity<byte[]> imageBytes = restOperations.getForEntity(PROFILE_LARGE_PICTURE_URL, byte[].class,
				profileId, accessToken);
		return imageBytes.getBody();
	}
	
	static final String PROFILE_LARGE_PICTURE_URL = "https://graph.facebook.com/{profile}/picture?type=large&access_token={accessToken}";
	static final String OBJECT_URL = "https://graph.facebook.com/{objectId}";
	static final String CONNECTION_URL = OBJECT_URL + "/{connection}?access_token={accessToken}";
	
	static final String FRIENDS = "friends";
	static final String FEED = "feed";
	static final String CURRENT_USER = "me";
}
