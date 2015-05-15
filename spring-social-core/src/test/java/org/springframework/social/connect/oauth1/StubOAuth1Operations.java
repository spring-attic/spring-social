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
package org.springframework.social.connect.oauth1;

import org.springframework.social.oauth1.AuthorizedRequestToken;
import org.springframework.social.oauth1.OAuth1Operations;
import org.springframework.social.oauth1.OAuth1Parameters;
import org.springframework.social.oauth1.OAuth1Version;
import org.springframework.social.oauth1.OAuthToken;
import org.springframework.util.MultiValueMap;

class StubOAuth1Operations implements OAuth1Operations {

	public OAuth1Version getVersion() {
		return OAuth1Version.CORE_10_REVISION_A;
	}
	
	public OAuthToken fetchRequestToken(String callbackUrl, MultiValueMap<String, String> additionalParameters) {
		return new OAuthToken("12345", "23456");
	}

	public String buildAuthorizeUrl(String requestToken, OAuth1Parameters parameters) {
		return "http://springsource.org/oauth/authorize?request_token=" + requestToken;
	}

	public String buildAuthenticateUrl(String requestToken, OAuth1Parameters parameters) {
		return "http://springsource.org/oauth/authenticate?request_token=" + requestToken;
	}

	public OAuthToken exchangeForAccessToken(AuthorizedRequestToken requestToken, MultiValueMap<String, String> additionalParameters) {
		return new OAuthToken("34567", "45678");
	}
	
}
