/*
 * Copyright 2011 the original author or authors.
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
package org.springframework.security.oauth.client.oauth1;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.security.oauth.client.ClientRequest;
import org.springframework.security.oauth.client.RestTemplateInterceptor;

/**
 * ClientRequestInterceptor implementation that performs OAuth1 request signing before the request is executed.
 * @author Keith Donald
 */
public class OAuth1ClientRequestInterceptor implements RestTemplateInterceptor {

	private final OAuthToken accessToken;
	private final String consumerKey;
	private final String consumerSecret;

	/**
	 * Creates a OAuth 1.0 client request interceptor.
	 * @param accessToken the access token and secret
	 */
	public OAuth1ClientRequestInterceptor(String consumerKey, String consumerSecret, OAuthToken accessToken) {
		this.consumerKey = consumerKey;
		this.consumerSecret = consumerSecret;
		this.accessToken = accessToken;
	}

	public void beforeExecution(ClientRequest request) {
		Map<String, String> parameters = extractBodyParameters(request.getHeaders().getContentType(), request.getBody());
		String authorizationHeader = OAuth1SigningUtils.buildAuthorizationHeader(request.getUri().toString(),
				request.getMethod(), parameters, consumerKey,
				consumerSecret, accessToken);
		request.getHeaders().set("Authorization", authorizationHeader);
	}
	
	private Map<String, String> extractBodyParameters(MediaType bodyType, byte[] bodyBytes) {
		Map<String, String> params = new HashMap<String, String>();

		if (bodyType != null && bodyType.equals(MediaType.APPLICATION_FORM_URLENCODED)) {
			String[] paramPairs = new String(bodyBytes).split("&");
			for (String pair : paramPairs) {
				String[] keyValue = pair.split("=");
				if (keyValue.length == 2) {
					// TODO: Determine if this decode step is necessary, since
					// it's just going to be encoded again later
					params.put(keyValue[0], decode(keyValue[1]));
				}
			}
		}
		return params;
	}

	protected String decode(String encoded) {
		try {
			return URLDecoder.decode(encoded, "UTF-8");
		} catch (UnsupportedEncodingException shouldntHappen) {
			return encoded;
		}
	}
}
