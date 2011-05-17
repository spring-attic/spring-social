package org.springframework.social.security;

import org.springframework.security.core.Authentication;

/**
 * Strategy interface used by {@link SocialAuthenticationFilter} to determine
 * the account ID of the user for purposes of creating connections.
 * 
 * @author stf@molindo.at
 */
public interface UserIdExtractor {

	/**
	 * Extracts an account ID from current {@link Authentication}.
	 */
	String extractUserId(Authentication authentication);

}
