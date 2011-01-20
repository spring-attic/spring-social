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
package org.springframework.social.config.xml;

import java.io.Serializable;
import java.util.Collection;

import org.springframework.social.provider.AccountConnection;
import org.springframework.social.provider.OAuthToken;
import org.springframework.social.provider.support.AccountConnectionRepository;

public class TestConnectionRepository implements AccountConnectionRepository {
	public void addConnection(Serializable accountId, String provider, OAuthToken accessToken,
			String providerAccountId, String providerProfileUrl) {
	}

	public void updateConnection(Serializable accountId, String name, OAuthToken accessToken,
			String username) {
	}

	public boolean isConnected(Serializable accountId, String provider) {
		return false;
	}

	public boolean isConnected(Serializable accountId, String provider, String providerAccountId) {
		return false;
	}

	public void disconnect(Serializable accountId, String provider) {
	}

	public void disconnect(Serializable accountId, String provider, String providerAccountId) {
	}

	public OAuthToken getAccessToken(Serializable accountId, String provider) {
		return null;
	}

	public OAuthToken getAccessToken(Serializable accountId, String provider, String providerAccountId) {
		return null;
	}

	public String getProviderAccountId(Serializable accountId, String provider) {
		return null;
	}

	public String getRefreshToken(Serializable accountId, String provider, String providerAccountId) {
		return null;
	}

	public Collection<AccountConnection> getAccountConnections(Serializable accountId, String provider) {
		return null;
	}
}
