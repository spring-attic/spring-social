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

import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJacksonHttpMessageConverter;
import org.springframework.social.facebook.support.CommentApiImpl;
import org.springframework.social.facebook.support.FeedApiImpl;
import org.springframework.social.facebook.support.InterestsApiImpl;
import org.springframework.social.facebook.support.UserApiImpl;
import org.springframework.social.oauth2.ProtectedResourceClientFactory;
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

	private InterestsApiImpl interestsApi;

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
		interestsApi = new InterestsApiImpl(restTemplate);
	}

	public UserApi userApi() {
		return userApi;
	}

	public InterestsApi interestsApi() {
		return interestsApi;
	}

	public FeedApi feedApi() {
		return feedApi;
	}

	public CommentApi commentApi() {
		return commentApi;
	}

	// subclassing hooks
	
	protected RestTemplate getRestTemplate() {
		return restTemplate;
	}
	
}
