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
package org.springframework.social.connect;

import org.scribe.model.Token;

/**
 * Base class for ServiceProvider implementations that use OAuth 1
 * authorization.
 * 
 * @author Craig Walls
 * @param <S> The service API hosted by this service provider.
 */
public abstract class AbstractOAuth1ServiceProvider<S> extends AbstractServiceProvider<S> {

	public AbstractOAuth1ServiceProvider(ServiceProviderParameters parameters,
			AccountConnectionRepository connectionRepository) {
		super(parameters, connectionRepository);
	}

	public OAuthToken fetchNewRequestToken(String callbackUrl) {
		Token requestToken = getOAuthService(callbackUrl).getRequestToken();
		return new OAuthToken(requestToken.getToken(), requestToken.getSecret());
	}

	/**
	 * Constructs a URL to the provider's authorization page. A typical OAuth 1
	 * authorization URL takes the value of the request token as a parameter.
	 * 
	 * @param requestToken
	 *            the request token received earlier in the authorization flow.
	 */
	public String buildAuthorizeUrl(String requestToken) {
		return parameters.getAuthorizeUrl().expand(requestToken).toString();
	}

	public AuthorizationStyle getAuthorizationStyle() {
		return AuthorizationStyle.OAUTH_1;
	}
}
