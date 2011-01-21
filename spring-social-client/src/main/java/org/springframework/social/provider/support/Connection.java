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

public class Connection {
	
	private Long id;
	
	private String accessToken;
	
	private String secret;
	
	private String refreshToken;
	
	private String accountId;
	
	private String profileUrl;

	public Connection(Long id, String accessToken, String secret, String refreshToken, String accountId, String profileUrl) {
		this.id = id;
		this.accessToken = accessToken;
		this.secret = secret;
		this.refreshToken = refreshToken;
		this.accountId = accountId;
		this.profileUrl = profileUrl;
	}

	public Long getId() {
		return id;
	}

	public String getAccessToken() {
		return accessToken;
	}

	public String getSecret() {
		return secret;
	}

	public String getRefreshToken() {
		return refreshToken;
	}

	public String getAccountId() {
		return accountId;
	}

	public String getProfileUrl() {
		return profileUrl;
	}
	
}
