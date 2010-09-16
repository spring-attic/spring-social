package org.springframework.social.twitter;

import org.springframework.social.core.OperationNotPermittedException;

/**
 * Exception thrown when a duplicate tweet is posted.
 * 
 * @author Craig Walls
 */
public class DuplicateTweetException extends OperationNotPermittedException {
	private static final long serialVersionUID = 1L;

	public DuplicateTweetException(String message) {
		super(message);
	}

	public DuplicateTweetException(String message, Throwable cause) {
		super(message, cause);
	}
}
