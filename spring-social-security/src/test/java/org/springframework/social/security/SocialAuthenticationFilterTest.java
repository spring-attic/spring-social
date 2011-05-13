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

package org.springframework.social.security;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Collection;
import java.util.Collections;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockFilterConfig;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockServletContext;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.NullRememberMeServices;
import org.springframework.social.connect.ConnectionFactory;
import org.springframework.social.connect.UsersConnectionRepository;
import org.springframework.social.security.SocialAuthenticationFilter;
import org.springframework.social.security.SocialAuthenticationToken;
import org.springframework.social.security.UserIdExtractor;
import org.springframework.social.security.provider.SocialAuthenticationService;
import org.springframework.social.security.provider.SocialAuthenticationService.AuthenticationMode;
import org.springframework.social.security.test.DummyConnection;

public class SocialAuthenticationFilterTest {

	@Before
	@After
	public void clean() {
		SecurityContextHolder.getContext().setAuthentication(null);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testImplicitAuth() throws Exception {

		FilterTestEnv env = new FilterTestEnv("GET", "/something");

		{
			ConnectionFactory<Object> factory = mock(ConnectionFactory.class);
			when(factory.getProviderId()).thenReturn("mockExplicit");

			SocialAuthenticationService<Object> authServiceExplicit = mock(SocialAuthenticationService.class);
			when(authServiceExplicit.getAuthenticationMode()).thenReturn(AuthenticationMode.EXPLICIT);
			when(authServiceExplicit.getAuthToken(AuthenticationMode.EXPLICIT, env.req, env.res)).thenThrow(
					new RuntimeException("unexpected call"));
			when(authServiceExplicit.getConnectionFactory()).thenReturn(factory);
			env.filter.addAuthService(authServiceExplicit);
		}

		{
			ConnectionFactory<Object> factory = mock(ConnectionFactory.class);
			when(factory.getProviderId()).thenReturn("mockImplicit");

			SocialAuthenticationService<Object> authServiceImplicit = mock(SocialAuthenticationService.class);
			when(authServiceImplicit.getAuthenticationMode()).thenReturn(AuthenticationMode.IMPLICIT);
			when(authServiceImplicit.getAuthToken(AuthenticationMode.IMPLICIT, env.req, env.res)).thenReturn(env.auth);
			when(authServiceImplicit.getConnectionFactory()).thenReturn(factory);
			env.filter.addAuthService(authServiceImplicit);
		}

		when(env.filter.getAuthManager().authenticate(env.auth)).thenReturn(env.authSuccess);

		assertNull(SecurityContextHolder.getContext().getAuthentication());

		env.doFilter();

		assertNotNull(SecurityContextHolder.getContext().getAuthentication());

	}

	@SuppressWarnings("unchecked")
	@Test
	public void testImplicitAuthFail() throws Exception {

		FilterTestEnv env = new FilterTestEnv("GET", "/something");

		{
			ConnectionFactory<Object> provider = mock(ConnectionFactory.class);
			when(provider.getProviderId()).thenReturn("mock");

			SocialAuthenticationService<Object> authServiceExplicit = mock(SocialAuthenticationService.class);
			when(authServiceExplicit.getAuthenticationMode()).thenReturn(AuthenticationMode.EXPLICIT);
			when(authServiceExplicit.getAuthToken(AuthenticationMode.EXPLICIT, env.req, env.res)).thenThrow(
					new RuntimeException("unexpected call"));
			when(authServiceExplicit.getConnectionFactory()).thenReturn(provider);
			env.filter.addAuthService(authServiceExplicit);
		}

		when(env.filter.getAuthManager().authenticate(env.auth)).thenReturn(env.authSuccess);

		assertNull(SecurityContextHolder.getContext().getAuthentication());

		env.doFilter();

		assertNull(SecurityContextHolder.getContext().getAuthentication());

	}

	@SuppressWarnings("unchecked")
	@Test
	public void testExplicitAuth() throws Exception {

		FilterTestEnv env = new FilterTestEnv("GET", "/auth");
		env.filter.setFilterProcessesUrl(env.req.getRequestURI());

		ConnectionFactory<Object> factory = mock(ConnectionFactory.class);
		when(factory.getProviderId()).thenReturn("mock");
		env.req.setRequestURI(env.req.getRequestURI() + "/" + factory.getProviderId());

		SocialAuthenticationService<Object> authService = mock(SocialAuthenticationService.class);
		when(authService.getAuthenticationMode()).thenReturn(AuthenticationMode.EXPLICIT);
		when(authService.getConnectionFactory()).thenReturn(factory);
		when(authService.getAuthToken(AuthenticationMode.EXPLICIT, env.req, env.res)).thenReturn(env.auth);
		env.filter.addAuthService(authService);

		when(env.filter.getAuthManager().authenticate(env.auth)).thenReturn(env.authSuccess);

		assertNull(SecurityContextHolder.getContext().getAuthentication());

		env.doFilter();

		assertNotNull(SecurityContextHolder.getContext().getAuthentication());

	}

	private static class FilterTestEnv {
		private final SocialAuthenticationFilter filter;

		private final MockServletContext context;
		private final MockHttpServletRequest req;
		private final MockHttpServletResponse res;
		private final MockFilterChain chain;
		private final MockFilterConfig config = new MockFilterConfig();
		private final SocialAuthenticationToken auth;
		private final SocialAuthenticationToken authSuccess;

		private FilterTestEnv(String method, String requestURI) {
			context = new MockServletContext();
			req = new MockHttpServletRequest(context, method, requestURI);
			res = new MockHttpServletResponse();
			chain = new MockFilterChain();

			filter = new SocialAuthenticationFilter();
			filter.setServletContext(context);
			filter.setRememberMeServices(new NullRememberMeServices());
			filter.setAuthManager(mock(AuthenticationManager.class));
			filter.setUserIdExtractor(mock(UserIdExtractor.class));
			filter.setUsersConnectionRepository(mock(UsersConnectionRepository.class));

			auth = new SocialAuthenticationToken(DummyConnection.dummy("provider", "user").createData(), null);

			Collection<? extends GrantedAuthority> authorities = Collections.emptyList();
			User user = new User("foo", "bar", authorities);
			authSuccess = new SocialAuthenticationToken("mock", user, null, authorities);
		}

		private void doFilter() throws Exception {

			filter.init(config);
			filter.doFilter(req, res, chain);
			filter.destroy();
		}
	}
}
