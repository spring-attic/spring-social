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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.util.LinkedList;
import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.Assert;

/**
 * Mock implementation of {@link ClientHttpRequest}. Implements {@link ResponseActions} to form a fluent API.
 * 
 * @author Arjen Poutsma
 * @author Lukas Krecan
 * @author Craig Walls
 */
public class MockClientHttpRequest implements ClientHttpRequest, ResponseActions {

    private final List<RequestMatcher> requestMatchers = new LinkedList<RequestMatcher>();

	private ResponseCreator responseCreator;

	private URI uri;

	private HttpMethod httpMethod;

	private HttpHeaders httpHeaders = new HttpHeaders();

	private ByteArrayOutputStream bodyStream = new ByteArrayOutputStream();

	public void setUri(URI uri) {
		this.uri = uri;
	}

	public void setHttpMethod(HttpMethod httpMethod) {
		this.httpMethod = httpMethod;
	}

	void addRequestMatcher(RequestMatcher requestMatcher) {
		Assert.notNull(requestMatcher, "'requestMatcher' must not be null");
		requestMatchers.add(requestMatcher);
	}

	// ResponseActions implementation

	public ResponseActions andExpect(RequestMatcher requestMatcher) {
		addRequestMatcher(requestMatcher);
		return this;
	}

	public void andRespond(ResponseCreator responseCreator) {
		Assert.notNull(responseCreator, "'responseCreator' must not be null");
		this.responseCreator = responseCreator;
	}

	public HttpMethod getMethod() {
		return httpMethod;
	}

	public URI getURI() {
		return uri;
	}

	public HttpHeaders getHeaders() {
		return httpHeaders;
	}

	public OutputStream getBody() throws IOException {
		return bodyStream;
	}

	public String getBodyContent() throws IOException {
		return bodyStream.toString("UTF-8");
	}

	public ClientHttpResponse execute() throws IOException {
		if (!requestMatchers.isEmpty()) {
			for (RequestMatcher requestMatcher : requestMatchers) {
				requestMatcher.match(this);
			}
		} else {
			throw new AssertionError("Unexpected execute()");
		}

		if (responseCreator != null) {
			return responseCreator.createResponse(this);
		} else {
			return null;
		}
	}

}
