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
package org.springframework.social.oauth2;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpResponse;

/**
 * Request factory that signs RestTemplate requests with an OAuth 2 Authorization header.
 * Internally used for Spring 3.0 compatibility only.
 * Planned for removal in Spring Social 1.1.
 * @author Craig Walls
 */
class Spring30OAuth2RequestFactory implements ClientHttpRequestFactory {
	
	private final ClientHttpRequestFactory delegate;
	
	private final String accessToken;
	
	private final OAuth2Version oauth2Version;

	public Spring30OAuth2RequestFactory(ClientHttpRequestFactory delegate, String accessToken, OAuth2Version oauth2Version) {
		this.delegate = delegate;
		this.accessToken = accessToken;
		this.oauth2Version = oauth2Version;
	}

	public ClientHttpRequest createRequest(URI uri, HttpMethod httpMethod) throws IOException {
		return new OAuth2SigningRequest(delegate.createRequest(uri, httpMethod), accessToken, oauth2Version);
	}	

	private static class OAuth2SigningRequest implements ClientHttpRequest {
		
		private final ClientHttpRequest delegate;

		private ByteArrayOutputStream bodyOutputStream;
		
		private final String accessToken;
		
		private final OAuth2Version oauth2Version;

		public OAuth2SigningRequest(ClientHttpRequest delegate, String accessToken, OAuth2Version oauth2Version) {
			this.delegate = delegate;
			this.accessToken = accessToken;
			this.oauth2Version = oauth2Version;
			this.bodyOutputStream = new ByteArrayOutputStream();
		}

		public ClientHttpResponse execute() throws IOException {
			byte[] bufferedOutput = bodyOutputStream.toByteArray();
			delegate.getBody().write(bufferedOutput);
			delegate.getHeaders().set("Authorization", oauth2Version.getAuthorizationHeaderValue(accessToken));
			return delegate.execute();
		}

		public URI getURI() {
			return delegate.getURI();
		}

		public HttpMethod getMethod() {
			return delegate.getMethod();
		}

		public HttpHeaders getHeaders() {
			return delegate.getHeaders();
		}

		public OutputStream getBody() throws IOException {
			return bodyOutputStream;
		}
		
	}

}