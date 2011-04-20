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

import org.springframework.util.MultiValueMap;

/**
 * Parameters for building an OAuth2 authorization URL
 * 
 * @author Roy Clarkson
 */
public final class AuthorizationParameters {
	
	private final String redirectUri;
	
	private final String scope;
	
	private final String state;
	
	private final MultiValueMap<String, String> additionalParameters;
		
	/**
	 * @param redirectUri the authorization callback url; this value must match the redirectUri registered with the provider
	 * @param scope the permissions the application is seeking with the authorization
	 * @param state
	 * @param additionalParameters additional parameters required by the provider
	 */
	public AuthorizationParameters(String redirectUri, String scope, String state, MultiValueMap<String, String> additionalParameters) {
		this.redirectUri = redirectUri;
		this.scope = scope;
		this.state = state;
		this.additionalParameters = additionalParameters;
	}
	
	public String getRedirectUri() {
		return redirectUri;
	}

	public String getScope() {
		return scope;
	}

	public String getState() {
		return state;
	}

	public MultiValueMap<String, String> getAdditionalParameters() {
		return additionalParameters;
	}
}
