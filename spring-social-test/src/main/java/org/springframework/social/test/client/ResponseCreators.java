/*
 * Copyright 2011 the original author or authors.
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
package org.springframework.social.test.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.util.Assert;

/**
 * Factory methods for {@link ResponseCreator} classes. Typically used to provide input for {@link
 * ResponseActions#andRespond(ResponseCreator)}.
 *
 * @author Arjen Poutsma
 * @author Craig Walls
 */
public abstract class ResponseCreators {
	private ResponseCreators() {
	}

	/**
	 * Respond with a given response body, headers, status code, and status text.
	 * 
	 * @param responseBody the body of the response
	 * @param headers the response headers
	 * @param statusCode the response status code
	 * @param statusText the response status text
	 * @return a {@link ResponseCreator}
	 */
	public static ResponseCreator withResponse(final String responseBody, final HttpHeaders headers,
			final HttpStatus statusCode, final String statusText) {
		Assert.notNull(responseBody, "'responseBody' must not be null");
		return new ResponseCreator() {
			public MockClientHttpResponse createResponse(ClientHttpRequest request) {
				return new MockClientHttpResponse(responseBody, headers, statusCode, statusText);
			}
		};
	}
	
	/**
	 * Response with a given response body and headers. The response status code is HTTP 200 (OK).
	 * @param responseBody the body of the response
	 * @param headers the response headers
	 * @return a {@link ResponseCreator}
	 */
	public static ResponseCreator withResponse(String responseBody, HttpHeaders headers) {
		return withResponse(responseBody, headers, HttpStatus.OK, "");
	}

	/**
	 * Respond with a given response body (from a {@link Resource}) and headers. The response status code is HTTP 200 (OK).
	 * 
	 * @param responseBodyResource a {@link Resource} containing the body of the response
	 * @param headers the response headers
	 * @param statusCode the response status code
	 * @param statusText the response status text
	 * @return a {@link ResponseCreator}
	 */
	public static ResponseCreator withResponse(final Resource responseBodyResource, final HttpHeaders headers,
			final HttpStatus statusCode, final String statusText) {
		return withResponse(readResource(responseBodyResource), headers, statusCode, statusText);
	}
	
	/**
	 * Response with a given response body and headers. The response status code is HTTP 200 (OK).
	 * @param responseBody the body of the response
	 * @param headers the response headers
	 * @return a {@link ResponseCreator}
	 */
	public static ResponseCreator withResponse(Resource responseBody, HttpHeaders headers) {
		return withResponse(responseBody, headers, HttpStatus.OK, "");
	}

	private static String readResource(Resource resource) {
		StringBuilder resourceText = new StringBuilder();
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream()));
			while (reader.ready()) {
				resourceText.append(reader.readLine() + "\n");
			}
		} catch (IOException e) {
		}
		return resourceText.toString();
	}
}
