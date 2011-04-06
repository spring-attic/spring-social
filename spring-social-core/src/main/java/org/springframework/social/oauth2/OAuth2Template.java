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
package org.springframework.social.oauth2;

import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.Map;

import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriUtils;

/**
 * OAuth2Operations implementation that uses REST-template to make the OAuth calls.
 * @author Keith Donald
 */
public class OAuth2Template implements OAuth2Operations {

	private final String clientId;
	
	private final String clientSecret;

	private final String accessTokenUrl;

	private final String authorizeUrl;

	private final RestTemplate restTemplate;
	
	public OAuth2Template(String clientId, String clientSecret, String authorizeUrl, String accessTokenUrl) {
		this.clientId = clientId;
		this.clientSecret = clientSecret;
		this.authorizeUrl = authorizeUrl + "?client_id=" + clientId;
		this.accessTokenUrl = accessTokenUrl;
		this.restTemplate = createRestTemplate();
	}

	public final String buildAuthorizeUrl(String redirectUri, String scope, String state) {
		StringBuilder authorizeUrl = new StringBuilder(this.authorizeUrl).append('&').append("redirect_uri").append('=').append(redirectUri);
		if (scope != null) {
			authorizeUrl.append('&').append("scope").append('=').append(scope);
		}
		if (state != null) {
			authorizeUrl.append('&').append("state").append('=').append(state);	
		}
		return encodeUri(authorizeUrl.toString());
	}

	public final AccessGrant exchangeForAccess(String authorizationCode, String redirectUri, MultiValueMap<String, String> additionalParameters) {
		MultiValueMap<String, String> params = new LinkedMultiValueMap<String, String>();
		params.set("client_id", clientId);
		params.set("client_secret", clientSecret);
		params.set("code", authorizationCode);
		params.set("redirect_uri", redirectUri);
		params.set("grant_type", "authorization_code");
		if (additionalParameters != null) {
			params.putAll(additionalParameters);
		}
		return postForAccessGrant(accessTokenUrl, params);
	}

	public final AccessGrant refreshAccess(String refreshToken, String scope, MultiValueMap<String, String> additionalParameters) {
		MultiValueMap<String, String> params = new LinkedMultiValueMap<String, String>();
		params.set("client_id", clientId);
		params.set("client_secret", clientSecret);
		params.set("refresh_token", refreshToken);
		if (scope != null) {
			params.set("scope", scope);
		}
		params.set("grant_type", "refresh_token");
		if (additionalParameters != null) {
			params.putAll(additionalParameters);
		}
		return postForAccessGrant(accessTokenUrl, params);
	}

	// subclassing hooks
	
	protected RestTemplate createRestTemplate() {
		return new RestTemplate();
	}

	@SuppressWarnings("unchecked")
	protected AccessGrant postForAccessGrant(String accessTokenUrl, MultiValueMap<String, String> parameters) {
		Map<String, Object> result = restTemplate.postForObject(accessTokenUrl, parameters, Map.class);
		return extractAccessGrant(result);
	}
	
	protected Map<String, Object> extractAdditionalAccessGrantParameters(Map<String, Object> result) {
		return Collections.emptyMap();
	}
		
	// testing hooks
	
	RestTemplate getRestTemplate() {
		return restTemplate;
	}
	
	// internal helpers

	private String encodeUri(String uri) {
		try {
			return UriUtils.encodeUri(uri, "UTF-8");
		}
		catch (UnsupportedEncodingException ex) {
			// should not happen, UTF-8 is always supported
			throw new IllegalStateException(ex);
		}
	}
	
	private AccessGrant extractAccessGrant(Map<String, Object> result) {
		Integer expiresIn = (Integer) result.get("expires_in");
		Long expiresAt = expiresIn != null ? System.currentTimeMillis() + expiresIn * 1000 : null;
		return new AccessGrant((String) result.get("access_token"), (String) result.get("refresh_token"), expiresAt, (String) result.get("scope"), extractAdditionalAccessGrantParameters(result));
	}
	
}