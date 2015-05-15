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

import org.springframework.http.client.ClientHttpRequestInterceptor;

/**
 * Strategy enumeration where each value carries an interceptor defining how an access token is carried on API requests.
 * @author Craig Walls
 */
public enum TokenStrategy {

	/**
	 * Indicates that the access token should be carried in the Authorization header as an OAuth2 Bearer token.
	 */
	AUTHORIZATION_HEADER {
		public ClientHttpRequestInterceptor interceptor(String accessToken, OAuth2Version oauth2Version) {
			return new OAuth2RequestInterceptor(accessToken, oauth2Version);
		}
	},
	/**
	 * Indicates that the access token should be carried as a query parameter named "access_token".
	 */
	ACCESS_TOKEN_PARAMETER {
		public ClientHttpRequestInterceptor interceptor(String accessToken, OAuth2Version oauth2Version) {
			return new OAuth2TokenParameterRequestInterceptor(accessToken);
		}
	},
	/**
	 * Indicates that the access token should be carried as a query parameter named "oauth_token".
	 */
	OAUTH_TOKEN_PARAMETER {
		public ClientHttpRequestInterceptor interceptor(String accessToken, OAuth2Version oauth2Version) {
			return new OAuth2TokenParameterRequestInterceptor(accessToken, "oauth_token");
		}
	};
	
	abstract ClientHttpRequestInterceptor interceptor(String accessToken, OAuth2Version oauth2Version);

}
