package org.springframework.social.twitter;

import org.springframework.social.core.ForbiddenSocialOperationException;

/**
 * Exception thrown when a duplicate tweet is posted.
 * 
 * @author Craig Walls
 */
public class DuplicateTweetException extends ForbiddenSocialOperationException {
	private static final long serialVersionUID = 1L;

	public DuplicateTweetException(String message) {
		super(message);
	}

	public DuplicateTweetException(String message, Throwable cause) {
		super(message, cause);
	}
}
