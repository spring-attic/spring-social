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

import java.net.URI;
import java.net.URISyntaxException;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpRequest;

public class MockHttpRequest implements HttpRequest {

	private final HttpHeaders headers = new HttpHeaders();

	private HttpMethod method;
	
	private URI uri;

	public MockHttpRequest(String uri) {
		this(HttpMethod.GET, uri);
	}

	public MockHttpRequest(HttpMethod method, String uri) {
		try {
			this.uri = new URI(uri);
		} catch (URISyntaxException e) {
			throw new IllegalArgumentException("Invalid uri: '"+ uri + "'", e);
		}
		this.method = method;
	}
	
	public HttpHeaders getHeaders() {
		return headers;
	}

	public HttpMethod getMethod() {
		return method;
	}

	public URI getURI() {
		return uri;
	}

}