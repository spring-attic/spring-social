package org.springframework.social.core;

import org.springframework.http.ResponseEntity;

/**
 * Strategy interface for converting responses from a social network provider
 * into specific instances of {@link SocialException}.
 * 
 * @author Craig Walls
 */
public interface ResponseStatusCodeTranslator {
	/**
	 * Translate responseEntity into a SocialException
	 * 
	 * @param responseEntity
	 *            The response from the social network provider
	 * 
	 * @return the exception translated from the response or <code>null</code>
	 *         if the response doesn't translate into an error.
	 */
	SocialException translate(ResponseEntity<?> responseEntity);
}
