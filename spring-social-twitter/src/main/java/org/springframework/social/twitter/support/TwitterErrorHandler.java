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
package org.springframework.social.twitter.support;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.social.AccountNotConnectedException;
import org.springframework.social.OperationNotPermittedException;
import org.springframework.social.twitter.DuplicateTweetException;
import org.springframework.social.twitter.FriendshipFailureException;
import org.springframework.social.twitter.InvalidMessageRecipientException;
import org.springframework.social.twitter.TwitterTemplate;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

/**
 * Subclass of {@link DefaultResponseErrorHandler} that allows some client and
 * server exceptions to flow through {@link RestTemplate} as non-errors.
 * 
 * By default, RestTemplate interprets HTTP client (4xx series) errors and
 * server (5xx series) errors as exceptions and throws an
 * {@link HttpClientErrorException}. In doing so, this prevents the caller from
 * retrieving the body of the response.
 * 
 * When Twitter returns an error, it places some extra information about that
 * error in the body of the response. In order to allow {@link TwitterTemplate}
 * to read that information, those errors need to be treated as non-errors by
 * RestTemplate. This puts the burden on TwitterTemplate to interpret the error
 * and act accordingly. But it also enables TwitterTemplate to obtain more
 * information about the nature of the error.
 * 
 * @author Craig Walls
 */
public class TwitterErrorHandler extends DefaultResponseErrorHandler {
	private static List<HttpStatus> NOT_ERRORS = Arrays.asList();
	
	public boolean hasError(ClientHttpResponse response) throws IOException {
		if (NOT_ERRORS.contains(response.getStatusCode())) {
			return false;
		}

		return super.hasError(response);
	}
	
	@Override
	public void handleError(ClientHttpResponse response) throws IOException {
	    String errorText = extractErrorTextFromResponse(response);	    
		HttpStatus statusCode = response.getStatusCode();
		
		if(statusCode == HttpStatus.FORBIDDEN) {
			if (errorText.equals(DUPLICATE_STATUS_TEXT) || errorText.contains("You already said that")) {
				throw new DuplicateTweetException(errorText);
			} else if (errorText.equals(INVALID_MESSAGE_RECIPIENT_TEXT)) {
				throw new InvalidMessageRecipientException(errorText);
			} else if (errorText.contains(COULD_NOT_FOLLOW_USER_TEXT)  && errorText.contains(USER_ALREADY_ON_LIST_TEXT)){
				throw new FriendshipFailureException(errorText);
			} else if (errorText.equals(USER_IS_NOT_FRIENDS_TEXT)) {
				throw new FriendshipFailureException(errorText);
			} else {
				throw new OperationNotPermittedException(errorText);
			}			
		} else if (statusCode.equals(HttpStatus.UNAUTHORIZED)) {
			throw new AccountNotConnectedException(errorText);
		}
		super.handleError(response);
	}

	private String extractErrorTextFromResponse(ClientHttpResponse response) throws IOException {
		ObjectMapper mapper = new ObjectMapper(new JsonFactory()); 
	    Map<String, String> body = mapper.readValue(response.getBody(), new TypeReference<Map<String, String>>() {}); 
		return body.get("error");
	}

	static final String INVALID_MESSAGE_RECIPIENT_TEXT = "You cannot send messages to users who are not following you.";
	static final String DUPLICATE_STATUS_TEXT = "Status is a duplicate.";
	
	static final String COULD_NOT_FOLLOW_USER_TEXT = "Could not follow user";
	static final String USER_ALREADY_ON_LIST_TEXT = "is already on your list";
	
	static final String USER_IS_NOT_FRIENDS_TEXT = "You are not friends with the specified user.";

}
