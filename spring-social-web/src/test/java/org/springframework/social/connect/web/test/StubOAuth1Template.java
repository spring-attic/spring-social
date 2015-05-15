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
package org.springframework.social.connect.web.test;

import static org.springframework.http.HttpStatus.*;
import static org.springframework.social.connect.web.test.StubOAuthTemplateBehavior.*;

import org.springframework.social.oauth1.AuthorizedRequestToken;
import org.springframework.social.oauth1.OAuth1Template;
import org.springframework.social.oauth1.OAuthToken;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;

public class StubOAuth1Template extends OAuth1Template {

	private final StubOAuthTemplateBehavior behavior;

	public StubOAuth1Template(String consumerKey, String consumerSecret, String requestTokenUrl, String authorizeUrl, String accessTokenUrl, StubOAuthTemplateBehavior behavior) {
		super(consumerKey, consumerSecret, requestTokenUrl, authorizeUrl, accessTokenUrl);
		this.behavior = behavior;
	}
	
	@Override
	public OAuthToken fetchRequestToken(String callbackUrl, MultiValueMap<String, String> additionalParameters) {
		
		if (behavior == THROW_EXCEPTION) {
			throw new HttpClientErrorException(BAD_REQUEST);
		}
		
		return new OAuthToken("requestToken", "requestTokenSecret");
	}
	
	@Override
	public OAuthToken exchangeForAccessToken(AuthorizedRequestToken requestToken, MultiValueMap<String, String> additionalParameters) {
		if (behavior == THROW_EXCEPTION) {
			throw new HttpClientErrorException(BAD_REQUEST);
		}
		return new OAuthToken("accessToken", "accessTokenSecret");
	}
}
