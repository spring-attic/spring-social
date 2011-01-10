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

import java.io.Serializable;

import org.scribe.extractors.BaseStringExtractorImpl;
import org.scribe.extractors.HeaderExtractorImpl;
import org.scribe.extractors.TokenExtractorImpl;
import org.scribe.model.OAuthConfig;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.model.Verifier;
import org.scribe.oauth.OAuth10aServiceImpl;
import org.scribe.oauth.OAuthService;
import org.scribe.services.HMACSha1SignatureService;
import org.scribe.services.TimestampServiceImpl;

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

	public void connect(Serializable accountId, AuthorizedRequestToken requestToken) {
		OAuthToken accessToken = getAccessToken(requestToken);
		S serviceOperations = createServiceOperations(accessToken);
		String providerAccountId = fetchProviderAccountId(serviceOperations);
		connectionRepository.addConnection(accountId, getName(), accessToken, providerAccountId,
				buildProviderProfileUrl(providerAccountId, serviceOperations));
	}

	public void connect(Serializable accountId, String redirectUri, String code) {
		throw new IllegalStateException(
				"Connections with redirectUri and code are not supported for an OAuth 1-based service provider");
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

	// internal helpers
	protected OAuthService getOAuthService() {
		return getOAuthService(null);
	}

	protected OAuthService getOAuthService(String callbackUrl) {
		OAuthConfig config = new OAuthConfig();
		config.setRequestTokenEndpoint(parameters.getRequestTokenUrl());
		config.setAccessTokenEndpoint(parameters.getAccessTokenUrl());
		config.setAccessTokenVerb(Verb.POST);
		config.setRequestTokenVerb(Verb.POST);
		config.setApiKey(parameters.getApiKey());
		config.setApiSecret(parameters.getSecret());
		if (callbackUrl != null) {
			config.setCallback(callbackUrl);
		}
		return new OAuth10aServiceImpl(new HMACSha1SignatureService(), new TimestampServiceImpl(),
				new BaseStringExtractorImpl(), new HeaderExtractorImpl(), new TokenExtractorImpl(),
				new TokenExtractorImpl(), config);
	}

	private OAuthToken getAccessToken(AuthorizedRequestToken requestToken) {
		Token accessToken = getOAuthService().getAccessToken(
				new Token(requestToken.getValue(), requestToken.getSecret()), new Verifier(requestToken.getVerifier()));
		return new OAuthToken(accessToken.getToken(), accessToken.getSecret());
	}
}
