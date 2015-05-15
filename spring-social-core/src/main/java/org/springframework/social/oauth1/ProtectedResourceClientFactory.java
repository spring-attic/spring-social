/*
 * Copyright 2015 the original author or authors.
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

import java.util.LinkedList;
import java.util.List;

import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.social.support.ClientHttpRequestFactorySelector;
import org.springframework.web.client.RestTemplate;

/**
 * Factory for RestTemplate instances that execute requests for resources protected by the OAuth 1 protocol.
 * Encapsulates the configuration of the interceptor that adds the necessary Authorization header to each request before it is executed.
 * 
 * <h4>Parameter Encoding</h4>
 * 
 * <p>The underlying OAuth signing algorithm assumes that query parameters are encoded as application/x-www-form-urlencoded.
 * The RestTemplate methods that take String URL templates encode query parameters per RFC 3986 and not form-encoded.
 * This leads to problems where certain characters are improperly encoded. Spaces, for example are encoded as %20 instead of +; 
 * and an actual + sign is left unencoded (and will be interpreted as a space when decoded as if it were form-encoded).</p>
 * 
 * <p>However, RestTemplate's methods that take URIs will leave the URI's parameters untouched. Therefore, when consuming a REST operation
 * with query parameters that require encoding (for example, if passing a + sign in a parameter value) you should use RestTemplate's
 * URI-based methods constructed with form-encoded parameters. See URIBuilder for a convenient way to build up such URIs.</p>
 * 
 * @author Keith Donald
 */
class ProtectedResourceClientFactory {
	
	/**
	 * Constructs a RestTemplate that adds the OAuth1 Authorization header to each request before it is executed.
	 */
	public static RestTemplate create(OAuth1Credentials credentials) {
		RestTemplate client = new RestTemplate(ClientHttpRequestFactorySelector.getRequestFactory());
		OAuth1RequestInterceptor interceptor = new OAuth1RequestInterceptor(credentials);
		List<ClientHttpRequestInterceptor> interceptors = new LinkedList<ClientHttpRequestInterceptor>();
		interceptors.add(interceptor);
		client.setInterceptors(interceptors);
		return client;
	}
	
}
