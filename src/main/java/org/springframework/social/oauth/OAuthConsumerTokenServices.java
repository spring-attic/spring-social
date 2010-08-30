package org.springframework.social.oauth;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth.consumer.token.OAuthConsumerToken;

public interface OAuthConsumerTokenServices {
	/**
	 * Get the token for the specified protected resource.
	 * 
	 * @param resourceId
	 *            The id of the protected resource.
	 * @return The token, or null if none was found.
	 */
	OAuthConsumerToken getToken(String resourceId, Object user) throws AuthenticationException;

	/**
	 * Store a token for a specified resource.
	 * 
	 * @param resourceId
	 *            The id of the protected resource.
	 * @param token
	 *            The token to store.
	 */
	void storeToken(String resourceId, Object user, OAuthConsumerToken token);

	  /**
	 * Removes the token for the specified resource.
	 * 
	 * @param resourceId
	 *            The id of the resource.
	 */
	void removeToken(String resourceId, Object user);
}
