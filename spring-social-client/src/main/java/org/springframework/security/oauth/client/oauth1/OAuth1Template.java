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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriTemplate;

/**
 * OAuth1Operations implementation that uses REST-template to make the OAuth calls.
 * @author Keith Donald
 */
public class OAuth1Template implements OAuth1Operations {

	private final String consumerKey;
	
	private final String consumerSecret;
	
	private final String requestTokenUrl;
	
	private final UriTemplate authorizeUrlTemplate;
	
	private final String accessTokenUrl;
	
	private final RestTemplate restTemplate = new RestTemplate();

	public OAuth1Template(String consumerKey, String consumerSecret, String requestTokenUrl, String authorizeUrl, String accessTokenUrl) {
		this.consumerKey = consumerKey;
		this.consumerSecret = consumerSecret;
		this.requestTokenUrl = requestTokenUrl;
		this.authorizeUrlTemplate = new UriTemplate(authorizeUrl);
		this.accessTokenUrl = accessTokenUrl;
	}

	public OAuthToken fetchNewRequestToken(String callbackUrl) {
		Map<String, String> requestTokenParameters = new HashMap<String, String>();
		requestTokenParameters.put("oauth_callback", callbackUrl);
		return getTokenFromProvider(requestTokenParameters, requestTokenUrl, null);
	}

	public String buildAuthorizeUrl(String requestToken) {
		return authorizeUrlTemplate.expand(requestToken).toString();
	}

	public OAuthToken exchangeForAccessToken(AuthorizedRequestToken requestToken) {
		Map<String, String> accessTokenParameters = new HashMap<String, String>();
		accessTokenParameters.put("oauth_token", requestToken.getValue());
		accessTokenParameters.put("oauth_verifier", requestToken.getVerifier());
		return getTokenFromProvider(accessTokenParameters, accessTokenUrl, requestToken.getSecret());
	}

	// internal helpers
	
	private OAuthToken getTokenFromProvider(Map<String, String> tokenRequestParameters, String tokenUrl, String tokenSecret) {
		Map<String, String> oauthParameters = SigningUtils.getCommonOAuthParameters(consumerKey);
		oauthParameters.putAll(tokenRequestParameters);
		String authHeader = SigningUtils.buildAuthorizationHeader(tokenUrl, oauthParameters,
				Collections.<String, String> emptyMap(), HttpMethod.POST, consumerSecret, tokenSecret);
		MultiValueMap<String, String> headers = new LinkedMultiValueMap<String, String>();
		headers.add("Authorization", authHeader);
		HttpEntity<String> request = new HttpEntity<String>(headers);
		ResponseEntity<String> response = getRestOperations().exchange(tokenUrl, HttpMethod.POST, request, String.class);
		Map<String, String> responseMap = parseResponse(response.getBody());
		return new OAuthToken(responseMap.get("oauth_token"), responseMap.get("oauth_token_secret"));
	}

	private Map<String, String> parseResponse(String response) {
		Map<String, String> responseMap = new HashMap<String, String>();
		String[] responseEntries = response.split("&");
		for (String entry : responseEntries) {
			String[] keyValuePair = entry.split("=");
			if (keyValuePair.length > 1) {
				responseMap.put(keyValuePair[0], keyValuePair[1]);
			}
		}
		return responseMap;
	}

	// subclassing hooks
	
	protected RestOperations getRestOperations() {
		return restTemplate;
	}
}