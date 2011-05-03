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

import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.social.support.ClientHttpRequestFactorySelector;
import org.springframework.util.ClassUtils;
import org.springframework.web.client.RestTemplate;

/**
 * Factory for RestTemplate instances that execute requests for resources protected by the OAuth 2 protocol.
 * Encapsulates the configuration of the interceptor that adds the necessary Authorization header to each request before it is executed.
 * Also hides the differences between Spring 3.0.x and 3.1 implementation.
 * @author Keith Donald
 */
public class ProtectedResourceClientFactory {

	/**
	 * Constructs a RestTemplate that adds the Authorization header using the bearer token style described in the latest draft (draft 12) of the OAuth2 specification:
	 * http://tools.ietf.org/html/draft-ietf-oauth-v2-12#section-7.1
	 * @param accessToken the access token
	 */
	public static RestTemplate standard(String accessToken) {
		return version(accessToken, OAuth2Version.STANDARD);
	}

	/**
	 * Constructs a RestTemplate that adds the Authorization header using the style described in the draft 10 of the OAuth2 specification:
	 * http://tools.ietf.org/html/draft-ietf-oauth-v2-10#section-5.1.1
	 * @param accessToken the access token
	 */
	public static RestTemplate draft10(String accessToken) {
		return version(accessToken, OAuth2Version.DRAFT_10);
	}

	/**
	 * Constructs a RestTemplate that adds the Authorization header using the style described in the draft 8 of the OAuth2 specification:
	 * http://tools.ietf.org/html/draft-ietf-oauth-v2-08#section-5.1
	 * @param accessToken the access token 
	 */
	public static RestTemplate draft8(String accessToken) {
		return version(accessToken, OAuth2Version.DRAFT_8);		
	}
	
	/**
	 * Wraps a given ClientHttpRequestFactory with Spring30OAuth2RequestFactory, if necessary to support OAuth 2 request signing.
	 * If Spring 3.1 interceptors are available, no wrapping is necessary and the original request factory is returned.
	 * @param requestFactory the request factory to wrap
	 * @param accessToken the access token
	 */
	public static ClientHttpRequestFactory standardOAuthSigningRequestFactoryIfNecessary(ClientHttpRequestFactory requestFactory, String accessToken) {
		return oauthSigningIfNecessary(requestFactory, accessToken, OAuth2Version.STANDARD);
	}

	/**
	 * Wraps a given ClientHttpRequestFactory with Spring30OAuth2RequestFactory, if necessary to support OAuth 2 draft 8 request signing.
	 * If Spring 3.1 interceptors are available, no wrapping is necessary and the original request factory is returned.
	 * @param requestFactory the request factory to wrap
	 * @param accessToken the access token
	 */
	public static ClientHttpRequestFactory draft8OAuthSigningRequestFactoryIfNecessary(ClientHttpRequestFactory requestFactory, String accessToken) {
		return oauthSigningIfNecessary(requestFactory, accessToken, OAuth2Version.DRAFT_8);
	}

	/**
	 * Wraps a given ClientHttpRequestFactory with Spring30OAuth2RequestFactory, if necessary to support OAuth 2 draft 10 request signing.
	 * If Spring 3.1 interceptors are available, no wrapping is necessary and the original request factory is returned.
	 * @param requestFactory the request factory to wrap
	 * @param accessToken the access token
	 */
	public static ClientHttpRequestFactory draft10OAuthSigningRequestFactoryIfNecessary(ClientHttpRequestFactory requestFactory, String accessToken) {
		return oauthSigningIfNecessary(requestFactory, accessToken, OAuth2Version.DRAFT_10);
	}

	// internal helpers
	
	private static RestTemplate version(String accessToken, OAuth2Version version) {
		RestTemplate client = new RestTemplate(ClientHttpRequestFactorySelector.getRequestFactory());
		if (interceptorsSupported) {
			// favored
			client.setInterceptors(new ClientHttpRequestInterceptor[] { new OAuth2RequestInterceptor(accessToken, version) });
		} else {
			// 3.0.x compatibility
			client.setRequestFactory(new Spring30OAuth2RequestFactory(client.getRequestFactory(), accessToken, version));
		}
		return client;				
	}

	private static ClientHttpRequestFactory oauthSigningIfNecessary(ClientHttpRequestFactory requestFactory, String accessToken, OAuth2Version version) {
		if(interceptorsSupported) {
			return requestFactory;
		}
		return new Spring30OAuth2RequestFactory(requestFactory, accessToken, version);
	}

	private static boolean interceptorsSupported = ClassUtils.isPresent("org.springframework.http.client.ClientHttpRequestInterceptor", ProtectedResourceClientFactory.class.getClassLoader());

}
