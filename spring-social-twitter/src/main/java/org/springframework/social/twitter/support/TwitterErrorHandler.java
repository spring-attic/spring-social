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

import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
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
	private static List<HttpStatus> NOT_ERRORS = Arrays.asList(HttpStatus.FORBIDDEN, HttpStatus.UNAUTHORIZED);
	
	// TODO: This is targeting a specific issue at the moment. I will probably
	// want to revisit this error handler to better address other Twitter errors
	// or even create a more generic error handler that can be used with the
	// other social networks.
	public boolean hasError(ClientHttpResponse response) throws IOException {
		if (NOT_ERRORS.contains(response.getStatusCode())) {
			return false;
		}

		return super.hasError(response);
	}

}
