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

import org.springframework.util.LinkedMultiValueMap;

/**
 * Parameters for building an OAuth2 authorize URL.
 * @author Roy Clarkson
 * @see OAuth2Operations#buildAuthorizeUrl(GrantType, OAuth2Parameters)
 */
@SuppressWarnings("serial")
public final class OAuth2Parameters extends LinkedMultiValueMap<String, String> {

	private static final String STATE = "state";
	
	private static final String SCOPE = "scope";
	
	private static final String REDIRECT_URI = "redirect_uri";

	/**
	 * Returns the authorization callback url; this value must match the redirectUri registered with the provider (optional per the OAuth 2 spec, but required by most OAuth 2 providers). 
	 */
	public String getRedirectUri() {
		return getFirst(REDIRECT_URI);
	}
	
	/**
	 * Sets the authorization callback url; this value must match the redirectUri registered with the provider (optional per the OAuth 2 spec, but required by most OAuth 2 providers). 
	 */
	public void setRedirectUri(String redirectUri) {
		set(REDIRECT_URI, redirectUri);
	}

	/**
	 * Returns the permissions the application is seeking with the authorization (optional).
	 */
	public String getScope() {
		return getFirst(SCOPE);
	}
	
	/**
	 * Sets the permissions the application is seeking with the authorization (optional).
	 */
	public void setScope(String scope) {
		set(SCOPE, scope);
	}

	/**
	 * Returns an opaque key that must be included in the provider's authorization callback (optional).
	 */
	public String getState() {
		return getFirst(STATE);
	}
	
	/**
	 * Sets an opaque key that must be included in the provider's authorization callback (optional).
	 */
	public void setState(String state) {
		set(STATE, state);
	}

}
