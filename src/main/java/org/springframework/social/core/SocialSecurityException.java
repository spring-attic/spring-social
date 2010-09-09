package org.springframework.social.core;

public class SocialSecurityException extends SocialException {
	private static final long serialVersionUID = 1L;

	public SocialSecurityException(String message) {
		super(message);
	}

	public SocialSecurityException(String message, Throwable cause) {
		super(message, cause);
	}

}
