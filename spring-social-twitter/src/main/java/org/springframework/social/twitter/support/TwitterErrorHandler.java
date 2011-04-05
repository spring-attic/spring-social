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
import java.util.Map;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.social.BadCredentialsException;
import org.springframework.social.OperationNotPermittedException;
import org.springframework.social.twitter.DuplicateTweetException;
import org.springframework.social.twitter.EnhanceYourCalmException;
import org.springframework.social.twitter.InternalProviderErrorException;
import org.springframework.social.twitter.InvalidMessageRecipientException;
import org.springframework.social.twitter.NotFoundException;
import org.springframework.social.twitter.StatusLengthException;
import org.springframework.social.twitter.ProviderDownException;
import org.springframework.social.twitter.ProviderOverloadedException;
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
		
	@Override
	public void handleError(ClientHttpResponse response) throws IOException {
		handleServerErrors(response);
		handleClientErrors(response);
		super.handleError(response);
	}
	
	@Override
	protected boolean hasError(HttpStatus statusCode) {
//		if(statusCode == HttpStatus.UNAUTHORIZED) {
//			throw new BadCredentialsException("");
//			return true;
//		}
		
		return super.hasError(statusCode);
	}

	private void handleClientErrors(ClientHttpResponse response) throws IOException {
		HttpStatus statusCode = response.getStatusCode();

		if (statusCode == HttpStatus.UNAUTHORIZED) {
			throw new BadCredentialsException("Bad or missing access token.");
		}
		
		Map<String, String> errorMap = extractErrorDetailsFromResponse(response);
	    String errorText = errorMap.get("error");
		
		if(statusCode == HttpStatus.FORBIDDEN) {
			if (errorText.equals(DUPLICATE_STATUS_TEXT) || errorText.contains("You already said that")) {
				throw new DuplicateTweetException(errorText);
			} else if (errorText.equals(STATUS_TOO_LONG_TEXT)) {
				throw new StatusLengthException(errorText);
			} else if (errorText.equals(INVALID_MESSAGE_RECIPIENT_TEXT)) {
				throw new InvalidMessageRecipientException(errorText);
			} else {
				throw new OperationNotPermittedException(errorText);
			}			
		} else if (statusCode == HttpStatus.NOT_FOUND) {
			throw new NotFoundException(errorText + "; Path: " + errorMap.get("request"));
		} else if (statusCode == HttpStatus.valueOf(ENHANCE_YOUR_CALM)) {
			throw new EnhanceYourCalmException(errorText);
		}
	}

	private void handleServerErrors(ClientHttpResponse response) throws IOException {
		HttpStatus statusCode = response.getStatusCode();
		if (statusCode == HttpStatus.INTERNAL_SERVER_ERROR) {
			throw new InternalProviderErrorException("Something is broken at Twitter. Please see http://dev.twitter.com/pages/support to report the issue.");
		} else if (statusCode == HttpStatus.BAD_GATEWAY) {
			throw new ProviderDownException("Twitter is down or is being upgraded.");
		} else if (statusCode == HttpStatus.SERVICE_UNAVAILABLE) {
			throw new ProviderOverloadedException("Twitter is overloaded with requests. Try again later.");
		}
	}

	private Map<String, String> extractErrorDetailsFromResponse(ClientHttpResponse response) throws IOException {
		ObjectMapper mapper = new ObjectMapper(new JsonFactory());
	    return mapper.<Map<String, String>>readValue(response.getBody(), new TypeReference<Map<String, String>>() {});
	}

	private static final String INVALID_MESSAGE_RECIPIENT_TEXT = "You cannot send messages to users who are not following you.";
	private static final String STATUS_TOO_LONG_TEXT = "Status is over 140 characters.";
	private static final String DUPLICATE_STATUS_TEXT = "Status is a duplicate.";
	
	private static final int ENHANCE_YOUR_CALM = 420;
}
