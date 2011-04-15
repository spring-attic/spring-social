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
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.exc.UnrecognizedPropertyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJacksonHttpMessageConverter;
import org.springframework.social.facebook.support.json.FacebookModule;
import org.springframework.social.oauth2.ProtectedResourceClientFactory;
import org.springframework.social.util.URIBuilder;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.ResourceAccessException;
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
	public <T> T fetchObject(String objectId, Class<T> type) {
		try {
			URI uri = URIBuilder.fromUri(GRAPH_API_URL + objectId).build();
			return restTemplate.getForObject(uri, type);
		} catch (ResourceAccessException e) {
			// Handle the special case where an unknown alias results in an error returned as a HTTP 200
			if(e.getCause() instanceof UnrecognizedPropertyException) {
				UnrecognizedPropertyException jsonException = (UnrecognizedPropertyException) e.getCause();
				if(jsonException.getUnrecognizedPropertyName().equals("error")) {
					throw new GraphAPIException("Unknown alias: " + objectId);
				}
			}
			// Handle any other errors that may come back from Facebook as HTTP 200
			throw new GraphAPIException("Unexpected graph API exception", e.getCause());
		}
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
