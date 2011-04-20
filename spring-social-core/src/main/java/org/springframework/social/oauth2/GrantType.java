package org.springframework.social.oauth2;

/**
 * OAuth2 supports two types of authorization flow, typically referred to as
 * "Client-side" and "Server-side".
 *
 * @author Roy Clarkson
 */
public enum GrantType {
	/**
	 * AUTHORIZATION_CODE denotes the server-side authorization flow, and is
	 * associated with the response_type=code parameter value
	 */
	AUTHORIZATION_CODE,

	/**
	 * IMPLICIT_GRANT denotes the client-side authorization flow and is
	 * associated with the response_type=token parameter value
	 */
	IMPLICIT_GRANT
}
