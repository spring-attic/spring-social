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
import org.springframework.http.converter.json.MappingJacksonHttpMessageConverter;
import org.springframework.social.facebook.support.CommentApiImpl;
import org.springframework.social.facebook.support.FeedApiImpl;
import org.springframework.social.facebook.support.UserApiImpl;
import org.springframework.social.oauth2.ProtectedResourceClientFactory;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

/**
 * This is the central class for interacting with Facebook.
 * <p>
 * All operations through Facebook require OAuth 2-based authentication.
 * Therefore, FacebookTemplate must be given an access token at construction time.
 * </p>
 * @author Craig Walls
 */
public class FacebookTemplate implements FacebookApi {

	private final RestTemplate restTemplate;

	private UserApi userApi;

	private FeedApi feedApi;

	private CommentApi commentApi;

	/**
	 * Create a new instance of FacebookTemplate.
	 * This constructor creates the FacebookTemplate using a given access token.
	 * @param accessToken An access token given by Facebook after a successful OAuth 2 authentication (or through Facebook's JS library).
	 */
	public FacebookTemplate(String accessToken) {
		this.restTemplate = ProtectedResourceClientFactory.draft10(accessToken);
		// Facebook returns JSON data with text/javascript content type
		MappingJacksonHttpMessageConverter json = new MappingJacksonHttpMessageConverter();
		json.setSupportedMediaTypes(Arrays.asList(new MediaType("text", "javascript")));
		restTemplate.getMessageConverters().add(json);

		restTemplate.setErrorHandler(new FacebookResponseErrorHandler());

		// sub-apis
		userApi = new UserApiImpl(restTemplate);
		feedApi = new FeedApiImpl(restTemplate);
		commentApi = new CommentApiImpl(restTemplate);
	}

	public UserApi userApi() {
		return userApi;
	}

	public FeedApi feedApi() {
		return feedApi;
	}

	public CommentApi commentApi() {
		return commentApi;
	}

	public String getProfileId() {
		return Long.toString(getUserProfile().getId());
	}

    public String getProfileUrl() {
        return "http://www.facebook.com/profile.php?id=" + getProfileId();
    }

	public FacebookProfile getUserProfile() {
		return getUserProfile(CURRENT_USER_ID);
	}

	public FacebookProfile getUserProfile(String facebookId) {
		@SuppressWarnings("unchecked")
		Map<String, ?> profileMap = restTemplate.getForObject(OBJECT_URL, Map.class,
				facebookId);

		long id = Long.valueOf(String.valueOf(profileMap.get("id")));
		String name = String.valueOf(profileMap.get("name"));
		String firstName = String.valueOf(profileMap.get("first_name"));
		String lastName = String.valueOf(profileMap.get("last_name"));
		String email = String.valueOf(profileMap.get("email"));
		return new FacebookProfile(id, name, firstName, lastName, email);
    }

	public List<String> getFriendIds() {
		ResponseEntity<Map> response = restTemplate.getForEntity(CONNECTION_URL, Map.class, CURRENT_USER_ID, FRIENDS);
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
		publish(CURRENT_USER_ID, FEED, map);
	}
	
	public void updateStatus(String message, FacebookLink link) {
		MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
		map.set("link", link.getLink());
		map.set("name", link.getName());
		map.set("caption", link.getCaption());
		map.set("description", link.getDescription());
		map.set("message", message);
		publish(CURRENT_USER_ID, FEED, map);
	}
	
	public void publish(String object, String connection, MultiValueMap<String, String> data) {
		MultiValueMap<String, String> requestData = new LinkedMultiValueMap<String, String>(data);
		restTemplate.postForLocation(CONNECTION_URL, requestData, object, connection);
	}

	// subclassing hooks
	
	protected RestTemplate getRestTemplate() {
		return restTemplate;
	}
	
	static final String OBJECT_URL = "https://graph.facebook.com/{objectId}";
	static final String CONNECTION_URL = OBJECT_URL + "/{connection}";
	
	static final String FRIENDS = "friends";
	static final String FEED = "feed";
	static final String CURRENT_USER_ID = "me";
}
