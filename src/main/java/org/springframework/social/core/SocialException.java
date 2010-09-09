package org.springframework.social.core;

public class SocialException extends Exception {
	private static final long serialVersionUID = 1L;

	public SocialException(String message) {
		super(message);
	}

	public SocialException(String message, Throwable cause) {
		super(message, cause);
	}
}
