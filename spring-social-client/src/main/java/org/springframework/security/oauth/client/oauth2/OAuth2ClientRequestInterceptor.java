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
package org.springframework.security.oauth.client.oauth2;

import org.springframework.security.oauth.client.ClientRequest;
import org.springframework.security.oauth.client.RestTemplateInterceptor;

/**
 * ClientRequestInterceptor implementation that adds the OAuth2 access token to the request before execution.
 * @author Keith Donald
 */
public class OAuth2ClientRequestInterceptor implements RestTemplateInterceptor {

	private String accessToken;
	
	public OAuth2ClientRequestInterceptor(String accessToken) {
		this.accessToken = accessToken;
	}

	public void beforeExecution(ClientRequest request) {
		// Draft 8/9 header: Supported by Gowalla and GitHub
		System.out.println("Adding authorization header:  " + "Token token=\"" + accessToken + "\"");
		request.getHeaders().set("Authorization", "Token token=\"" + accessToken + "\"");
		
		// Draft 10 header: Supported by Facebook
		//		request.getHeaders().set("Authorization", "OAuth " + accessToken + "");
		
		// Draft 12 header: Supported by nobody yet (two variations depending on how you read the spec
		//		request.getHeaders().set("Authorization", "BEARER " + accessToken + "");
		//		request.getHeaders().set("Authorization", "OAuth2 " + accessToken + "");
	}
	
}