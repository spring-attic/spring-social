/*
 * Copyright 2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.social.security.test;

import org.mockito.ArgumentMatcher;
import org.mockito.ArgumentMatchers;
import org.springframework.social.oauth1.AuthorizedRequestToken;
import org.springframework.social.oauth1.OAuthToken;
import org.springframework.social.oauth2.OAuth2Parameters;

public class ArgMatchers {
	private ArgMatchers() {

	}

	public static OAuth2Parameters oAuth2Parameters(final String redirectUri) {
		return oAuth2Parameters(redirectUri, null, null);
	}

	public static OAuth2Parameters oAuth2Parameters(final String redirectUri, final String scope, final String state) {
		return ArgumentMatchers.argThat(new ArgumentMatcher<OAuth2Parameters>() {

			public boolean matches(OAuth2Parameters params) {
				return eq(state, params.getState()) && eq(scope, params.getScope())
						&& eq(redirectUri, params.getRedirectUri());
			}

		});
	}

	public static OAuthToken oAuthToken(OAuthToken token) {
		return oAuthToken(token.getValue(), token.getSecret());
	}

	public static OAuthToken oAuthToken(final String value, final String secret) {
		return ArgumentMatchers.argThat(new ArgumentMatcher<OAuthToken>() {

			public boolean matches(OAuthToken token) {
				return eq(value, token.getValue()) && eq(secret, token.getSecret());
			}

		});
	}

	public static AuthorizedRequestToken authorizedRequestToken(OAuthToken token, String verifier) {
		return authorizedRequestToken(token.getValue(), token.getSecret(), verifier);
	}

	public static AuthorizedRequestToken authorizedRequestToken(final String value, final String secret,
			final String verifier) {
		return ArgumentMatchers.argThat(new ArgumentMatcher<AuthorizedRequestToken>() {

			public boolean matches(AuthorizedRequestToken token) {
				return eq(value, token.getValue()) && eq(secret, token.getSecret())
						&& eq(verifier, token.getVerifier());
			}

		});
	}

	private static boolean eq(Object a, Object b) {
		return (a == null) ? b == null : a.equals(b);
	}
}
