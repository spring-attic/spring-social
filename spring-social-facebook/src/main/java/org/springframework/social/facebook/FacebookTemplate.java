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

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJacksonHttpMessageConverter;
import org.springframework.social.facebook.support.extractors.ResponseExtractor;
import org.springframework.social.facebook.support.json.FacebookModule;
import org.springframework.social.oauth2.ProtectedResourceClientFactory;
import org.springframework.social.util.URIBuilder;
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
		registerFacebookModule(restTemplate);
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

	private void registerFacebookModule(RestTemplate restTemplate2) {
		List<HttpMessageConverter<?>> converters = restTemplate.getMessageConverters();
		for (HttpMessageConverter<?> converter : converters) {
			if(converter instanceof MappingJacksonHttpMessageConverter) {
				MappingJacksonHttpMessageConverter jsonConverter = (MappingJacksonHttpMessageConverter) converter;
				ObjectMapper objectMapper = new ObjectMapper();				
				objectMapper.registerModule(new FacebookModule());
				jsonConverter.setObjectMapper(objectMapper);
			}
		}
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
		URI uri = URIBuilder.fromUri(GRAPH_API_URL + objectId).build();
		Map<String, Object> response = (Map<String, Object>) restTemplate.getForObject(uri, Map.class);
		checkForErrors(response);
		return extractor.extractObject(response);
	}
	
	public <T> T fetchObject(String objectId, Class<T> type) {
		URI uri = URIBuilder.fromUri(GRAPH_API_URL + objectId).build();
		return restTemplate.getForObject(uri, type);
	}
	
	@SuppressWarnings("unchecked")
	public <T> T fetchObject(String objectId, ResponseExtractor<T> extractor, String... fields) {
		String joinedFields = join(fields);
		URI uri = URIBuilder.fromUri(GRAPH_API_URL + objectId).queryParam("fields", joinedFields).build();
		Map<String, Object> response = (Map<String, Object>) restTemplate.getForObject(uri, Map.class);
		checkForErrors(response);
		return extractor.extractObject( response);
	}
	
	@SuppressWarnings("unchecked")
	public <T> List<T> fetchObject(ResponseExtractor<T> extractor, String... objectIds) {
		String joinedIds = join(objectIds);
		URI uri = URIBuilder.fromUri(GRAPH_API_URL).queryParam("ids", joinedIds).build();
		Map<String, Object> response = restTemplate.getForObject(uri, Map.class);		
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
		URI uri = URIBuilder.fromUri(GRAPH_API_URL + objectId + "/" + connectionType).build();
		Map<String, Object> response = restTemplate.getForObject(uri, Map.class);
		checkForErrors(response);
		return extractor.extractObjects((List<Map<String, Object>>) response.get("data"));
	}
	
	@SuppressWarnings("unchecked")
	public <T> List<T> fetchConnections(String objectId, String connectionType, ResponseExtractor<T> extractor, String... fields) {
		String joinedFields = join(fields);
		URI uri = URIBuilder.fromUri(GRAPH_API_URL + objectId + "/" + connectionType).queryParam("fields", joinedFields).build();
		Map<String, Object> response = restTemplate.getForObject(uri, Map.class);
		checkForErrors(response);
		return extractor.extractObjects((List<Map<String, Object>>) response.get("data"));
	}
	
	public <T> T fetchConnections(String objectId, String connectionType, Class<T> type, String... fields) {
		URIBuilder uriBuilder = URIBuilder.fromUri(GRAPH_API_URL + objectId + "/" + connectionType);
		if(fields.length > 0) {
			String joinedFields = join(fields);
			uriBuilder.queryParam("fields", joinedFields);
		}		
		return restTemplate.getForObject(uriBuilder.build(), type);
	}
	
	public byte[] fetchImage(String objectId, String connectionType, ImageType type) {
		URI uri = URIBuilder.fromUri(GRAPH_API_URL + objectId + "/" + connectionType + "?type=" + type.toString().toLowerCase()).build();
		ResponseEntity<byte[]> response = restTemplate.getForEntity(uri, byte[].class);
		if(response.getStatusCode() == HttpStatus.FOUND) {
			throw new UnsupportedOperationException("Attempt to fetch image resulted in a redirect which could not be followed. Add Apache HttpComponents HttpClient to the classpath " +
					"to be able to follow redirects.");
		}
		return response.getBody();
	}
	
	@SuppressWarnings("unchecked")
	public String publish(String objectId, String connectionType, MultiValueMap<String, String> data) {
		MultiValueMap<String, String> requestData = new LinkedMultiValueMap<String, String>(data);
		URI uri = URIBuilder.fromUri(GRAPH_API_URL + objectId + "/" + connectionType).build();
		Map<String, Object> response = restTemplate.postForObject(uri, requestData, Map.class);
		checkForErrors(response);
		return (String) response.get("id");
	}
	
	public void post(String objectId, String connectionType, MultiValueMap<String, String> data) {
		MultiValueMap<String, String> requestData = new LinkedMultiValueMap<String, String>(data);
		URI uri = URIBuilder.fromUri(GRAPH_API_URL + objectId + "/" + connectionType).build();
		restTemplate.postForObject(uri, requestData, String.class);
	}
	
	public void delete(String objectId) {
		LinkedMultiValueMap<String, String> deleteRequest = new LinkedMultiValueMap<String, String>();
		deleteRequest.set("method", "delete");
		URI uri = URIBuilder.fromUri(GRAPH_API_URL + objectId).build();
		restTemplate.postForObject(uri, deleteRequest, String.class);
	}
	
	public void delete(String objectId, String connectionType) {
		LinkedMultiValueMap<String, String> deleteRequest = new LinkedMultiValueMap<String, String>();
		deleteRequest.set("method", "delete");
		URI uri = URIBuilder.fromUri(GRAPH_API_URL + objectId + "/" + connectionType).build();
		restTemplate.postForObject(uri, deleteRequest, String.class);
	}

	/*
	 * Facebook sometimes returns an error message with an HTTP 200. The HTTP 200 prevents the error handler
	 * from handling it, so we need to check all responses for errors before assuming that they're good data. 
	 */
	@SuppressWarnings("unchecked")
	private void checkForErrors(Map<String, Object> response) {
		if(response.containsKey("error")) {
			System.out.println("Error");
			// TODO: Revisit this problem of determining errors that look like successes
//			Map<String, String> errorDetails = (Map<String, String>) response.get("error");
//			errorHandler.handleFacebookError(errorDetails);
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
			for (int i = 1; i < strings.length; i++) {
				builder.append("," + strings[i]);
			}
		}
		return builder.toString();
	}
	
}
