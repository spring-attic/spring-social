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
package org.springframework.social.oauth2;

import java.io.Serializable;
import java.util.Map;

/**
 * OAuth2 access token.
 * @author Keith Donald
 */
@SuppressWarnings("serial")
public final class AccessGrant implements Serializable {

	private final String accessToken;
	
	private final String refreshToken;
	
	private final Long expireTime;

	private final String scope;

	private final Map<String, Object> additionalParameters;

	public AccessGrant(String accessToken, String refreshToken, Long expireTime, String scope, Map<String, Object> additionalParameters) {
		this.accessToken = accessToken;
		this.refreshToken = refreshToken;
		this.expireTime = expireTime;
		this.scope = scope;
		this.additionalParameters = additionalParameters;
	}

	/**
	 * The access token value.
	 */
	public String getAccessToken() {
		return accessToken;
	}

	/**
	 * The refresh token that can be used to renew the access token.
	 */
	public String getRefreshToken() {
		return refreshToken;
	}

	/**
	 * The time (in milliseconds since Jan 1, 1970 UTC) when this access grant will expire.
	 * May be null if the token is non-expiring.
	 */
	public Long getExpireTime() {
		return expireTime;
	}
	
	/**
	 * The scope of the access grant.
	 * May be null if the provider doesn't return the granted scope in the response.
	 */
	public String getScope() {
		return scope;
	}
	
	/**
	 * Additional parameters returned along with the access grant.
	 */
	public Map<String, Object> getAdditionalParameters() {
		return additionalParameters;
	}

}
