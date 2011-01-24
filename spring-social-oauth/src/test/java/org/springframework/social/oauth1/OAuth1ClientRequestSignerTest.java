/*
 * Copyright 2010 the original author or authors.
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

import java.net.URI;
import java.util.Collections;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.internal.matchers.Contains;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.SimpleClientHttpRequestFactory;

/**
 * @author Craig Walls
 */
public class OAuth1ClientRequestSignerTest {
	private OAuth1ClientRequestSigner signer;

	@Before
	public void setup() throws Exception {
		signer = new OAuth1ClientRequestSigner("api_key", "api_secret", "access_token", "access_token_secret");
	}

	@Test
	public void authorize_postRequest() throws Exception {
		Map<String, String> bodyParameters = Collections.singletonMap("status", "some status message");
		ClientHttpRequest request = new SimpleClientHttpRequestFactory().createRequest(new URI("http://bar.com/foo"),
				HttpMethod.POST);
		signer.sign(request, bodyParameters);
		String authorizationHeader = request.getHeaders().get("Authorization").get(0);
		assertTrue(authorizationHeader.startsWith("OAuth"));
		assertThat(authorizationHeader, new Contains("oauth_version=\"1.0\""));
		assertThat(authorizationHeader, new Contains("oauth_nonce=\""));
		assertThat(authorizationHeader, new Contains("oauth_signature_method=\"HMAC-SHA1\""));
		assertThat(authorizationHeader, new Contains("oauth_consumer_key=\"api_key\""));
		assertThat(authorizationHeader, new Contains("oauth_token=\"access_token\""));
		assertThat(authorizationHeader, new Contains("oauth_timestamp=\""));
		assertThat(authorizationHeader, new Contains("oauth_signature=\""));
		System.out.println(authorizationHeader);
	}

}
