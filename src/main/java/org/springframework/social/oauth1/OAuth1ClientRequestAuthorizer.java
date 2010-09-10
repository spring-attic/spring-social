package org.springframework.social.oauth1;

import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.social.oauth.AuthorizationException;
import org.springframework.social.oauth.OAuthClientRequestAuthorizer;

/**
 * Implementation of {@link OAuthClientRequestAuthorizer} that adds an OAuth 1
 * Authorization header to the request.
 * 
 * Delegates to an {@link OAuth1Template} to produce the header value.
 * 
 * @author Craig Walls
 */
public class OAuth1ClientRequestAuthorizer implements OAuthClientRequestAuthorizer {

	private final OAuth1Template oauthTemplate;

	/**
	 * An implementation of {@link OAuth1Template} that will produce the
	 * Authorization header value.
	 * 
	 * @param oauthTemplate
	 */
	public OAuth1ClientRequestAuthorizer(OAuth1Template oauthTemplate) {
		// TODO: I'm tempted to make this class abstract and have subclasses
		// implement the buildAuthorizationHeader() functionality from the
		// template...and then do away with the template interface and
		// implementations.
		this.oauthTemplate = oauthTemplate;
	}

	public ClientHttpRequest authorize(ClientHttpRequest request) throws AuthorizationException {
		try {
			Map<String, String> params = extractParametersFromRequest(request);
			String authorizationHeader = oauthTemplate.buildAuthorizationHeader(request.getMethod(), request.getURI()
					.toURL(), params);

			if (authorizationHeader != null) {
				request.getHeaders().add("Authorization", authorizationHeader);
			}
			return request;
		} catch (MalformedURLException e) {
			throw new AuthorizationException("Bad URL", e);
		}
	}

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
