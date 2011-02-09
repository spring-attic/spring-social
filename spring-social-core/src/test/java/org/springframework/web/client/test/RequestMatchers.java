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
package org.springframework.web.client.test;

import java.io.IOException;
import java.net.URI;
import java.util.List;

import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.util.Assert;

/**
 * Factory methods for {@link RequestMatcher} classes. Typically used to provide input for
 * {@link MockRestServiceServer#expect(RequestMatcher)}.
 * 
 * @author Arjen Poutsma
 * @author Craig Walls
 */
public abstract class RequestMatchers {
	private RequestMatchers() {
	}

	/**
	 * Expects any request.
	 * 
	 * @return the request matcher
	 */
	public static RequestMatcher anything() {
		return new RequestMatcher() {
			public void match(ClientHttpRequest request) throws AssertionError {
			}
		};
	}

	/**
	 * Expects the given {@link HttpMethod}.
	 * 
	 * @param method the HTTP method
	 * @return the request matcher
	 */
	public static RequestMatcher method(final HttpMethod method) {
		Assert.notNull(method, "'method' must not be null");
		return new RequestMatcher() {
			public void match(ClientHttpRequest request) throws AssertionError {
				AssertionErrors.assertEquals("Unexpected HttpMethod", method, request.getMethod());
			}
		};
	}

	/**
	 * Expects a request to the given URI.
	 * 
	 * @param uri the request URI
	 * @return the request matcher
	 */
	public static RequestMatcher requestTo(String uri) {
		Assert.notNull(uri, "'uri' must not be null");
		return requestTo(URI.create(uri));
	}

	/**
	 * Expects a request to the given URI.
	 * 
	 * @param uri the request URI
	 * @return the request matcher
	 */
	public static RequestMatcher requestTo(URI uri) {
		Assert.notNull(uri, "'uri' must not be null");
		return new UriMatcher(uri);
	}

	/**
	 * Expects the given request header
	 * 
	 * @param header the header name
	 * @param value the header value
	 * @return the request matcher
	 */
	public static RequestMatcher header(final String header, final String value) {
		Assert.notNull(header, "'header' must not be null");
		Assert.notNull(value, "'value' must not be null");
		return new RequestMatcher() {
			public void match(ClientHttpRequest request) throws AssertionError {
				List<String> actual = request.getHeaders().get(header);
				AssertionErrors.assertTrue("Expected header in request: " + header, actual != null);
				AssertionErrors.assertTrue("Unexpected header", actual.contains(value));
			}
		};
	}
	
	/**
	 * Expects the given request body content
	 * 
	 * @param body the request body
	 * @return the request matcher
	 */
	public static RequestMatcher body(final String body) {
		Assert.notNull(body, "'body' must not be null");
		return new RequestMatcher() {
			public void match(ClientHttpRequest request) throws AssertionError, IOException {
				MockClientHttpRequest mockRequest = (MockClientHttpRequest) request;
				AssertionErrors.assertEquals("Unexpected body content", body,
						mockRequest.getBodyContent());
			}
		};
	}
}
