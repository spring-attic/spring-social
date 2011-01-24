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
package org.springframework.social.provider.support;

/**
 * The record of a connection between an account and a service provider.
 * Managed by a {@link ConnectionRepository}.
 * @author Keith Donald
 */
public class Connection {
	
	private final Long id;
	
	private final String accessToken;
	
	private final String secret;
	
	private final String refreshToken;
	
	/**
	 * Creates a new connection with all fields populated.
	 * Consider the static factory methods for more convenient construction options.
	 */
	public Connection(Long id, String accessToken, String secret, String refreshToken) {
		this.id = id;
		this.accessToken = accessToken;
		this.secret = secret;
		this.refreshToken = refreshToken;
	}

	/**
	 * The internal connection id assigned by the managing connection repository.
	 * Null if this connection instance is not yet persistent.
	 */
	public Long getId() {
		return id;
	}

	/**
	 * The access token.
	 */
	public String getAccessToken() {
		return accessToken;
	}

	/**
	 * The access token secret (OAuth 1 only).
	 */
	public String getSecret() {
		return secret;
	}

	/**
	 * The refresh token (OAuth 2 only).
	 */
	public String getRefreshToken() {
		return refreshToken;
	}

	/**
	 * Create a new, transient Connection instance with the oauth1 fields populated.
	 * @param accessToken the access token
	 * @param secret the access token secret
	 */
	public static Connection oauth1(String accessToken, String secret) {
		return new Connection(null, accessToken, secret, null);
	}

	/**
	 * Create a new, transient Connection instance with the oauth2 fields populated.
	 * @param accessToken the access token
	 * @param secret the access token secret
	 */
	public static Connection oauth2(String accessToken, String refreshToken) {
		return new Connection(null, accessToken, null, refreshToken);
	}
	
}