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

import java.io.Serializable;

/**
 * OAuth2 access token.
 * @author Keith Donald
 */
@SuppressWarnings("serial")
public class AccessGrant implements Serializable {

	private final String accessToken;

	private final String scope;

	private final String refreshToken;
	
	private final Long expireTime;

	public AccessGrant(String accessToken) {
		this(accessToken, null, null, null);
	}

	public AccessGrant(String accessToken, String scope, String refreshToken, Long expiresIn) {
		this.accessToken = accessToken;
		this.scope = scope;
		this.refreshToken = refreshToken;
		this.expireTime = expiresIn != null ? System.currentTimeMillis() + expiresIn * 1000l : null;
	}

	/**
	 * The access token value.
	 * @return The access token value.
	 */
	public String getAccessToken() {
		return accessToken;
	}

	/**
	 * The scope of the access grant.
	 * May be null if the provider doesn't return the granted scope in the response.
	 * @return The scope of the access grant.
	 */
	public String getScope() {
		return scope;
	}

	/**
	 * The refresh token that can be used to renew the access token.
	 * May be null if the provider does not support refresh tokens.
	 * @return The refresh token that can be used to renew the access token.
	 */
	public String getRefreshToken() {
		return refreshToken;
	}

	/**
	 * The time (in milliseconds since Jan 1, 1970 UTC) when this access grant will expire.
	 * May be null if the token is non-expiring.
	 * @return The time (in milliseconds since Jan 1, 1970 UTC) when this access grant will expire.
	 */
	public Long getExpireTime() {
		return expireTime;
	}
	
}
