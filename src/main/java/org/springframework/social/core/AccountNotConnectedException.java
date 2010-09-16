package org.springframework.social.core;

/**
 * Indicates a security-related error while performing an operation on a social
 * network provider (updating a Twitter user's status without an OAuth access
 * token, for example).
 * 
 * @author Craig Walls
 */
public class AccountNotConnectedException extends SocialException {
	private static final long serialVersionUID = 1L;

	public AccountNotConnectedException(String message) {
		super(message);
	}

	public AccountNotConnectedException(String message, Throwable cause) {
		super(message, cause);
	}

}
