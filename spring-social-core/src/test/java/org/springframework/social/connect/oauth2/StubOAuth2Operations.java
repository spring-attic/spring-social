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
package org.springframework.social.connect.oauth2;

import org.springframework.social.oauth2.AccessGrant;
import org.springframework.social.oauth2.GrantType;
import org.springframework.social.oauth2.OAuth2Operations;
import org.springframework.social.oauth2.OAuth2Parameters;
import org.springframework.util.MultiValueMap;

class StubOAuth2Operations implements OAuth2Operations {

	public String buildAuthorizeUrl(GrantType grantType, OAuth2Parameters parameters) {
		return "http://springsource.org/oauth/authorize?scope=" + parameters.getScope();
	}
	
	public String buildAuthenticateUrl(GrantType grantType, OAuth2Parameters parameters) {
		return buildAuthorizeUrl(grantType, parameters);
	}

	public String buildAuthorizeUrl(OAuth2Parameters parameters) {
		return "http://springsource.org/oauth/authorize?scope=" + parameters.getScope();
	}
	
	public String buildAuthenticateUrl(OAuth2Parameters parameters) {
		return buildAuthorizeUrl(parameters);
	}

	public AccessGrant exchangeForAccess(String authorizationGrant, String redirectUri, MultiValueMap<String, String> additionalParameters) {
		return new AccessGrant("12345", null, "23456", 3600L);
	}
	
	public AccessGrant exchangeCredentialsForAccess(String username, String password, MultiValueMap<String, String> additionalParameters) {
		return new AccessGrant("12345", null, "23456", 3600L);
	}
	
	public AccessGrant refreshAccess(String refreshToken, MultiValueMap<String, String> additionalParameters) {
		return new AccessGrant("12345", null,  "23456", 3600L);
	}
	
	@Deprecated
	public AccessGrant refreshAccess(String refreshToken, String scope, MultiValueMap<String, String> additionalParameters) {
		return new AccessGrant("12345", null,  "23456", 3600L);
	}
	public AccessGrant authenticateClient() {
		return new AccessGrant("12345", null,  null, 3600L);
	}
	public AccessGrant authenticateClient(String scope) {
		return new AccessGrant("12345", null,  null, 3600L);
	}

}
