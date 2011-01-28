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

import org.springframework.social.intercept.ClientRequest;
import org.springframework.social.intercept.ClientRequestInterceptor;

/**
 * ClientRequestInterceptor implementation that performs OAuth1 request signing before the request is executed.
 * @author Keith Donald
 * @author Craig Walls
 */
public class OAuth1ClientRequestInterceptor implements ClientRequestInterceptor {

	private final String consumerKey;
	
	private final String consumerSecret;

	private final OAuthToken accessToken;

	/**
	 * Creates an OAuth 1.0 client request interceptor.
	 * @param accessToken the access token and secret
	 */
	public OAuth1ClientRequestInterceptor(String consumerKey, String consumerSecret, OAuthToken accessToken) {
		this.consumerKey = consumerKey;
		this.consumerSecret = consumerSecret;
		this.accessToken = accessToken;
	}

	public void beforeExecution(ClientRequest request) {
		request.getHeaders().set("Authorization", SigningUtils.buildAuthorizationHeader(request, consumerKey, consumerSecret, accessToken));
	}

}
