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
		return getTokenFromProvider(requestTokenUrl, requestTokenParameters, Collections.<String, String> emptyMap(),
				null);
	}

	public String buildAuthorizeUrl(String requestToken) {
		return authorizeUrlTemplate.expand(requestToken).toString();
	}

	public OAuthToken exchangeForAccessToken(AuthorizedRequestToken requestToken) {
		Map<String, String> accessTokenParameters = new HashMap<String, String>();
		accessTokenParameters.put("oauth_token", requestToken.getValue());
		accessTokenParameters.put("oauth_verifier", requestToken.getVerifier());
		return getTokenFromProvider(accessTokenUrl, accessTokenParameters, Collections.<String, String> emptyMap(),
				requestToken.getSecret());
	}

	// internal helpers
	
	private OAuthToken getTokenFromProvider(String tokenUrl, Map<String, String> tokenRequestParameters,
			Map<String, String> additionalParameters, String tokenSecret) {
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", getAuthorizationHeaderValue(tokenUrl, tokenRequestParameters, additionalParameters, tokenSecret));
		MultiValueMap<String, String> bodyParameters = new LinkedMultiValueMap<String, String>();
		bodyParameters.setAll(additionalParameters);
		HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(bodyParameters, headers);
		@SuppressWarnings("rawtypes")
		ResponseEntity<MultiValueMap> response = restTemplate.exchange(tokenUrl, HttpMethod.POST, request, MultiValueMap.class);
		@SuppressWarnings("unchecked")
		MultiValueMap<String, String> responseMap = response.getBody();
		return new OAuthToken(responseMap.getFirst("oauth_token"), responseMap.getFirst("oauth_token_secret"));
	}

	private String getAuthorizationHeaderValue(String tokenUrl, Map<String, String> tokenRequestParameters,
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