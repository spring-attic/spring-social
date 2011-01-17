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
package org.springframework.social.client.connect;

import java.io.Serializable;

/**
 * Represents a connection between an application and a social provider (such as
 * Twitter or Facebook).
 * 
 * @author Craig Walls
 */
public class AccountConnection {
	private final Serializable accountId;
	private final String provider;
	private final OAuthToken accessToken;
	private final String refreshToken;
	private final String providerAccountId;
	private final String providerProfileUrl;

	public AccountConnection(Serializable accountId, String provider, OAuthToken accessToken, String refreshToken,
			String providerAccountId, String providerProfileUrl) {
		this.accountId = accountId;
		this.provider = provider;
		this.accessToken = accessToken;
		this.refreshToken = refreshToken;
		this.providerAccountId = providerAccountId;
		this.providerProfileUrl = providerProfileUrl;
	}

	public Serializable getAccountId() {
		return accountId;
	}

	public String getProvider() {
		return provider;
	}

	public OAuthToken getAccessToken() {
		return accessToken;
	}

	public String getRefreshToken() {
		return refreshToken;
	}

	public String getProviderAccountId() {
		return providerAccountId;
	}

	public String getProviderProfileUrl() {
		return providerProfileUrl;
	}
}
