package org.springframework.social.core;

/**
 * Exception class that represents a problem from performing an operation on a
 * social network provider (e.g. Twitter).
 * 
 * This exception class is abstract, as it is too generic for actual use. When a
 * SocialException is thrown, it should be one of the more specific subclasses.
 * 
 * @author Craig Walls
 */
public abstract class SocialException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public SocialException(String message) {
		super(message);
	}

	public SocialException(String message, Throwable cause) {
		super(message, cause);
	}
}
