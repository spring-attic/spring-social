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
package org.springframework.social.oauth1;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.springframework.social.support.ParameterMap;

/**
 * Parameters for building an OAuth1 authorize URL.
 * @author Keith Donald
 * @see OAuth1Operations#buildAuthorizeUrl(String, OAuth1Parameters)
 */
public final class OAuth1Parameters extends ParameterMap {

	private static final String OAUTH_CALLBACK = "oauth_callback";
	
	/**
	 * Shared instance for passing zero authorization parameters (common for OAuth 1.0a-based flows).
	 * The underlying map is immutable.
	 * @see Collections#emptyMap()
	 */
	public static final OAuth1Parameters NONE = new OAuth1Parameters(Collections.<String, List<String>>emptyMap());

	/**
	 * Creates a new OAuth1Parameters map that is initially empty.
	 * Use the setter methods to add parameters after construction.
	 * @see #setCallbackUrl(String)
	 * @see #set(String, String)
	 */
	public OAuth1Parameters() {
		super();
	}
	
	/**
	 * Creates a new OAuth1Parameters populated from the initial parameters provided.
	 * @param parameters the initial parameters
	 * @see #setCallbackUrl(String)
	 */	
	public OAuth1Parameters(Map<String, List<String>> parameters) {
		super(parameters);
	}
	
	/**
	 * The authorization callback url.
	 * @return The authorization callback url.
	 */
	public String getCallbackUrl() {
		return getFirst(OAUTH_CALLBACK);
	}

	/**
	 * Sets the authorization callback url.
	 * This value must be included for OAuth 1.0 providers (and NOT for OAuth 1.0a).
	 * @param callbackUrl The authorization callback url.
	 */
	public void setCallbackUrl(String callbackUrl) {
		set(OAUTH_CALLBACK, callbackUrl);
	}
	
}
