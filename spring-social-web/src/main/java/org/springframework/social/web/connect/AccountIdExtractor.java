package org.springframework.social.web.connect;

import java.io.Serializable;

import org.springframework.web.context.request.WebRequest;

/**
 * Strategy interface used by ConnectController to determine the account ID of the user for purposes of creating connections.
 * @author Craig Walls
 */
public interface AccountIdExtractor {

	/**
	 * Extracts an account ID from the web request.
	 */
	Serializable extractAccountId(WebRequest request);
}
