package org.springframework.social.provider.oauth2;

public interface OAuth2Operations {

	/**
	 * Construct the URL to redirect the user to for connection authorization.
	 * @param redirectUri the authorization callback url; this value must match the redirectUri registered with the provider
	 */ 
	String buildAuthorizeUrl(String redirectUri, String scope);

	/**
	 * Exchange the authorization code for an access token.
	 * @param redirectUri the authorization callback url; this value must match the redirectUri registered with the provider
	 * @param authorizationCode the authorization code returned by the provider upon user authorization
	 */
	AccessToken exchangeForAccessToken(String redirectUri, String authorizationCode);

}
