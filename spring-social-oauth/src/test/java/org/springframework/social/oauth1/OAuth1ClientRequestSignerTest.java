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
import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.CommonsClientHttpRequestFactory;

/**
 * @author Craig Walls
 */
public class OAuth1ClientRequestSignerTest {
	private OAuth1ClientRequestSigner signer;

	@Before
	public void setup() throws Exception {
		signer = new StubbedOAuth1ClientRequestAuthorizer();
	}

	@Test
	public void authorize_postRequest() throws Exception {
		Map<String, String> bodyParameters = Collections.singletonMap("status", "some status message");
		ClientHttpRequest request = new CommonsClientHttpRequestFactory().createRequest(new URI("http://bar.com/foo"),
				HttpMethod.POST);
		signer.sign(request, bodyParameters);
		assertEquals("POST_AUTHORIZATION_HEADER", request.getHeaders().get("Authorization").get(0));
	}


	// stub buildAuthorizationHeader(), since that's not what we're
	// testing here anyway. We're just checking that the signer puts the
	// authorization into the request...not that the value is good
	private class StubbedOAuth1ClientRequestAuthorizer extends OAuth1ClientRequestSigner {
		protected String buildAuthorizationHeader(HttpMethod method, URI url, Map<String, String> parameters) {
			if (method.equals(HttpMethod.POST) && url.toString().equals("http://bar.com/foo")
					&& parameters.equals(Collections.singletonMap("status", "some status message"))) {
				return "POST_AUTHORIZATION_HEADER";
			}
			return null;
		}
	}
}
