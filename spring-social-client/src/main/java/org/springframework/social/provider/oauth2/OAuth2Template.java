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
package org.springframework.social.provider.oauth2;

/**
 * OAuth2Operations implementation that uses REST-template to make the OAuth calls.
 * @author Keith Donald
 */
public class OAuth2Template implements OAuth2Operations {

	private final String clientId;
	
	private final String clientSecret;
	
	public OAuth2Template(String clientId, String clientSecret) {
		this.clientId = clientId;
		this.clientSecret = clientSecret;
	}

	public String buildAuthorizeUrl(String redirectUri, String scope) {
		return null;
	}

	public AccessToken exchangeForAccessToken(String redirectUri, String authorizationCode) {
		return null;
	}
	
}
