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
package org.springframework.social.oauth1;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriTemplate;

/**
 * OAuth10aOperations implementation that uses REST-template to make the OAuth calls.
 * @author Keith Donald
 */
public class OAuth1Template implements OAuth1Operations {

	protected final String consumerKey;

	protected final String consumerSecret;

	protected final String requestTokenUrl;

	protected final UriTemplate authorizeUrlTemplate;

	protected final String accessTokenUrl;

	protected final RestTemplate restTemplate = new RestTemplate();

	private final boolean oauth10a;

	/**
	 * Constructs an OAuth1Template in OAuth 1.0a mode.
	 */
	public OAuth1Template(String consumerKey, String consumerSecret, String requestTokenUrl, String authorizeUrl, String accessTokenUrl) {
		this(consumerKey, consumerSecret, requestTokenUrl, authorizeUrl, accessTokenUrl, true);
	}

	/**
	 * Constructs an OAuth1Template.
	 * @param oauth10a if true this template operates against an OAuth 1.0a provider. If false, it works in OAuth 1.0 mode.
	 */
	public OAuth1Template(String consumerKey, String consumerSecret, String requestTokenUrl, String authorizeUrl, String accessTokenUrl, boolean oauth10a) {
		this.consumerKey = consumerKey;
		this.consumerSecret = consumerSecret;
		this.requestTokenUrl = requestTokenUrl;
		this.oauth10a = oauth10a;
		this.authorizeUrlTemplate = new UriTemplate(authorizeUrl);
		this.accessTokenUrl = accessTokenUrl;
	}

	public OAuthToken fetchNewRequestToken(String callbackUrl) {
		Map<String, String> requestTokenParameters = new HashMap<String, String>();
		if (oauth10a) {
			requestTokenParameters.put("oauth_callback", callbackUrl);
		}
		return getTokenFromProvider(requestTokenUrl, requestTokenParameters, Collections.<String, String> emptyMap(), null);
	}

	public String buildAuthorizeUrl(String requestToken, String callbackUrl) {
		if (oauth10a) {
			return authorizeUrlTemplate.expand(requestToken).toString();
		} else {
			return authorizeUrlTemplate.expand(requestToken, callbackUrl).toString();
		}
	}

	public OAuthToken exchangeForAccessToken(AuthorizedRequestToken requestToken) {
		Map<String, String> accessTokenParameters = new HashMap<String, String>();
		accessTokenParameters.put("oauth_token", requestToken.getValue());
		if (oauth10a) {
			accessTokenParameters.put("oauth_verifier", requestToken.getVerifier());
		}
		return getTokenFromProvider(accessTokenUrl, accessTokenParameters, Collections.<String, String> emptyMap(),
				requestToken.getSecret());
	}

	// internal helpers

	protected OAuthToken getTokenFromProvider(String tokenUrl, Map<String, String> tokenRequestParameters,
			Map<String, String> additionalParameters, String tokenSecret) {
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization",
				getAuthorizationHeaderValue(tokenUrl, tokenRequestParameters, additionalParameters, tokenSecret));
		MultiValueMap<String, String> bodyParameters = new LinkedMultiValueMap<String, String>();
		bodyParameters.setAll(additionalParameters);
		HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(
				bodyParameters, headers);
		ResponseEntity<String> response = restTemplate.exchange(tokenUrl, HttpMethod.POST, request, String.class);
		Map<String, String> responseMap = parseResponse(response.getBody());
		return new OAuthToken(responseMap.get("oauth_token"), responseMap.get("oauth_token_secret"));
	}

	// manually parse the response instead of using a message converter.
	// The response content type could by text/plain, text/html, etc...and may not trigger the form-encoded message
	// converter
	private Map<String, String> parseResponse(String response) {
		Map<String, String> responseMap = new HashMap<String, String>();
		String[] responseEntries = response.split("&");
		for (String entry : responseEntries) {
			String[] keyValuePair = entry.trim().split("=");
			if (keyValuePair.length > 1) {
				responseMap.put(keyValuePair[0], keyValuePair[1]);
			}
		}
		return responseMap;
	}

	protected String getAuthorizationHeaderValue(String tokenUrl, Map<String, String> tokenRequestParameters,
			Map<String, String> additionalParameters, String tokenSecret) {
		Map<String, String> oauthParameters = SigningUtils.commonOAuthParameters(consumerKey);
		oauthParameters.putAll(tokenRequestParameters);
		return SigningUtils.buildAuthorizationHeaderValue(tokenUrl, oauthParameters, additionalParameters,
				HttpMethod.POST, consumerSecret, tokenSecret);
	}

	// testing hooks
	RestTemplate getRestTemplate() {
		return restTemplate;
	}

}