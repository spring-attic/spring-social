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
 * Internally used for Spring 3.0 compatibility only. Spring 3.1 uses request interceptors.
 * @author Craig Walls
 */
class OAuth2SigningRequestFactory implements ClientHttpRequestFactory {
	private final ClientHttpRequestFactory delegate;
	private final String accessToken;
	private final org.springframework.social.oauth2.OAuth2SigningRequestFactory.OAuth2SigningRequest.PreviousOAuth2Version previousOAuth2Version;

	public static OAuth2SigningRequestFactory standard(ClientHttpRequestFactory delegate, String accessToken) {
		return new OAuth2SigningRequestFactory(delegate, accessToken, null);
	}

	public static OAuth2SigningRequestFactory draft8(ClientHttpRequestFactory delegate, String accessToken) {
		return new OAuth2SigningRequestFactory(delegate, accessToken, OAuth2SigningRequest.PreviousOAuth2Version.DRAFT_8);
	}

	public static OAuth2SigningRequestFactory draft10(ClientHttpRequestFactory delegate, String accessToken) {
		return new OAuth2SigningRequestFactory(delegate, accessToken, OAuth2SigningRequest.PreviousOAuth2Version.DRAFT_10);
	}

	private OAuth2SigningRequestFactory(ClientHttpRequestFactory delegate, String accessToken, OAuth2SigningRequest.PreviousOAuth2Version previousOAuth2Version) {
		this.delegate = delegate;
		this.accessToken = accessToken;
		this.previousOAuth2Version = previousOAuth2Version;
	}

	public ClientHttpRequest createRequest(URI uri, HttpMethod httpMethod) throws IOException {
		return new OAuth2SigningRequest(delegate.createRequest(uri, httpMethod), accessToken, previousOAuth2Version);
	}	

	private static class OAuth2SigningRequest implements ClientHttpRequest {
		private final ClientHttpRequest delegate;
		private ByteArrayOutputStream bodyOutputStream;
		private final String accessToken;
		private final PreviousOAuth2Version previousOAuth2Version;

		public OAuth2SigningRequest(ClientHttpRequest delegate, String accessToken, PreviousOAuth2Version previousOAuth2Version) {
			this.delegate = delegate;
			this.accessToken = accessToken;
			this.previousOAuth2Version = previousOAuth2Version;
			this.bodyOutputStream = new ByteArrayOutputStream();
		}

		public ClientHttpResponse execute() throws IOException {
			byte[] bufferedOutput = bodyOutputStream.toByteArray();
			HttpHeaders headers = getHeaders();
			delegate.getBody().write(bufferedOutput);
			delegate.getHeaders().putAll(headers);
			delegate.getHeaders().set("Authorization", getAuthorizationHeaderValue());
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

		private String getAuthorizationHeaderValue() {
			return previousOAuth2Version == null ? "BEARER " + accessToken : previousOAuth2Version
					.getAuthorizationHeaderValue(accessToken);
		}

		private static enum PreviousOAuth2Version {

			DRAFT_10 {
				public String getAuthorizationHeaderValue(String accessToken) {
					return "OAuth " + accessToken + "";
				}
			},

			DRAFT_8 {
				public String getAuthorizationHeaderValue(String accessToken) {
					return "Token token=\"" + accessToken + "\"";
				}
			};

			public abstract String getAuthorizationHeaderValue(String accessToken);
		}
	}

}