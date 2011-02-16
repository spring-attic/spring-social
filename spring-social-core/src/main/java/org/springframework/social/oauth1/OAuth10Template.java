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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * OAuth10Operations implementation that uses REST-template to make the OAuth calls.
 * @author Keith Donald
 */
public class OAuth10Template extends AbstractOAuth1Template implements OAuth10Operations {

	public OAuth10Template(String consumerKey, String consumerSecret, String requestTokenUrl, String authorizeUrl, String accessTokenUrl) {
		super(consumerKey, consumerSecret, requestTokenUrl, authorizeUrl, accessTokenUrl);
	}

	public OAuthToken fetchNewRequestToken() {
		Map<String, String> requestTokenParameters = new HashMap<String, String>();
		return getTokenFromProvider(requestTokenUrl, requestTokenParameters, Collections.<String, String> emptyMap(),
				null);
	}

	public String buildAuthorizeUrl(String requestToken, String callbackUrl) {
		return authorizeUrlTemplate.expand(requestToken, callbackUrl).toString();
	}

	public OAuthToken exchangeForAccessToken(OAuthToken requestToken) {
		Map<String, String> accessTokenParameters = new HashMap<String, String>();
		accessTokenParameters.put("oauth_token", requestToken.getValue());
		return getTokenFromProvider(accessTokenUrl, accessTokenParameters, Collections.<String, String> emptyMap(),
				requestToken.getSecret());
	}
}