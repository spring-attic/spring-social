package org.springframework.social.oauth1;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.social.oauth.OAuthClientRequestSigner;

/**
 * Abstract implementation of {@link OAuthClientRequestSigner} that adds an
 * OAuth 1 Authorization header to the request. Concrete implementations will
 * generate the Authorization header by implementing the
 * buildAuthorizationHeader() method.
 * 
 * @author Craig Walls
 */
public abstract class OAuth1ClientRequestSigner implements OAuthClientRequestSigner {

	public void sign(HttpMethod method, HttpHeaders headers, String url, Map<String, String> bodyParameters) {
		String authorizationHeader = buildAuthorizationHeader(method, url, bodyParameters);
		if (authorizationHeader != null) {
			headers.add("Authorization", authorizationHeader);
		}
	}

	protected String decode(String encoded) {
		try {
			return URLDecoder.decode(encoded, "UTF-8");
		} catch (UnsupportedEncodingException shouldntHappen) {
			return encoded;
		}
	}

	protected abstract String buildAuthorizationHeader(HttpMethod method, String url, Map<String, String> parameters);
}
