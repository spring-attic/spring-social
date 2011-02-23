package org.springframework.social.web.connect;

import java.io.Serializable;

public class DeferredConnectionDetails implements Serializable {
	private final String providerId;
	private final String accessToken;
	private final String accessTokenSecret;

	public DeferredConnectionDetails(String providerId, String accessToken, String accessTokenSecret) {
		this.providerId = providerId;
		this.accessToken = accessToken;
		this.accessTokenSecret = accessTokenSecret;
	}

	public String getProviderId() {
		return providerId;
	}

	public String getAccessToken() {
		return accessToken;
	}

	public String getAccessTokenSecret() {
		return accessTokenSecret;
	}
}
