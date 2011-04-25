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
 * Parameters for building an OAuth2 authorize URL.
 * @author Roy Clarkson
 * @see OAuth2Operations#buildAuthorizeUrl(GrantType, OAuth2Parameters)
 */
public final class OAuth2Parameters {
	
	private final String redirectUri;
	
	private final String scope;
	
	private final String state;
	
	private final MultiValueMap<String, String> additionalParameters;

	/**
	 * Creates a new authorization parameters instance.
	 * @param redirectUri the authorization callback url; this value must match the redirectUri registered with the provider (required)
	 */
	public OAuth2Parameters(String redirectUri) {
		this(redirectUri, null, null, null);
	}

	/**
	 * Creates a new authorization parameters instance.
	 * @param redirectUri the authorization callback url; this value must match the redirectUri registered with the provider (required)
	 * @param scope the permissions the application is seeking with the authorization (optional)
	 */
	public OAuth2Parameters(String redirectUri, String scope) {
		this(redirectUri, scope, null, null);
	}
	
	/**
	 * Creates a new authorization parameters instance.
	 * @param redirectUri the authorization callback url; this value must match the redirectUri registered with the provider (required)
	 * @param scope the permissions the application is seeking with the authorization (optional)
	 * @param state an opaque key that must be included in the provider's authorization callback (optional)
	 * @param additionalParameters additional supported parameters to pass to the provider (optional)
	 */
	public OAuth2Parameters(String redirectUri, String scope, String state, MultiValueMap<String, String> additionalParameters) {
		this.redirectUri = redirectUri;
		this.scope = scope;
		this.state = state;
		this.additionalParameters = additionalParameters;
	}
	
	/**
	 * The authorization callback url; this value must match the redirectUri registered with the provider (required). 
	 */
	public String getRedirectUri() {
		return redirectUri;
	}

	/**
	 * The permissions the application is seeking with the authorization (optional).
	 */
	public String getScope() {
		return scope;
	}

	/**
	 * An opaque key that must be included in the provider's authorization callback (optional).
	 */
	public String getState() {
		return state;
	}

	/**
	 * Additional supported parameters to pass to the provider (optional).
	 */
	public MultiValueMap<String, String> getAdditionalParameters() {
		return additionalParameters;
	}
	
}
