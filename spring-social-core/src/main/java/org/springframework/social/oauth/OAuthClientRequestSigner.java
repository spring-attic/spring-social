package org.springframework.social.oauth;

import java.util.Map;

import org.springframework.http.client.ClientHttpRequest;

/**
 * Strategy interface for signing a client request with the necessary
 * information for it to be OAuth-authenticated. Implementations of this
 * interface will vary for different versions of OAuth.
 * 
 * @author Craig Walls
 */
public interface OAuthClientRequestSigner {
	void sign(ClientHttpRequest request, Map<String, String> bodyParameters);
}
