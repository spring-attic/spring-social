package org.springframework.social.twitter;

import org.springframework.social.core.SocialException;

public class InvalidMessageRecipientException extends SocialException {
	private static final long serialVersionUID = 1L;

	public InvalidMessageRecipientException(String message) {
		super(message);
	}

	public InvalidMessageRecipientException(String message, Throwable cause) {
		super(message, cause);
	}
}
