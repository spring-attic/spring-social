package org.springframework.social.oauth;

import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpRequest;

public class OAuth1ClientRequestAuthorizer implements OAuthClientRequestAuthorizer {

	private final OAuthTemplate oauthTemplate;

	public OAuth1ClientRequestAuthorizer(OAuthTemplate oauthTemplate) {
		this.oauthTemplate = oauthTemplate;
	}

	@Override
	public ClientHttpRequest authorize(ClientHttpRequest request) throws AuthorizationException {
		try {
			Map<String, String> params = extractParametersFromRequest(request);
			request.getHeaders().add("Authorization",
					oauthTemplate.buildAuthorizationHeader(request.getMethod(), request.getURI().toURL(), params));
			return request;
		} catch (MalformedURLException e) {
			throw new AuthorizationException("Bad URL", e);
		}
	}

	Map<String, String> extractParametersFromRequest(ClientHttpRequest request) {
		if (request.getMethod().equals(HttpMethod.POST) || request.getMethod().equals(HttpMethod.PUT)) {
			return extractFormParameters(request);
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

	private Map<String, String> extractFormParameters(ClientHttpRequest request) {
		// TODO: Figure out how to get parameters from a POST
		return new HashMap<String, String>();
	}

}
