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
package org.springframework.social.security.provider;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static org.springframework.social.security.test.ArgMatchers.*;

import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.mock.web.MockServletContext;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.support.OAuth2ConnectionFactory;
import org.springframework.social.oauth2.AccessGrant;
import org.springframework.social.oauth2.OAuth2Operations;
import org.springframework.social.security.SocialAuthenticationRedirectException;
import org.springframework.social.security.SocialAuthenticationToken;
import org.springframework.social.security.test.DummyConnection;

public class OAuth2AuthenticationServiceTest {

	@Test
	public void test() throws Exception {
		@SuppressWarnings("unchecked")
		final OAuth2ConnectionFactory<Object> factory = mock(OAuth2ConnectionFactory.class);
		final OAuth2Operations operations = mock(OAuth2Operations.class);
		final String serverName = "example.com";
		final AccessGrant accessGrant = new AccessGrant("my_token");
		final String code = "code";
		final Connection<Object> connection = DummyConnection.dummy("provider", "user");

		final OAuth2AuthenticationService<Object> authSvc = new OAuth2AuthenticationService<Object>(factory);
		authSvc.getReturnToUrlParameters().add("param");
		authSvc.afterPropertiesSet();

		final MockServletContext context = new MockServletContext();
		final MockHttpSession session = new MockHttpSession(context);

		// mock definitions
		when(factory.getProviderId()).thenReturn(connection.getKey().getProviderId());
		when(factory.getOAuthOperations()).thenReturn(operations);
		when(factory.createConnection(accessGrant)).thenReturn(connection);

		when(
				operations.buildAuthenticateUrl(oAuth2Parameters("http://example.com/auth/foo?param=param_value"))).thenReturn(
				"http://facebook.com/auth");
		when(operations.exchangeForAccess(code, "http://example.com/auth/foo", null)).thenReturn(accessGrant);

		// first phase
		MockHttpServletRequest request = new MockHttpServletRequest(context, "GET", "/auth/foo");
		request.setServerName(serverName);
		request.setSession(session);
		request.addParameter("param", "param_value");
		MockHttpServletResponse response = new MockHttpServletResponse();

		try {
			SocialAuthenticationToken token = authSvc.getAuthToken(request, response);
			fail("redirect expected, was token " + token);
		} catch (SocialAuthenticationRedirectException e) {
			// expect redirect to service url including token
			// assertEquals(serviceUrl + "?oauth_token=" +
			// oAuthToken.getValue(), e.getRedirectUrl());
		}

		// second phase
		request = new MockHttpServletRequest(context, "GET", "/auth/foo");
		request.setServerName(serverName);
		request.setSession(session);
		request.addParameter("code", code);
		response = new MockHttpServletResponse();

		SocialAuthenticationToken token = authSvc.getAuthToken(request, response);
		assertNotNull(token);
		assertTrue(token.getConnection() instanceof Connection);
		assertFalse(token.isAuthenticated());
	}
}
