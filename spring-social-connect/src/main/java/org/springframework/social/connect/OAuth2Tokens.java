package org.springframework.social.connect;

public final class OAuth2Tokens {
	private final OAuthToken accessToken;
	private final String refreshToken;

	public OAuth2Tokens(OAuthToken accessToken, String refreshToken) {
		this.accessToken = accessToken;
		this.refreshToken = refreshToken;
	}

	public OAuthToken getAccessToken() {
		return accessToken;
	}

	public String getRefreshToken() {
		return refreshToken;
	}
}
