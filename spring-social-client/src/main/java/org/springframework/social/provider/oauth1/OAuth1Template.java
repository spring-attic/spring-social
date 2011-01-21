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
package org.springframework.social.provider.oauth1;

/**
 * OAuth1Operations implementation that uses REST-template to make the OAuth calls.
 * @author Keith Donald
 */
public class OAuth1Template implements OAuth1Operations {

	private final String consumerKey;
	
	private final String consumerSecret;
	
	private final String requestTokenUrl;
	
	private final String authorizeUrl;
	
	private final String accessTokenUrl;
	
	public OAuth1Template(String consumerKey, String consumerSecret, String requestTokenUrl, String authorizeUrl, String accessTokenUrl) {
		this.consumerKey = consumerKey;
		this.consumerSecret = consumerSecret;
		this.requestTokenUrl = requestTokenUrl;
		this.authorizeUrl = authorizeUrl;
		this.accessTokenUrl = accessTokenUrl;
	}

	public OAuthToken fetchNewRequestToken(String callbackUrl) {
		return null;
	}

	public String buildAuthorizeUrl(String requestToken) {
		return null;
	}

	public OAuthToken exchangeForAccessToken(AuthorizedRequestToken requestToken) {
		return null;
	}

}
