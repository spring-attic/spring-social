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
package org.springframework.social.oauth2;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpRequest;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.mock.web.MockHttpServletRequest;

public class OAuth2RequestInterceptorTest {
	
	@Test
	public void currentOAuth2SpecInterceptor() throws Exception {
		OAuth2RequestInterceptor interceptor = new OAuth2RequestInterceptor("access_token", OAuth2Version.BEARER);
		assertThatInterceptorWritesAuthorizationHeader(interceptor, "Bearer access_token");
	}
	
	@Test
	public void draft10Interceptor() throws Exception {
		OAuth2RequestInterceptor interceptor = new OAuth2RequestInterceptor("access_token", OAuth2Version.DRAFT_10);
		assertThatInterceptorWritesAuthorizationHeader(interceptor, "OAuth access_token");
	}

	@Test
	public void draft8Interceptor() throws Exception {
		OAuth2RequestInterceptor interceptor = new OAuth2RequestInterceptor("access_token", OAuth2Version.DRAFT_8);
		assertThatInterceptorWritesAuthorizationHeader(interceptor, "Token token=\"access_token\"");
	}

	private void assertThatInterceptorWritesAuthorizationHeader(OAuth2RequestInterceptor interceptor, final String expected) throws Exception {
		byte[] body = "status=Hello+there".getBytes();
		MockHttpServletRequest request = new MockHttpServletRequest(HttpMethod.POST.name(), "https://api.someprovider.com/status/update");
		request.setContentType(MediaType.APPLICATION_FORM_URLENCODED.toString());
		ClientHttpRequestExecution execution = new ClientHttpRequestExecution() {
			public ClientHttpResponse execute(HttpRequest request, byte[] body) throws IOException {
				String authorizationHeader = request.getHeaders().getFirst("Authorization");
				assertEquals(expected, authorizationHeader);
				assertEquals(MediaType.APPLICATION_FORM_URLENCODED, request.getHeaders().getContentType());
				return null;
			}
		};		
		interceptor.intercept(new ServletServerHttpRequest(request), body, execution);
	}
}
