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
 * Parameters for building an OAuth2 authentication URL
 * 
 * @author Roy Clarkson
 */
public class AuthenticateParameters {
	
	private String redirectUri;
	
	private String state;
	
	private GrantType grantType;
	
	private MultiValueMap<String, String> additionalParameters;
	
	/**
	 * @param redirectUri the authorization callback url; this value must match the redirectUri registered with the provider
	 * @param state
	 * @param grantType specifies whether the OAuth flow is "client-side" or "server-side"
	 * @param additionalParameters additional parameters required by the provider
	 */
	public AuthenticateParameters(String redirectUri, String state, GrantType grantType, MultiValueMap<String, String> additionalParameters) {
		this.redirectUri = redirectUri;
		this.state = state;
		this.grantType = grantType;
		this.additionalParameters = additionalParameters;
	}

	public void setRedirectUri(String redirectUri) {
		this.redirectUri = redirectUri;
	}

	public String getRedirectUri() {
		return redirectUri;
	}
	
	public void setState(String state) {
		this.state = state;
	}

	public String getState() {
		return state;
	}

	public void setGrantType(GrantType grantType) {
		this.grantType = grantType;
	}

	public GrantType getGrantType() {
		return grantType;
	}

	public void setAdditionalParameters(MultiValueMap<String, String> additionalParameters) {
		this.additionalParameters = additionalParameters;
	}

	public MultiValueMap<String, String> getAdditionalParameters() {
		return additionalParameters;
	}
}
