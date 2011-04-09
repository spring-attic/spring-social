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
package org.springframework.social.facebook;

import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.social.oauth2.AccessGrant;
import org.springframework.social.oauth2.OAuth2Template;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

/**
 * Facebook-specific extension of OAuth2Template to use a RestTemplate that recognizes form-encoded responses as "text/plain".
 * Facebook token responses are form-encoded results with a content type of "text/plain", which prevents the FormHttpMessageConverter
 * registered by default from parsing the results.
 * @author Craig Walls
 */
public class FacebookOAuth2Template extends OAuth2Template {

	public FacebookOAuth2Template(String clientId, String clientSecret, String authorizeUrl, String authenticateUrl, String accessTokenUrl) {
		super(clientId, clientSecret, authorizeUrl, authenticateUrl, accessTokenUrl);
	}

	@Override
	protected RestTemplate createRestTemplate() {
		RestTemplate restTemplate = new RestTemplate();
		FormHttpMessageConverter messageConverter = new FormHttpMessageConverter() {
			@Override
			public boolean canRead(Class<?> clazz, MediaType mediaType) {
				return clazz.equals(Map.class) && mediaType != null && mediaType.getType().equals("text")
					&& mediaType.getSubtype().equals("plain");
			}
		};
		restTemplate.getMessageConverters().add(messageConverter);
		return restTemplate;
	}
	
	@Override
	protected AccessGrant postForAccessGrant(String accessTokenUrl, MultiValueMap<String, String> parameters) {
		MultiValueMap<String, String> response = (MultiValueMap<String, String>) getRestTemplate().postForObject(accessTokenUrl, parameters, Map.class);
		String accessToken = response.getFirst("access_token");
		String expires = response.getFirst("expires");
		Long expireTime = expires != null ? System.currentTimeMillis() + Long.valueOf(expires) * 1000 : null;
		return new AccessGrant(accessToken, null, null, expireTime);
	}
}
