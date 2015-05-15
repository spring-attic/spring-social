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

import org.junit.Test;
import org.mockito.Matchers;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.mock.web.MockServletContext;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.support.OAuth1ConnectionFactory;
import org.springframework.social.oauth1.OAuth1Operations;
import org.springframework.social.oauth1.OAuth1Parameters;
import org.springframework.social.oauth1.OAuth1Version;
import org.springframework.social.oauth1.OAuthToken;
import org.springframework.social.security.SocialAuthenticationRedirectException;
import org.springframework.social.security.SocialAuthenticationToken;
import org.springframework.social.security.test.ArgMatchers;
import org.springframework.social.security.test.DummyConnection;
import org.springframework.util.MultiValueMap;

public class OAuth1AuthenticationServiceTest {

	@Test
	public void test() throws Exception {
		@SuppressWarnings("unchecked")
		final OAuth1ConnectionFactory<Object> factory = mock(OAuth1ConnectionFactory.class);
		final OAuth1Operations operations = mock(OAuth1Operations.class);
		final String serverName = "example.com";
		final String serviceUrl = "http://twitter.com/auth";
		final OAuthToken oAuthToken = new OAuthToken("my_token", "my_secret");
		final String verifier = "my_verifier";
		final Connection<Object> connection = DummyConnection.dummy("provider", "user");
		
		final OAuth1AuthenticationService<Object> authSvc = new OAuth1AuthenticationService<Object>(factory);
		authSvc.getReturnToUrlParameters().add("param");
		authSvc.afterPropertiesSet();
		
		final MockServletContext context = new MockServletContext();
		final MockHttpSession session = new MockHttpSession(context);
		
		// mock definitions
		when(factory.getProviderId()).thenReturn(connection.getKey().getProviderId());
		when(factory.getOAuthOperations()).thenReturn(operations);
		when(factory.createConnection(ArgMatchers.oAuthToken(oAuthToken))).thenReturn(connection);
		
		when(operations.getVersion()).thenReturn(OAuth1Version.CORE_10_REVISION_A);
		when(operations.fetchRequestToken("http://"+serverName+"/auth/foo?param=param_value", null)).thenReturn(oAuthToken);
		when(operations.exchangeForAccessToken(ArgMatchers.authorizedRequestToken(oAuthToken, verifier), Matchers.same((MultiValueMap<String, String>) null))).thenReturn(oAuthToken);
		when(operations.buildAuthenticateUrl(oAuthToken.getValue(), OAuth1Parameters.NONE)).thenReturn(serviceUrl + "?oauth_token=" + oAuthToken.getValue());
		
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
			assertEquals(serviceUrl + "?oauth_token=" + oAuthToken.getValue(), e.getRedirectUrl());
		}
		
		// second phase
		request = new MockHttpServletRequest(context, "GET", "/auth/foo");
		request.setServerName(serverName);
		request.setSession(session);
		request.addParameter("oauth_verifier", verifier);
		response = new MockHttpServletResponse(); 
		
		SocialAuthenticationToken token = authSvc.getAuthToken(request, response);
		assertNotNull(token);
		assertTrue(token.getConnection() instanceof Connection);
		assertFalse(token.isAuthenticated());
	}
}
