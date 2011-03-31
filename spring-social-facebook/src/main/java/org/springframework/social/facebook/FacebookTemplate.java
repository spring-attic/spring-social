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

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJacksonHttpMessageConverter;
import org.springframework.social.facebook.support.CheckinApiImpl;
import org.springframework.social.facebook.support.CommentApiImpl;
import org.springframework.social.facebook.support.EventsApiImpl;
import org.springframework.social.facebook.support.FeedApiImpl;
import org.springframework.social.facebook.support.FriendsApiImpl;
import org.springframework.social.facebook.support.GroupApiImpl;
import org.springframework.social.facebook.support.LikeApiImpl;
import org.springframework.social.facebook.support.MediaApiImpl;
import org.springframework.social.facebook.support.UserApiImpl;
import org.springframework.social.facebook.support.extractors.ResponseExtractor;
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
	
	private CheckinApi checkinApi;

	private FriendsApi friendsApi;
	
	private FeedApi feedApi;
	
	private GroupApi groupApi;

	private CommentApi commentApi;

	private LikeApi likeApi;
	
	private EventsApi eventsApi;
	
	private MediaApi mediaApi;

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

		// sub-apis
		userApi = new UserApiImpl(this);
		checkinApi = new CheckinApiImpl(this);
		friendsApi = new FriendsApiImpl(this, restTemplate);
		feedApi = new FeedApiImpl(this);
		commentApi = new CommentApiImpl(this);
		likeApi = new LikeApiImpl(this);
		eventsApi = new EventsApiImpl(this);
		mediaApi = new MediaApiImpl(this);
		groupApi = new GroupApiImpl(this);
	}

	public UserApi userApi() {
		return userApi;
	}
	
	public CheckinApi checkinApi() {
		return checkinApi;
	}

	public LikeApi likeApi() {
		return likeApi;
	}

	public FriendsApi friendsApi() {
		return friendsApi;
	}
	
	public FeedApi feedApi() {
		return feedApi;
	}
	
	public GroupApi groupApi() {
		return groupApi;
	}

	public CommentApi commentApi() {
		return commentApi;
	}
	
	public EventsApi eventsApi() {
		return eventsApi;
	}
	
	public MediaApi mediaApi() {
		return mediaApi;
	}
	
	// low-level Graph API operations
	@SuppressWarnings("unchecked")
	public <T> T fetchObject(String objectId, ResponseExtractor<T> extractor) {
		return extractor.extractObject( (Map<String, Object>) restTemplate.getForObject(OBJECT_URL, Map.class, objectId));
	}
	
	@SuppressWarnings("unchecked")
	public <T> List<T> fetchConnections(String objectId, String connectionType, ResponseExtractor<T> extractor) {
		Map<String, Object> response = restTemplate.getForObject(CONNECTION_URL, Map.class, objectId, connectionType);
		return extractor.extractObjects((List<Map<String, Object>>) response.get("data"));
	}
	
	@SuppressWarnings("unchecked")
	public Map<String, Object> publish(String objectId, String connectionType, MultiValueMap<String, String> data) {
		MultiValueMap<String, String> requestData = new LinkedMultiValueMap<String, String>(data);
		return restTemplate.postForObject(CONNECTION_URL, requestData, Map.class, objectId, connectionType);
	}
	
	public void post(String objectId, String connectionType, MultiValueMap<String, String> data) {
		MultiValueMap<String, String> requestData = new LinkedMultiValueMap<String, String>(data);
		restTemplate.postForObject(CONNECTION_URL, requestData, String.class, objectId, connectionType);
	}
	
	public void delete(String objectId) {
		LinkedMultiValueMap<String, String> deleteRequest = new LinkedMultiValueMap<String, String>();
		deleteRequest.set("method", "delete");
		restTemplate.postForObject(OBJECT_URL, deleteRequest, String.class, objectId);
	}
	
	public void delete(String objectId, String connectionType) {
		LinkedMultiValueMap<String, String> deleteRequest = new LinkedMultiValueMap<String, String>();
		deleteRequest.set("method", "delete");
		restTemplate.postForObject(CONNECTION_URL, deleteRequest, String.class, objectId, connectionType);
	}


	// subclassing hooks
	
	protected RestTemplate getRestTemplate() {
		return restTemplate;
	}

}
