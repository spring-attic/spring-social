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

import java.util.List;
import java.util.Map;

import org.springframework.social.support.ParameterMap;

/**
 * Parameters for building an OAuth2 authorize URL.
 * @author Roy Clarkson
 * @see OAuth2Operations#buildAuthorizeUrl(GrantType, OAuth2Parameters)
 */
public final class OAuth2Parameters extends ParameterMap {

	private static final String STATE = "state";
	
	private static final String SCOPE = "scope";
	
	private static final String REDIRECT_URI = "redirect_uri";

	/**
	 * Creates a new OAuth2Parameters map that is initially empty.
	 * Use the setter methods to add parameters after construction.
	 * @see #setRedirectUri(String)
	 * @see #setScope(String)
	 * @see #setState(String)
	 * @see #set(String, String)
	 */
	public OAuth2Parameters() {
		super();
	}
	
	/**
	 * Creates a new OAuth2Parameters populated from the initial parameters provided.
	 * @param parameters the initial parameters
	 * @see #setRedirectUri(String)
	 * @see #setScope(String)
	 * @see #setState(String)
	 */
	public OAuth2Parameters(Map<String, List<String>> parameters) {
		super(parameters);
	}
	
	/**
	 * The authorization callback url.
	 * This value must match the redirectUri registered with the provider.
	 * This is optional per the OAuth 2 spec, but required by most OAuth 2 providers.
	 * @return The authorization callback url. 
	 */
	public String getRedirectUri() {
		return getFirst(REDIRECT_URI);
	}
	
	/**
	 * Sets the authorization callback url.
	 * This value must match the redirectUri registered with the provider.
	 * This is optional per the OAuth 2 spec, but required by most OAuth 2 providers.
	 * @param redirectUri the authorization callback URL
	 */
	public void setRedirectUri(String redirectUri) {
		set(REDIRECT_URI, redirectUri);
	}

	/**
	 * The permissions the application is seeking with the authorization (optional).
	 * @return the permissions the application is seeking with the authorization.
	 */
	public String getScope() {
		return getFirst(SCOPE);
	}
	
	/**
	 * Sets the permissions the application is seeking with the authorization (optional).
	 * @param scope sets the permissions the application is seeking with the authorization.
	 */
	public void setScope(String scope) {
		set(SCOPE, scope);
	}

	/**
	 * An opaque key that must be included in the provider's authorization callback (optional).
	 * @return an opaque key that must be included in the provider's authorization callback.
	 */
	public String getState() {
		return getFirst(STATE);
	}
	
	/**
	 * Sets an opaque key that must be included in the provider's authorization callback (optional).
	 * @param state an opaque key that must be included in the provider's authorization callback
	 */
	public void setState(String state) {
		set(STATE, state);
	}

}
