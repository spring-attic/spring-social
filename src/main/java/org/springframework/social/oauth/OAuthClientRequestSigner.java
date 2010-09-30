package org.springframework.social.oauth;

import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;

/**
 * Strategy interface for signing a client request with the necessary
 * information for it to be OAuth-authenticated. Implementations of this
 * interface will vary for different versions of OAuth.
 * 
 * @author Craig Walls
 */
public interface OAuthClientRequestSigner {
	void sign(HttpMethod method, HttpHeaders headers, String url, Map<String, String> bodyParameters);
}
