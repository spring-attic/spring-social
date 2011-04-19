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

/**
 * A data transfer object that allows the internal state of a ServiceProviderConnection to be persisted and transferred between layers of an application.
 * Some fields may be null depending on the specific type of ServiceProviderConnection.
 * For example, an OAuth2ServiceProviderConnection has a null 'secret' field while an OAuth1ServiceProviderConnection has null 'refreshToken' and 'expireTime' fields.
 * @author Keith Donald
 * @see ServiceProviderConnection#createData()
 */
@SuppressWarnings("serial")
public class ServiceProviderConnectionData implements Serializable {
	
	private String providerId;
	
	private String providerUserId;
	
	private String profileName;
	
	private String profileUrl;
	
	private String profilePictureUrl;
	
	private String accessToken;
	
	private String secret;
	
	private String refreshToken;
	
	private Long expireTime;

	public ServiceProviderConnectionData(String providerId, String providerUserId, String profileName, String profileUrl, String profilePictureUrl, String accessToken, String secret, String refreshToken, Long expireTime) {
		this.providerId = providerId;
		this.providerUserId = providerUserId;
		this.profileName = profileName;
		this.profileUrl = profileUrl;
		this.profilePictureUrl = profilePictureUrl;
		this.accessToken = accessToken;
		this.secret = secret;
		this.refreshToken = refreshToken;
		this.expireTime = expireTime;
	}

	/**
	 * The id of the provider the connection is associated with.
	 */
	public String getProviderId() {
		return providerId;
	}

	/**
	 * The id of the provider user this connection is connected to.
	 */
	public String getProviderUserId() {
		return providerUserId;
	}

	/**
	 * A display name for the provider user's profile.
	 */
	public String getProfileName() {
		return profileName;
	}

	/**
	 * A link to the provider's user profile page.
	 */
	public String getProfileUrl() {
		return profileUrl;
	}

	/**
	 * A link to the provider user's profile picture.
	 */
	public String getProfilePictureUrl() {
		return profilePictureUrl;
	}

	/**
	 * The access token required to make authorized API calls.
	 */
	public String getAccessToken() {
		return accessToken;
	}

	/**
	 * The secret token needed to make authorized API calls.
	 * Required for OAuth1-based connections.
	 */
	public String getSecret() {
		return secret;
	}

	/**
	 * A token use to renew this connection. Optional.
	 * Always null for OAuth1-based connections.
	 */
	public String getRefreshToken() {
		return refreshToken;
	}

	/**
	 * The time the connection expires. Optional.
	 * Always null for OAuth1-based connections.
	 */
	public Long getExpireTime() {
		return expireTime;
	}
		
}