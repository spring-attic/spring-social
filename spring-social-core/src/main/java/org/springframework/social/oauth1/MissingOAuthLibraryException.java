package org.springframework.social.oauth1;

import org.springframework.social.core.SocialException;

public class MissingOAuthLibraryException extends SocialException {
    private static final long serialVersionUID = 1L;
	
	public MissingOAuthLibraryException(String message) {
		super(message);
	}
}
