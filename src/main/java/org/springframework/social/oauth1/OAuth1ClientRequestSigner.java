package org.springframework.social.oauth1;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URLDecoder;
import java.util.HashMap;
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
			Map<String, String> params = new HashMap<String, String>();
			String requestUrl = request.getURI().toURL().toString();
			if (request.getHttpMethod() == HttpMethod.POST || request.getHttpMethod() == HttpMethod.PUT) {
				String[] baseAndParams = requestUrl.split("\\?");
				requestUrl = baseAndParams[0];
				if (baseAndParams.length == 2) {
					String[] paramPairs = baseAndParams[1].split("\\&");
					for (String paramPair : paramPairs) {
						String[] splitPair = paramPair.split("=");
						params.put(splitPair[0], decode(splitPair[1]));
					}
				}
			}

			String authorizationHeader = buildAuthorizationHeader(request.getHttpMethod(), requestUrl, params);

			if (authorizationHeader != null) {
				request.addHeader("Authorization", authorizationHeader);
			}
		} catch (MalformedURLException e) {
			throw new AuthorizationException("Bad URL", e);
		}
	}

	String decode(String encoded) {
		try {
			return URLDecoder.decode(encoded, "UTF-8");
		} catch (UnsupportedEncodingException shouldntHappen) {
			return encoded;
		}
	}

	protected abstract String buildAuthorizationHeader(HttpMethod method, String url, Map<String, String> parameters);
}
