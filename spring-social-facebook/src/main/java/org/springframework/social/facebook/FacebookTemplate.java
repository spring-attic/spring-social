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
import java.util.Set;

import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJacksonHttpMessageConverter;
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

	private UserOperations userOperations;
	
	private CheckinOperations checkinOperations;

	private FriendOperations friendOperations;
	
	private FeedOperations feedOperations;
	
	private GroupOperations groupOperations;

	private CommentOperations commentOperations;

	private LikeOperations likeOperations;
	
	private EventOperations eventOperations;
	
	private MediaOperations mediaOperations;

	private FacebookErrorHandler errorHandler;

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
		errorHandler = new FacebookErrorHandler();
		restTemplate.setErrorHandler(errorHandler);

		// sub-apis
		userOperations = new UserTemplate(this);
		checkinOperations = new CheckinTemplate(this);
		friendOperations = new FriendTemplate(this, restTemplate);
		feedOperations = new FeedTemplate(this);
		commentOperations = new CommentTemplate(this);
		likeOperations = new LikeTemplate(this);
		eventOperations = new EventTemplate(this);
		mediaOperations = new MediaTemplate(this);
		groupOperations = new GroupTemplate(this);
	}

	public UserOperations userOperations() {
		return userOperations;
	}
	
	public CheckinOperations checkinOperations() {
		return checkinOperations;
	}

	public LikeOperations likeOperations() {
		return likeOperations;
	}

	public FriendOperations friendOperations() {
		return friendOperations;
	}
	
	public FeedOperations feedOperations() {
		return feedOperations;
	}
	
	public GroupOperations groupOperations() {
		return groupOperations;
	}

	public CommentOperations commentOperations() {
		return commentOperations;
	}
	
	public EventOperations eventOperations() {
		return eventOperations;
	}
	
	public MediaOperations mediaOperations() {
		return mediaOperations;
	}
	
	// low-level Graph API operations
	@SuppressWarnings("unchecked")
	public <T> T fetchObject(String objectId, ResponseExtractor<T> extractor) {
		Map<String, Object> response = (Map<String, Object>) restTemplate.getForObject(OBJECT_URL, Map.class, objectId);
		checkForErrors(response);
		return extractor.extractObject(response);
	}
	
	@SuppressWarnings("unchecked")
	public <T> T fetchObject(String objectId, ResponseExtractor<T> extractor, String... fields) {
		String joinedFields = join(fields);
		Map<String, Object> response = (Map<String, Object>) restTemplate.getForObject(OBJECT_URL + "?fields={fields}", Map.class, objectId, joinedFields);
		checkForErrors(response);
		return extractor.extractObject( response);
	}
	
	@SuppressWarnings("unchecked")
	public <T> List<T> fetchObject(ResponseExtractor<T> extractor, String... objectIds) {
		String joinedIds = join(objectIds);
		Map<String, Object> response = restTemplate.getForObject(GRAPH_API_URL + "?ids={ids}", Map.class, joinedIds);		
		checkForErrors(response);
		Set<String> keys = response.keySet();
		List<T> objects = new ArrayList<T>(keys.size());
		for (String key : keys) {
			Map<String, Object> objectMap = (Map<String, Object>) response.get(key);
			objects.add(extractor.extractObject(objectMap));
		}
		return objects;
	}
	
	@SuppressWarnings("unchecked")
	public <T> List<T> fetchConnections(String objectId, String connectionType, ResponseExtractor<T> extractor) {
		Map<String, Object> response = restTemplate.getForObject(CONNECTION_URL, Map.class, objectId, connectionType);
		checkForErrors(response);
		return extractor.extractObjects((List<Map<String, Object>>) response.get("data"));
	}
	
	@SuppressWarnings("unchecked")
	public <T> List<T> fetchConnections(String objectId, String connectionType, ResponseExtractor<T> extractor, String... fields) {
		String joinedFields = join(fields);
		Map<String, Object> response = restTemplate.getForObject(CONNECTION_URL + "?fields={fields}", Map.class, objectId, connectionType, joinedFields);
		checkForErrors(response);
		return extractor.extractObjects((List<Map<String, Object>>) response.get("data"));
	}
	
	@SuppressWarnings("unchecked")
	public String publish(String objectId, String connectionType, MultiValueMap<String, String> data) {
		MultiValueMap<String, String> requestData = new LinkedMultiValueMap<String, String>(data);
		Map<String, Object> response = restTemplate.postForObject(CONNECTION_URL, requestData, Map.class, objectId, connectionType);
		checkForErrors(response);
		return (String) response.get("id");
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

	/*
	 * Facebook sometimes returns an error message with an HTTP 200. The HTTP 200 prevents the error handler
	 * from handling it, so we need to check all responses for errors before assuming that they're good data. 
	 */
	@SuppressWarnings("unchecked")
	private void checkForErrors(Map<String, Object> response) {
		if(response.containsKey("error")) {
			Map<String, String> errorDetails = (Map<String, String>) response.get("error");
			errorHandler.handleFacebookError(errorDetails);
		}
	}
	// subclassing hooks
	
	protected RestTemplate getRestTemplate() {
		return restTemplate;
	}

	private String join(String[] strings) {
		StringBuilder builder = new StringBuilder();
		if(strings.length > 0) {
			builder.append(strings[0]);
			for (String string : strings) {
				builder.append("," + string);
			}
		}
		return builder.toString();
	}
	
}
