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
package org.springframework.social.provider.oauth2;

import java.util.HashMap;
import java.util.Map;

import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriTemplate;

/**
 * OAuth2Operations implementation that uses REST-template to make the OAuth calls.
 * @author Keith Donald
 */
public class OAuth2Template implements OAuth2Operations {

	private final String clientId;
	
	private final String clientSecret;

	private final String accessTokenUrl;

	private final UriTemplate authorizeUrlTemplate;
	
	public OAuth2Template(String clientId, String clientSecret, String authorizeUrl, String accessTokenUrl) {
		this.clientId = clientId;
		this.clientSecret = clientSecret;
		this.authorizeUrlTemplate = authorizeUrl != null ? new UriTemplate(authorizeUrl) : null;
		this.accessTokenUrl = accessTokenUrl;
	}

	public String buildAuthorizeUrl(String redirectUri, String scope) {
		Map<String, String> urlVariables = new HashMap<String, String>();
		urlVariables.put("clientId", clientId);
		urlVariables.put("redirectUri", redirectUri);
		urlVariables.put("scope", scope);
		return authorizeUrlTemplate.expand(urlVariables).toString();
	}

	public AccessToken exchangeForAccessToken(String redirectUri, String authorizationCode) {
		Map<String, String> requestParameters = new HashMap<String, String>();
		requestParameters.put("client_id", clientId);
		requestParameters.put("client_secret", clientSecret);
		requestParameters.put("code", authorizationCode);
		requestParameters.put("redirect_uri", redirectUri);
		requestParameters.put("grant_type", "authorization_code");
		@SuppressWarnings("unchecked")
		Map<String, String> result = getRestOperations().postForObject(accessTokenUrl, requestParameters, Map.class);
		return new AccessToken(result.get("access_token"), result.get("refresh_token"));
	}

	protected RestOperations getRestOperations() {
		return new RestTemplate();
	}
}
