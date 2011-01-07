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

/**
 * Base class for ServiceProvider implementations that use OAuth 2
 * authorization.
 * 
 * @author Craig Walls
 * @param <S> The service API hosted by this service provider.
 */
public abstract class AbstractOAuth2ServiceProvider<S> extends AbstractServiceProvider<S> {

	public AbstractOAuth2ServiceProvider(ServiceProviderParameters parameters,
			AccountConnectionRepository connectionRepository) {
		super(parameters, connectionRepository);
	}

	public OAuthToken fetchNewRequestToken(String callbackUrl) {
		throw new IllegalStateException("You may not fetch a request token for an OAuth 2-based service provider");
	}

	/**
	 * <p>
	 * Constructs a URL to the service provider's authorization page.
	 * </p>
	 * 
	 * <p>
	 * A typical OAuth 2 authorization URL takes 2 parameters: The client's API
	 * key and its redirect URI. Since the provider instance will already know
	 * its client API key, the caller will only need to pass in the redirect
	 * URI.
	 * </p>
	 * 
	 * @param redirectUri
	 *            the client's redirect URI
	 */
	public String buildAuthorizeUrl(String redirectUri) {
		return parameters.getAuthorizeUrl().expand(parameters.getApiKey(), redirectUri).toString();
	}

	public AuthorizationStyle getAuthorizationStyle() {
		return AuthorizationStyle.OAUTH_2;
	}
}
