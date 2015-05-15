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
 * ClientHttpRequestInterceptor implementation that adds the OAuth2 access token as a query parameter to protected resource requests before execution.
 * @author Craig Walls
 */
class OAuth2TokenParameterRequestInterceptor implements ClientHttpRequestInterceptor {

	private final String parameterName;
	
	private final String accessToken;
	
	/**
	 * Creates an instance of the interceptor, defaulting to use a parameter named "access_token".
	 * @param accessToken The access token.
	 */
	public OAuth2TokenParameterRequestInterceptor(String accessToken) {
		this(accessToken, "access_token");
	}

	/**
	 * Creates an instance of the interceptor, using a parameter with the specified name.
	 * @param accessToken The access token.
	 * @param parameterName The name of the query parameter that will carry the access token. 
	 */
	public OAuth2TokenParameterRequestInterceptor(String accessToken, String parameterName) {
		this.accessToken = accessToken;
		this.parameterName = parameterName;
	}

	public ClientHttpResponse intercept(final HttpRequest request, final byte[] body, ClientHttpRequestExecution execution) throws IOException {
		HttpRequestDecorator protectedResourceRequest = new HttpRequestDecorator(request);
		protectedResourceRequest.addParameter(parameterName, accessToken);
		return execution.execute(protectedResourceRequest, body);
	}

}
