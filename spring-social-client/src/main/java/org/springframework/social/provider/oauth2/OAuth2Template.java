package org.springframework.social.provider.oauth2;

public class OAuth2Template implements OAuth2Operations {

	private final String clientId;
	
	private final String clientSecret;
	
	public OAuth2Template(String clientId, String clientSecret) {
		this.clientId = clientId;
		this.clientSecret = clientSecret;
	}

	public String buildAuthorizeUrl(String redirectUri, String scope) {
		return null;
	}

	public AccessToken exchangeForAccessToken(String redirectUri, String authorizationCode) {
		return null;
	}
	
}
