package org.springframework.social.provider.support;

public class ConnectionToken {
	private final String accessToken;
	private final String accessTokenSecret;
	private final String refreshToken;

	public ConnectionToken(String accessToken, String accessTokenSecret, String refreshToken) {
		this.accessToken = accessToken;
		this.accessTokenSecret = accessTokenSecret;
		this.refreshToken = refreshToken;
	}

	public String getAccessToken() {
		return accessToken;
	}

	public String getAccessTokenSecret() {
		return accessTokenSecret;
	}

	public String getRefreshToken() {
		return refreshToken;
	}
}
