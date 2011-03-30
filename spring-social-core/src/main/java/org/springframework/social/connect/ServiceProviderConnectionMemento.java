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
package org.springframework.social.connect;

import java.io.Serializable;

@SuppressWarnings("serial")
public class ServiceProviderConnectionMemento implements Serializable {

	private Long id;
	
	private Serializable accountId;
	
	private String providerId;
	
	private String providerAccountId;
	
	private String profileName;
	
	private String profileUrl;
	
	private String profilePictureUrl;
	
	private boolean allowSignIn;
	
	private String accessToken;
	
	private String secret;
	
	private String refreshToken;

	public ServiceProviderConnectionMemento(Long id, Serializable accountId, String providerId,
			String providerAccountId, String profileName, String profileUrl, String profilePictureUrl,
			boolean allowSignIn,
			String accessToken, String secret, String refreshToken) {
		this.id = id;
		this.accountId = accountId;
		this.providerId = providerId;
		this.providerAccountId = providerAccountId;
		this.profileName = profileName;
		this.profileUrl = profileUrl;
		this.profilePictureUrl = profilePictureUrl;
		this.allowSignIn = allowSignIn;
		this.accessToken = accessToken;
		this.secret = secret;
		this.refreshToken = refreshToken;
	}

	public Long getId() {
		return id;
	}

	public Serializable getAccountId() {
		return accountId;
	}

	public String getProviderId() {
		return providerId;
	}

	public String getProviderAccountId() {
		return providerAccountId;
	}

	public String getProfileName() {
		return profileName;
	}

	public String getProfileUrl() {
		return profileUrl;
	}

	public String getProfilePictureUrl() {
		return profilePictureUrl;
	}

	public boolean isAllowSignIn() {
		return allowSignIn;
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
	
}
