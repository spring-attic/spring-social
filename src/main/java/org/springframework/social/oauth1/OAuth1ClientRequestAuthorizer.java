package org.springframework.social.oauth1;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.social.oauth.AuthorizationException;
import org.springframework.social.oauth.OAuthClientRequestAuthorizer;

/**
 * Abstract implementation of {@link OAuthClientRequestAuthorizer} that adds an
 * OAuth 1 Authorization header to the request. Concrete implementations will
 * generate the Authorization header by implementing the
 * buildAuthorizationHeader() method.
 * 
 * @author Craig Walls
 */
public abstract class OAuth1ClientRequestAuthorizer implements OAuthClientRequestAuthorizer {

	public ClientHttpRequest authorize(ClientHttpRequest request) throws AuthorizationException {
		try {
			Map<String, String> params = extractParametersFromRequest(request);
			String authorizationHeader = buildAuthorizationHeader(request.getMethod(), request.getURI()
					.toURL(), params);

			if (authorizationHeader != null) {
				request.getHeaders().add("Authorization", authorizationHeader);
			}
			return request;
		} catch (MalformedURLException e) {
			throw new AuthorizationException("Bad URL", e);
		}
	}

	protected abstract String buildAuthorizationHeader(HttpMethod method, URL url, Map<String, String> parameters);

	Map<String, String> extractParametersFromRequest(ClientHttpRequest request) {
		if (request.getMethod().equals(HttpMethod.POST) || request.getMethod().equals(HttpMethod.PUT)) {
			return new HashMap<String, String>();
		} else {
			return extractQueryParameters(request);
		}
	}

	private Map<String, String> extractQueryParameters(ClientHttpRequest request) {
		HashMap<String, String> paramMap = new HashMap<String, String>();

		String queryString = request.getURI().getQuery();
		if (queryString != null) {
			String[] paramPairs = queryString.split("[\\?|\\&]");

			for (String paramPair : paramPairs) {
				String[] split = paramPair.split("\\=");
				String value = split.length > 1 ? split[1] : null;
				paramMap.put(split[0], value);
			}
		}

		return paramMap;
	}
}
