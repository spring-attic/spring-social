/*
 * Copyright 2010 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.social.provider.oauth1;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.social.provider.oauth.OAuthClientRequestSigner;

/**
 * Abstract implementation of {@link OAuthClientRequestSigner} that adds an
 * OAuth 1 Authorization header to the request. Concrete implementations will
 * generate the Authorization header by implementing the
 * buildAuthorizationHeader() method.
 * 
 * @author Craig Walls
 */
public class OAuth1ClientRequestSigner implements OAuthClientRequestSigner {

	private final String accessToken;
	private final String accessTokenSecret;
	private OAuth1Template oauth1Template;

	public OAuth1ClientRequestSigner(String apiKey, String apiSecret, String accessToken, String accessTokenSecret) {
		this.accessToken = accessToken;
		this.accessTokenSecret = accessTokenSecret;
		oauth1Template = new OAuth1Template(apiKey, apiSecret, null, "http://localhost:8080/foo", null);
	}

	public void sign(ClientHttpRequest request, Map<String, String> bodyParameters) {
		String authorizationHeader = buildAuthorizationHeader(request.getMethod(), request.getURI(),
				decodeBodyParameters(bodyParameters));
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

	protected String buildAuthorizationHeader(HttpMethod method, URI url, Map<String, String> parameters) {
		return oauth1Template.buildAuthorizationHeader(url.toString(), method, parameters, new OAuthToken(accessToken,
				accessTokenSecret));
	}

	private Map<String, String> decodeBodyParameters(Map<String, String> parameters) {
		Map<String, String> decodedParameters = new HashMap<String, String>();
		for (String key : parameters.keySet()) {
			decodedParameters.put(key, decode(parameters.get(key)));
		}
		return decodedParameters;
	}
}
