/*
 * Copyright 2015 the original author or authors.
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

import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.social.support.HttpRequestDecorator;

/**
 * ClientHttpRequestInterceptor implementation that adds the OAuth2 access token to protected resource requests before execution.
 * @author Keith Donald
 * @author Craig Walls
 */
class OAuth2RequestInterceptor implements ClientHttpRequestInterceptor {

	private final String accessToken;
	
	private final OAuth2Version oauth2Version;

	public OAuth2RequestInterceptor(String accessToken, OAuth2Version oauth2Version) {
		this.accessToken = accessToken;
		this.oauth2Version = oauth2Version;
	}
	
	public ClientHttpResponse intercept(final HttpRequest request, final byte[] body, ClientHttpRequestExecution execution) throws IOException {
		HttpRequest protectedResourceRequest = new HttpRequestDecorator(request);
		protectedResourceRequest.getHeaders().set("Authorization", oauth2Version.getAuthorizationHeaderValue(accessToken));
		return execution.execute(protectedResourceRequest, body);
	}

}
