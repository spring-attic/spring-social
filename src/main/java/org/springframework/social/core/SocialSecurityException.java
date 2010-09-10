package org.springframework.social.core;

/**
 * Indicates a security-related error while performing an operation on a social
 * network provider (updating a Twitter user's status without an OAuth access
 * token, for example).
 * 
 * @author Craig Walls
 */
public class SocialSecurityException extends SocialException {
	private static final long serialVersionUID = 1L;

	public SocialSecurityException(String message) {
		super(message);
	}

	public SocialSecurityException(String message, Throwable cause) {
		super(message, cause);
	}

}
