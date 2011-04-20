package org.springframework.social.oauth2;

/**
 * OAuth2 supports two types of authorization flow, typically referred to as
 * "Client-side" and "Server-side".
 *
 * @author Roy Clarkson
 */
public enum GrantType {
	/**
	 * AuthorizationCode denotes the server-side authorization flow, and is
	 * associated with the response_type=code parameter value
	 */
	AuthorizationCode,

	/**
	 * ImplicitGrant denotes the client-side authorization flow and is
	 * associated with the response_type=token parameter value
	 */
	ImplicitGrant
}
