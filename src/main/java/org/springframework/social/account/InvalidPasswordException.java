package org.springframework.social.account;

@SuppressWarnings("serial")
public final class InvalidPasswordException extends AccountException {

	public InvalidPasswordException() {
		super("invalid password");
	}
}
