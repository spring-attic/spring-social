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

import java.io.IOException;
import java.net.URI;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

/**
 * ClientHttpRequestInterceptor implementation that adds the OAuth2 access token to protected resource requests before execution.
 * @author Keith Donald
 * @author Craig Walls
 */
public class OAuth2RequestInterceptor implements ClientHttpRequestInterceptor {

	private final String accessToken;
	
	private final PreviousOAuth2Version previousOAuth2Version;

	/**
	 * Constructs a request interceptor that adds the Authorization header using the bearer token style described in the latest draft (draft 12) of the OAuth2 specification:
	 * http://tools.ietf.org/html/draft-ietf-oauth-v2-12#section-7.1
	 * @param accessToken the access token
	 */
	public OAuth2RequestInterceptor(String accessToken) {
		this(accessToken, null);
	}
	
	public ClientHttpResponse intercept(final HttpRequest request, final byte[] body, ClientHttpRequestExecution execution) throws IOException {
		HttpRequest protectedResourceRequest = new HttpRequest() {
			public HttpHeaders getHeaders() {
				HttpHeaders headers = new HttpHeaders();
				headers.putAll(request.getHeaders());
				headers.set("Authorization", getAuthorizationHeaderValue());
				return headers;
			}
			
			public URI getURI() {
				return request.getURI();
			}
			
			public HttpMethod getMethod() {
				return request.getMethod();
			}
		};
		return execution.execute(protectedResourceRequest, body);
	}

	/**
	 * Static factory method that constructs a OAuth2ClientRequestInterceptor.
	 * This implementation adds the Authorization header using the style described in draft 10 of the OAuth2 specification:
	 * http://tools.ietf.org/html/draft-ietf-oauth-v2-10#section-5.1.1
	 * @author Craig Walls
	 */
	public static OAuth2RequestInterceptor draft10(String accessToken) {
		return new OAuth2RequestInterceptor(accessToken, PreviousOAuth2Version.DRAFT_10);
	}

	/**
	 * Static factory method that constructs a OAuth2ClientRequestInterceptor.
	 * This implementation adds the Authorization header using the style described in draft 8 of the OAuth2 specification:
	 * http://tools.ietf.org/html/draft-ietf-oauth-v2-08#section-5.1
	 * @author Craig Walls
	 */
	public static OAuth2RequestInterceptor draft8(String accessToken) {
		return new OAuth2RequestInterceptor(accessToken, PreviousOAuth2Version.DRAFT_8);
	}

	// internal helpers

	private OAuth2RequestInterceptor(String accessToken, PreviousOAuth2Version previousOAuth2Version) {
		this.accessToken = accessToken;
		this.previousOAuth2Version = previousOAuth2Version;
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
	
	private String getAuthorizationHeaderValue() {
		return previousOAuth2Version == null ? "BEARER " + accessToken : previousOAuth2Version.getAuthorizationHeaderValue(accessToken);
	}
	
}