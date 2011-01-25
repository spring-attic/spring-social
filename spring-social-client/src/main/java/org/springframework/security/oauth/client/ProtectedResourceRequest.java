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
package org.springframework.security.oauth.client;

import java.net.URI;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;

/**
 * A client request for a resource protected by OAuth.
 * Used to sign the request with OAuth credentials before it is executed.
 * @author Keith Donald
 */
public final class ProtectedResourceRequest {
	
	private final HttpHeaders headers;
	
	private final byte[] body;
	
	private final URI uri;
	
	private final HttpMethod method;

	public ProtectedResourceRequest(HttpHeaders headers, byte[] body, URI uri, HttpMethod method) {
		this.headers = headers;
		this.body = body;
		this.uri = uri;
		this.method = method;
	}

	/**
	 * The request headers.
	 * Will usually be modified during the signing process; for example, to add an Authorization header.
 	 */
	public HttpHeaders getHeaders() {
		return headers;
	}

	/**
	 * The request body.
	 * Needed for signature generation for OAuth 1 requests.
	 */
	public byte[] getBody() {
		return body;
	}

	/**
	 * The target resource URL.
	 * Needed for signature generation for OAuth 1 requests. 
	 */
	public URI getUri() {
		return uri;
	}

	/**
	 * The request method.
	 * Needed for signature generation for OAuth 1 requests. 
	 */
	public HttpMethod getMethod() {
		return method;
	}
	
}
