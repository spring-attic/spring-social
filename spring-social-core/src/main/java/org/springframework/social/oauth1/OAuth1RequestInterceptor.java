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
package org.springframework.social.oauth1;

import java.io.IOException;

import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.social.support.HttpRequestDecorator;

/**
 * ClientHttpRequestInterceptor implementation that performs OAuth1 request signing before a request for a protected resource is executed.
 * @author Keith Donald
 * @author Craig Walls
 */
public class OAuth1RequestInterceptor implements ClientHttpRequestInterceptor {

	private final String consumerKey;
	
	private final String consumerSecret;

	private final OAuthToken accessToken;

	/**
	 * Creates an OAuth 1.0 protected resource request interceptor.
	 * @param accessToken the access token and secret
	 */
	public OAuth1RequestInterceptor(String consumerKey, String consumerSecret, OAuthToken accessToken) {
		this.consumerKey = consumerKey;
		this.consumerSecret = consumerSecret;
		this.accessToken = accessToken;
	}

	public ClientHttpResponse intercept(final HttpRequest request, final byte[] body, ClientHttpRequestExecution execution) throws IOException {
		HttpRequest protectedResourceRequest = new HttpRequestDecorator(request);
		protectedResourceRequest.getHeaders().add("Authorization", getAuthorizationHeaderValue(request, body));
		return execution.execute(protectedResourceRequest, body);
	}

	// internal helpers
	
	private String getAuthorizationHeaderValue(HttpRequest request, byte[] body) {
		return SigningUtils.buildAuthorizationHeaderValue(request, body, consumerKey, consumerSecret, accessToken);
	}
	
}