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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
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
	
	private final RestTemplate restTemplate = new RestTemplate();
	
	public OAuth2Template(String clientId, String clientSecret, String authorizeUrl, String accessTokenUrl) {
		this.clientId = clientId;
		this.clientSecret = clientSecret;
		this.authorizeUrlTemplate = new UriTemplate(authorizeUrl);
		this.accessTokenUrl = accessTokenUrl;
		addTextToMapMessageConverter();
	}

	public String buildAuthorizeUrl(String redirectUri, String scope) {
		Map<String, String> urlVariables = new HashMap<String, String>();
		urlVariables.put("client_id", clientId);
		urlVariables.put("redirect_uri", redirectUri);
		urlVariables.put("scope", scope);
		return authorizeUrlTemplate.expand(urlVariables).toString();
	}

	public AccessGrant exchangeForAccess(String authorizationCode, String redirectUri) {
		MultiValueMap<String, String> requestParameters = new LinkedMultiValueMap<String, String>();
		requestParameters.set("client_id", clientId);
		requestParameters.set("client_secret", clientSecret);
		requestParameters.set("code", authorizationCode);
		requestParameters.set("redirect_uri", redirectUri);
		requestParameters.set("grant_type", "authorization_code");
		@SuppressWarnings("unchecked")
		Map<String, ?> result = restTemplate.postForObject(accessTokenUrl, requestParameters, Map.class);
		return new AccessGrant(valueOf(result.get("access_token")), valueOf(result.get("refresh_token")));
	}

	// testing hooks
	
	RestTemplate getRestTemplate() {
		return restTemplate;
	}
	
	// internal helpers
	
	// TODO : Can probably tweak RestTemplate's message converters to deal with this better.
	// TODO - KD: clarify: what is this for?
	private String valueOf(Object object) {
		if (object == null) {
			return null;
		} else if (object instanceof List) {
			List<?> list = (List<?>) object;
			if (list.size() > 0) {
				return String.valueOf(list.get(0));
			}
			return null;
		}
		return String.valueOf(object);
	}

	/*
	 * Facebook returns form-encoded results with a content type of "text/plain". The "text/plain" content type prevents
	 * any of the default encoders from being able to parse the results, even though FormHttpMessageConverter is
	 * perfectly capable of doing so. This method adds another FormHttpMessageConverter that can read "text/plain" into
	 * a Map so that this works for Facebook.
	 */
	private void addTextToMapMessageConverter() {
		FormHttpMessageConverter messageConverter = new FormHttpMessageConverter() {
			public boolean canRead(Class<?> clazz, MediaType mediaType) {
				return clazz.equals(Map.class) && mediaType != null && mediaType.getType().equals("text")
						&& mediaType.getSubtype().equals("plain");
			}
		};
		restTemplate.getMessageConverters().add(messageConverter);
	}
}
