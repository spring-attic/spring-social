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

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpRequest;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.mock.web.MockHttpServletRequest;

public class OAuth1RequestInterceptorTest {

	@Test
	public void beforeExecution() throws Exception {
		OAuth1RequestInterceptor interceptor = new OAuth1RequestInterceptor(new OAuth1Credentials("consumer_key", "consumer_secret", "access_token", "token_secret"));
		byte[] body = "status=Hello+there".getBytes();
		MockHttpServletRequest request = new MockHttpServletRequest(HttpMethod.POST.name(), "/status/update");
		request.setRemoteHost("api.someprovider.com");
		request.setSecure(true);
		request.setContentType(MediaType.APPLICATION_FORM_URLENCODED.toString());

		ClientHttpRequestExecution execution = new ClientHttpRequestExecution() {
			public ClientHttpResponse execute(HttpRequest request, byte[] body) throws IOException {
				String authorizationHeader = request.getHeaders().getFirst("Authorization");
				Map<String, String> headerParameters = extractHeaderParameters(authorizationHeader);
				// TODO: Figure out how to test this more precisely with a fixed nonce and timestamp (and thus a fixed signature)
				assertEquals("1.0", headerParameters.get("oauth_version"));
				assertTrue(headerParameters.containsKey("oauth_nonce"));
				assertEquals("HMAC-SHA1", headerParameters.get("oauth_signature_method"));
				assertEquals("consumer_key", headerParameters.get("oauth_consumer_key"));
				assertEquals("access_token", headerParameters.get("oauth_token"));
				assertTrue(headerParameters.containsKey("oauth_timestamp"));
				assertTrue(headerParameters.containsKey("oauth_signature"));
				assertEquals(MediaType.APPLICATION_FORM_URLENCODED, request.getHeaders().getContentType());				
				return null;
			}
		};
		interceptor.intercept(new ServletServerHttpRequest(request), body, execution);
	}

	private Map<String, String> extractHeaderParameters(String authorizationHeader) {
		String[] keysAndValues = authorizationHeader.substring(6).split(",\\s");
		Map<String, String> parameters = new HashMap<String, String>();
		for (String keyAndValue : keysAndValues) {
			String[] keyValuePair = keyAndValue.split("=");
			String value = keyValuePair[1].substring(1, keyValuePair[1].length() - 1);
			parameters.put(keyValuePair[0], value);
		}
		return parameters;
	}
}
