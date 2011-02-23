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

import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.web.client.RestTemplate;

/**
 * Factory for RestTemplate instances that execute requests for resources protected by the OAuth 1 protocol.
 * Encapsulates the configuration of the interceptor that adds the necessary Authorization header to each request before it is executed.
 * Also hides the differences between Spring 3.0.x and 3.1 implementation.
 * @author Keith Donald
 */
public class ProtectedResourceClientFactory {

	public static RestTemplate create(String consumerKey, String consumerSecret, String accessToken, String accessTokenSecret) {
		// TODO add 3.0.x compatibility
		RestTemplate client = new RestTemplate();
		client.setInterceptors(new ClientHttpRequestInterceptor[] {
			new OAuth1RequestInterceptor(consumerKey, consumerSecret, accessToken, accessTokenSecret)	
		});
		return client;
	}

}
