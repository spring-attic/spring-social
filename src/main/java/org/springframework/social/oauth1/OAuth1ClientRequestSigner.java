package org.springframework.social.oauth1;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLDecoder;
import java.util.Map;

import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpRequest;
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

	public void sign(ClientHttpRequest request, Map<String, String> bodyParameters) {
		String authorizationHeader = buildAuthorizationHeader(request.getMethod(), request.getURI(), bodyParameters);
		if (authorizationHeader != null) {
			request.getHeaders().add("Authorization", authorizationHeader);
		}
	}

	protected String decode(String encoded) {
		try {
			return URLDecoder.decode(encoded, "UTF-8");
		} catch (UnsupportedEncodingException shouldntHappen) {
			return encoded;
		}
	}

	protected abstract String buildAuthorizationHeader(HttpMethod method, URI url, Map<String, String> parameters);
}
