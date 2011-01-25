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
package org.springframework.security.oauth.client.oauth1;

import org.springframework.security.oauth.client.ClientRequest;
import org.springframework.security.oauth.client.RestTemplateInterceptor;

/**
 * ClientRequestInterceptor implementation that performs OAuth1 request signing before the request is executed.
 * @author Keith Donald
 * @author Craig Walls
 */
public class OAuth1ClientRequestInterceptor implements RestTemplateInterceptor {

	private final OAuthToken accessToken;
	private final String consumerKey;
	private final String consumerSecret;

	/**
	 * Creates a OAuth 1.0 client request interceptor.
	 * @param accessToken the access token and secret
	 */
	public OAuth1ClientRequestInterceptor(String consumerKey, String consumerSecret, OAuthToken accessToken) {
		this.consumerKey = consumerKey;
		this.consumerSecret = consumerSecret;
		this.accessToken = accessToken;
	}

	public void beforeExecution(ClientRequest request) {
		request.getHeaders().set("Authorization", OAuth1SigningUtils.buildAuthorizationHeader(request, consumerKey, consumerSecret,
				accessToken));
	}

}
