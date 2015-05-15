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

public class OAuth2TokenParameterRequestInterceptorTest {

	@Test
	public void currentOAuth2SpecInterceptor() throws Exception {
		OAuth2TokenParameterRequestInterceptor interceptor = new OAuth2TokenParameterRequestInterceptor("SOMETOKEN");
		assertThatInterceptorAddsTokenParameter(interceptor);
	}
	
	private void assertThatInterceptorAddsTokenParameter(OAuth2TokenParameterRequestInterceptor interceptor) throws Exception {
		byte[] body = "status=Hello+there".getBytes();
		MockHttpServletRequest originalRequest = new MockHttpServletRequest(HttpMethod.POST.name(), "/status/update");
		originalRequest.setServerName("api.someprovider.com");
		originalRequest.setContentType(MediaType.APPLICATION_FORM_URLENCODED.toString());
		ClientHttpRequestExecution execution = new ClientHttpRequestExecution() {
			public ClientHttpResponse execute(HttpRequest request, byte[] body) throws IOException {
				assertEquals(MediaType.APPLICATION_FORM_URLENCODED, request.getHeaders().getContentType());
				assertEquals("access_token=SOMETOKEN", request.getURI().getQuery());
				return null;
			}
		};		
		interceptor.intercept(new ServletServerHttpRequest(originalRequest), body, execution);
	}

}
