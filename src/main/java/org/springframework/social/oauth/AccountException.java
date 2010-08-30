package org.springframework.social.oauth;

@SuppressWarnings("serial")
public abstract class AccountException extends RuntimeException {

	public AccountException(String message) {
		super(message);
	}

	public AccountException(String message, Throwable cause) {
		super(message, cause);
	}
	
}
