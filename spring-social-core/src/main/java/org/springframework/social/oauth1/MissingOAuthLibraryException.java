package org.springframework.social.oauth1;

import org.springframework.social.core.SocialException;

public class MissingOAuthLibraryException extends SocialException {
	public MissingOAuthLibraryException(String message) {
		super(message);
	}
}
