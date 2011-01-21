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

import java.io.Serializable;

import org.springframework.social.provider.AuthorizationProtocol;
import org.springframework.social.provider.ServiceProviderConnection;
import org.springframework.social.provider.support.AbstractServiceProvider;
import org.springframework.social.provider.support.Connection;
import org.springframework.social.provider.support.ConnectionRepository;

public abstract class AbstractOAuth1ServiceProvider<S> extends AbstractServiceProvider<S> implements OAuth1ServiceProvider<S> {

	private final String consumerKey;
	
	private final String consumerSecret;
	
	private final String requestTokenUrl;
	
	private final String authorizeUrl;
	
	private final String accessTokenUrl;
	
	public AbstractOAuth1ServiceProvider(String id, String displayName, ConnectionRepository connectionRepository, String consumerKey, String consumerSecret,
			String  requestTokenUrl, String authorizeUrl, String accessTokenUrl) {
		super(id, displayName, connectionRepository);
		this.consumerKey = consumerKey;
		this.consumerSecret = consumerSecret;
		this.requestTokenUrl = requestTokenUrl;
		this.authorizeUrl = authorizeUrl;
		this.accessTokenUrl = accessTokenUrl;
	}

	public AuthorizationProtocol getAuthorizationProtocol() {
		return AuthorizationProtocol.OAUTH_1;
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

	public ServiceProviderConnection<S> connect(Serializable accountId, OAuthToken accessToken) {
		return null;
	}

	// subclassing hooks
	
	protected String getConsumerKey() {
		return consumerKey;
	}
	
	protected String getConsumerSecret() {
		return consumerSecret;
	}
	
	@Override
	protected final S getApi(Connection connection) {
		return getApi(connection.getAccessToken(), connection.getSecret());
	}

	protected abstract S getApi(String accessToken, String secret);
	
}