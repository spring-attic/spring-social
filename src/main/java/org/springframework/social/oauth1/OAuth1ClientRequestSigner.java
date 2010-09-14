package org.springframework.social.oauth1;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

import org.springframework.http.HttpMethod;
import org.springframework.social.oauth.AuthorizationException;
import org.springframework.social.oauth.ClientRequest;
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

	public void sign(ClientRequest request) throws AuthorizationException {
		try {
			Map<String, String> params = request.getQueryParameters();
			String authorizationHeader = buildAuthorizationHeader(request.getHttpMethod(), request.getURI()
					.toURL(), params);

			if (authorizationHeader != null) {
				request.addHeader("Authorization", authorizationHeader);
			}
		} catch (MalformedURLException e) {
			throw new AuthorizationException("Bad URL", e);
		}
	}

	protected abstract String buildAuthorizationHeader(HttpMethod method, URL url, Map<String, String> parameters);
}
