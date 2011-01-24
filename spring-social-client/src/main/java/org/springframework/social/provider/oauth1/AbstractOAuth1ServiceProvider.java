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

/**
 * Base class for ServiceProviders that use the OAuth1 protocol.
 * OAuth1-based ServiceProvider implementations should extend and implement {@link #getApi(String, String, String, String)}.
 * @author Keith Donald
 * @param <S> the service API type
 */
public abstract class AbstractOAuth1ServiceProvider<S> extends AbstractServiceProvider<S> implements OAuth1ServiceProvider<S> {

	private final String consumerKey;
	
	private final String consumerSecret;
	
	private final OAuth1Operations oauth1Operations;
	
	public AbstractOAuth1ServiceProvider(String id, ConnectionRepository connectionRepository, 
			String consumerKey, String consumerSecret, OAuth1Operations oauth1Operations) {
		super(id, connectionRepository);
		this.consumerKey = consumerKey;
		this.consumerSecret = consumerSecret;
		this.oauth1Operations = oauth1Operations;
	}

	public AuthorizationProtocol getAuthorizationProtocol() {
		return AuthorizationProtocol.OAUTH_1;
	}

	public OAuth1Operations getOAuth1Operations() {
		return oauth1Operations;
	}
	
	public ServiceProviderConnection<S> connect(Serializable accountId, OAuthToken accessToken) {
		return connect(accountId, Connection.oauth1(accessToken.getValue(), accessToken.getSecret()));
	}

	@Override
	protected final S getApi(Connection connection) {
		return getApi(consumerKey, consumerSecret, connection.getAccessToken(), connection.getSecret());
	}

	// subclassing hooks
	
	/**
	 * Construct the ServiceProvider's API, secured by OAuth1 and to be invoked by the client application on behalf of a user.
	 * OAuth-1 based ServiceProvider implementors should override to construct their specific API interface implementation e.g. TwitterTemplate.
	 * @param consumerKey the client key assigned by the provider 
	 * @param consumerSecret the client secret assigned by the provider
	 * @param accessToken the user's access token granted by the provider upon connection authorization
	 * @param secret the access token secret granted by the provider upon connection authorization
	 * @return the service API
	 */
	protected abstract S getApi(String consumerKey, String consumerSecret, String accessToken, String secret);
	
}