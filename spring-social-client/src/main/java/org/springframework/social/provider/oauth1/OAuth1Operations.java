package org.springframework.social.provider.oauth1;

public interface OAuth1Operations {

	/**
	 * Begin the account connection process by fetching a new request token from this service provider.
	 * The request token should be stored in the user's session up until the authorization callback is made and it's time to exchange it for an {@link #exchangeForAccessToken(AuthorizedRequestToken) access token}.
	 * @param callbackUrl the URL the provider should redirect to after the member authorizes the connection 
	 */
	OAuthToken fetchNewRequestToken(String callbackUrl);

	/**
	 * Construct the URL to redirect the user to for connection authorization.
	 * @param requestToken the request token value, to be encoded in the authorize URL.
	 * @return the absolute authorize URL to redirect the member to for authorization
	 */
	String buildAuthorizeUrl(String requestToken);

	/**
	 * Exchange the authorized request token for an access token.
	 * @param requestToken an authorized request token and verifier
	 * @return an access token granted by the provider
	 */
	OAuthToken exchangeForAccessToken(AuthorizedRequestToken requestToken);
	
}
