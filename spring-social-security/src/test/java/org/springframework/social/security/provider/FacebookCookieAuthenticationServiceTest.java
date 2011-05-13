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

package org.springframework.social.security.provider;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.social.security.provider.SocialAuthenticationService.AuthenticationMode.EXPLICIT;
import static org.springframework.social.security.provider.SocialAuthenticationService.AuthenticationMode.IMPLICIT;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.mock.web.MockServletContext;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionData;
import org.springframework.social.connect.support.OAuth2ConnectionFactory;
import org.springframework.social.facebook.api.Facebook;
import org.springframework.social.oauth2.AccessGrant;
import org.springframework.social.oauth2.OAuth2Operations;
import org.springframework.social.security.SocialAuthenticationToken;
import org.springframework.social.security.provider.FacebookCookieAuthenticationService;
import org.springframework.social.security.provider.OAuth2AuthenticationService;
import org.springframework.social.security.test.DummyConnection;

public class FacebookCookieAuthenticationServiceTest {

	@Test
	public void test() throws Exception {
		@SuppressWarnings("unchecked")
		final OAuth2ConnectionFactory<Facebook> factory = mock(OAuth2ConnectionFactory.class);
		final OAuth2Operations operations = mock(OAuth2Operations.class);
		final String serverName = "example.com";
		final AccessGrant grant = new AccessGrant("my_token");
		final Connection<Facebook> connection = new DummyConnection<Facebook>("provider", "user", mock(Facebook.class));

		final FacebookCookieAuthenticationService authSvc = new FacebookCookieAuthenticationService(factory) {

			@Override
			protected Map<String, String> getFbParams(HttpServletRequest request, String appId, String appSecret) {
				Map<String, String> params = new HashMap<String, String>();
				for (Object o : request.getParameterMap().entrySet()) {
					Map.Entry<?, ?> e = (Map.Entry<?, ?>) o;
					String name = (String) e.getKey();
					String[] values = (String[]) e.getValue();
					if (values != null && values.length >= 1) {
						params.put(name, values[0]);
					}
				}
				return params;
			}
		};
		authSvc.setAppId("appId");
		authSvc.setAppSecret("appSecret");
		authSvc.setOAuthEnabled(false);
		authSvc.afterPropertiesSet();

		final MockServletContext context = new MockServletContext();
		final MockHttpSession session = new MockHttpSession(context);

		// mock definitions
		when(factory.getProviderId()).thenReturn(connection.getKey().getProviderId());
		when(factory.getOAuthOperations()).thenReturn(operations);
		when(factory.createConnection(grant)).thenReturn(connection);

		// first checks
		assertNull(authSvc.getAuthenticationMode());

		// implicit success
		MockHttpServletRequest request = new MockHttpServletRequest(context, "GET", "/auth/foo");
		request.setServerName(serverName);
		request.setSession(session);
		request.addParameter("uid", "1234567890");
		request.addParameter("access_token", "my_access_token");
		MockHttpServletResponse response = new MockHttpServletResponse();

		SocialAuthenticationToken token = authSvc.getAuthToken(IMPLICIT, request, response);
		assertNotNull(token);
		assertTrue(token.getPrincipal() instanceof ConnectionData);
		assertFalse(token.isAuthenticated());

		// implicit fail
		request = new MockHttpServletRequest(context, "GET", "/auth/foo");
		request.setServerName(serverName);
		request.setSession(session);
		response = new MockHttpServletResponse();

		token = authSvc.getAuthToken(IMPLICIT, request, response);
		assertNull(token);
		
		// explicit fallback
		@SuppressWarnings("unchecked")
		OAuth2AuthenticationService<Facebook> oAuthSvc = mock(OAuth2AuthenticationService.class);
		authSvc.setOAuthEnabled(true);
		authSvc.setOAuthService(oAuthSvc);
		when(oAuthSvc.getAuthToken(
				eq(EXPLICIT), 
				any(HttpServletRequest.class),
				any(HttpServletResponse.class)
			)).thenReturn(new SocialAuthenticationToken(data("oAuthUser"), null));
		
		request = new MockHttpServletRequest(context, "GET", "/auth/foo");
		request.setServerName(serverName);
		request.setSession(session);
		response = new MockHttpServletResponse();

		token = authSvc.getAuthToken(EXPLICIT, request, response);
		assertNotNull(token);
		assertTrue(token.getPrincipal() instanceof ConnectionData);
		assertFalse(token.isAuthenticated());
		assertEquals("oAuthUser", ((ConnectionData)token.getPrincipal()).getProviderUserId());
	}

	private static ConnectionData data(String providerUserId) {
		return new ConnectionData("provider", providerUserId, null, null, null, null, null, null, null);
	}
}
