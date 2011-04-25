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
package org.springframework.social.oauth1;

import org.springframework.util.MultiValueMap;

/**
 * Parameters for building an OAuth1 authorize URL.
 * @author Keith Donald
 * @see OAuth1Operations#buildAuthorizeUrl(String, OAuth1Parameters)
 */
public final class OAuth1Parameters {
	
	private final String callbackUrl;
	
	private final MultiValueMap<String, String> additionalParameters;

	/**
	 * Shared instance for passing zero authorization parameters (accepted for OAuth 1.0a-based flows).
	 */
	public static final OAuth1Parameters NONE = new OAuth1Parameters(null, null);
	
	/**
	 * Creates a new OAuth1Parameters instance.
	 * @param callbackUrl the authorization callback url; this value must be included for OAuth 1.0 providers (and NOT for OAuth 1.0a)
	 */
	public OAuth1Parameters(String callbackUrl) {
		this(callbackUrl, null);
	}
	
	/**
	 * Creates a new OAuth1Parameters instance.
	 * @param callbackUrl the authorization callback url; this value must be included for OAuth 1.0 providers (and NOT for OAuth 1.0a)
	 * @param additionalParameters additional supported parameters to pass to the provider
	 */
	public OAuth1Parameters(String callbackUrl, MultiValueMap<String, String> additionalParameters) {
		this.callbackUrl = callbackUrl;
		this.additionalParameters = additionalParameters;
	}

	/**
	 * The authorization callback url; this value must be included for OAuth 1.0 providers (and NOT for OAuth 1.0a)
	 */
	public String getCallbackUrl() {
		return callbackUrl;
	}

	/**
	 * Additional supported parameters to pass to the provider.
	 */
	public MultiValueMap<String, String> getAdditionalParameters() {
		return additionalParameters;
	}
	
}
