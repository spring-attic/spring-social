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
package org.springframework.social.provider.oauth2;

import java.io.Serializable;

import org.springframework.social.provider.AuthorizationProtocol;
import org.springframework.social.provider.ServiceProviderConnection;
import org.springframework.social.provider.support.AbstractServiceProvider;
import org.springframework.social.provider.support.Connection;
import org.springframework.social.provider.support.ConnectionRepository;

public abstract class AbstractOAuth2ServiceProvider<S> extends AbstractServiceProvider<S> implements OAuth2ServiceProvider<S> {

	private String clientId;
	
	private String clientSecret;
	
	public AbstractOAuth2ServiceProvider(String id, String displayName, ConnectionRepository connectionRepository, String clientId, String clientSecret) {
		super(id, displayName, connectionRepository);
		this.clientId = clientId;
		this.clientSecret = clientSecret;
	}

	public AuthorizationProtocol getAuthorizationProtocol() {
		return AuthorizationProtocol.OAUTH_2;
	}

	public String buildAuthorizeUrl(String redirectUri, String scope) {
		return null;
	}

	public AccessToken exchangeForAccessToken(String redirectUri, String authorizationCode) {
		return null;
	}

	public ServiceProviderConnection<S> connect(Serializable accountId, AccessToken accessToken) {
		return null;
	}

	// subclassing hooks

	@Override
	protected S getApi(Connection connection) {
		return getApi(connection.getAccessToken());
	}

	protected abstract S getApi(String accessToken);
	
}