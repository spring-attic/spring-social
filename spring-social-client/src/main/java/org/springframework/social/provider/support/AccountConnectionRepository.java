/*
 * Copyright 2010 the original author or authors.
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
package org.springframework.social.provider.support;

import java.io.Serializable;
import java.util.Collection;

import org.springframework.social.provider.AccountConnection;
import org.springframework.social.provider.OAuthToken;


/**
 * Strategy for storing account connection information.
 * Delegated to {@link AbstractServiceProvider} to decouple the provider implementation from any physical connection store.
 * @author Keith Donald
 */
public interface AccountConnectionRepository {

	void addConnection(Serializable accountId, String provider, AccessToken accessToken, String providerAccountId,
			String providerProfileUrl);

	void updateConnection(Serializable accountId, String name, AccessToken accessToken,
			String username);

	boolean isConnected(Serializable accountId, String provider);

	boolean isConnected(Serializable accountId, String provider, String providerAccountId);

	void disconnect(Serializable accountId, String provider);

	void disconnect(Serializable accountId, String provider, String providerAccountId);

	AccessToken getAccessToken(Serializable accountId, String provider);

	AccessToken getAccessToken(Serializable accountId, String provider, String providerAccountId);

	String getProviderAccountId(Serializable accountId, String provider);

	String getRefreshToken(Serializable accountId, String provider, String providerAccountId);

	Collection<AccountConnection> getAccountConnections(Serializable accountId, String provider);
}