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
package org.springframework.social.connect;

import java.io.Serializable;

import org.springframework.social.connect.support.OAuth1Connection;
import org.springframework.social.connect.support.OAuth2Connection;

/**
 * A data transfer object that allows the internal state of a Connection to be persisted and transferred between layers of an application.
 * Some fields may be null depending on the specific type of {@link Connection}.
 * For example, an {@link OAuth2Connection} has a null 'secret' field while an {@link OAuth1Connection} has null 'refreshToken' and 'expireTime' fields.
 * @author Keith Donald
 * @see Connection#createData()
 */
@SuppressWarnings("serial")
public class ConnectionData implements Serializable {
	
	private final String providerId;
	
	private final String providerUserId;
	
	private final String displayName;
	
	private final String profileUrl;
	
	private final String imageUrl;
	
	private final String accessToken;
	
	private final String secret;
	
	private final String refreshToken;
	
	private final Long expireTime;

	public ConnectionData(String providerId, String providerUserId, String displayName, String profileUrl, String imageUrl, String accessToken, String secret, String refreshToken, Long expireTime) {
		this.providerId = providerId;
		this.providerUserId = providerUserId;
		this.displayName = displayName;
		this.profileUrl = profileUrl;
		this.imageUrl = imageUrl;
		this.accessToken = accessToken;
		this.secret = secret;
		this.refreshToken = refreshToken;
		this.expireTime = expireTime;
	}

	/**
	 * The id of the provider the connection is associated with.
	 * @return The id of the provider the connection is associated with.
	 */
	public String getProviderId() {
		return providerId;
	}

	/**
	 * The id of the provider user this connection is connected to.
	 * @return The id of the provider user this connection is connected to.
	 */
	public String getProviderUserId() {
		return providerUserId;
	}

	/**
	 * A display name for the connection.
	 * @return A display name for the connection.
	 */
	public String getDisplayName() {
		return displayName;
	}

	/**
	 * A link to the provider's user profile page.
	 * @return A link to the provider's user profile page.
	 */
	public String getProfileUrl() {
		return profileUrl;
	}

	/**
	 * An image visualizing the connection.
	 * @return An image visualizing the connection.
	 */
	public String getImageUrl() {
		return imageUrl;
	}

	/**
	 * The access token required to make authorized API calls.
	 * @return The access token required to make authorized API calls.
	 */
	public String getAccessToken() {
		return accessToken;
	}

	/**
	 * The secret token needed to make authorized API calls.
	 * Required for OAuth1-based connections.
	 * @return The secret token needed to make authorized API calls.
	 */
	public String getSecret() {
		return secret;
	}

	/**
	 * A token use to renew this connection. Optional.
	 * Always null for OAuth1-based connections.
	 * @return A token use to renew this connection. Optional.
	 */
	public String getRefreshToken() {
		return refreshToken;
	}

	/**
	 * The time the connection expires. Optional.
	 * Always null for OAuth1-based connections.
	 * @return The time the connection expires. Optional.
	 */
	public Long getExpireTime() {
		return expireTime;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((accessToken == null) ? 0 : accessToken.hashCode());
		result = prime * result
				+ ((displayName == null) ? 0 : displayName.hashCode());
		result = prime * result
				+ ((expireTime == null) ? 0 : expireTime.hashCode());
		result = prime * result
				+ ((imageUrl == null) ? 0 : imageUrl.hashCode());
		result = prime * result
				+ ((profileUrl == null) ? 0 : profileUrl.hashCode());
		result = prime * result
				+ ((providerId == null) ? 0 : providerId.hashCode());
		result = prime * result
				+ ((providerUserId == null) ? 0 : providerUserId.hashCode());
		result = prime * result
				+ ((refreshToken == null) ? 0 : refreshToken.hashCode());
		result = prime * result + ((secret == null) ? 0 : secret.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof ConnectionData))
			return false;
		ConnectionData other = (ConnectionData) obj;
		if (accessToken == null) {
			if (other.accessToken != null)
				return false;
		} else if (!accessToken.equals(other.accessToken))
			return false;
		if (displayName == null) {
			if (other.displayName != null)
				return false;
		} else if (!displayName.equals(other.displayName))
			return false;
		if (expireTime == null) {
			if (other.expireTime != null)
				return false;
		} else if (!expireTime.equals(other.expireTime))
			return false;
		if (imageUrl == null) {
			if (other.imageUrl != null)
				return false;
		} else if (!imageUrl.equals(other.imageUrl))
			return false;
		if (profileUrl == null) {
			if (other.profileUrl != null)
				return false;
		} else if (!profileUrl.equals(other.profileUrl))
			return false;
		if (providerId == null) {
			if (other.providerId != null)
				return false;
		} else if (!providerId.equals(other.providerId))
			return false;
		if (providerUserId == null) {
			if (other.providerUserId != null)
				return false;
		} else if (!providerUserId.equals(other.providerUserId))
			return false;
		if (refreshToken == null) {
			if (other.refreshToken != null)
				return false;
		} else if (!refreshToken.equals(other.refreshToken))
			return false;
		if (secret == null) {
			if (other.secret != null)
				return false;
		} else if (!secret.equals(other.secret))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "ConnectionData [providerId=" + providerId + ", providerUserId="
				+ providerUserId + ", displayName=" + displayName
				+ ", profileUrl=" + profileUrl + ", imageUrl=" + imageUrl
				+ ", accessToken=" + accessToken + ", secret=" + secret
				+ ", refreshToken=" + refreshToken + ", expireTime="
				+ expireTime + "]";
	}

}
