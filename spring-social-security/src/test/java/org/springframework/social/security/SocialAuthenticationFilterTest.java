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
package org.springframework.social.security;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockFilterConfig;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockServletContext;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.NullRememberMeServices;
import org.springframework.social.UserIdSource;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionData;
import org.springframework.social.connect.ConnectionFactory;
import org.springframework.social.connect.ConnectionRepository;
import org.springframework.social.connect.UsersConnectionRepository;
import org.springframework.social.security.provider.SocialAuthenticationService;
import org.springframework.social.security.provider.SocialAuthenticationService.ConnectionCardinality;
import org.springframework.social.security.test.DummyConnection;
import org.springframework.social.security.test.MockConnectionFactory;

public class SocialAuthenticationFilterTest {

	@Before
	@After
	public void clean() {
		SecurityContextHolder.getContext().setAuthentication(null);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testExplicitAuth() throws Exception {

		FilterTestEnv env = new FilterTestEnv("GET", "/auth", null);
		env.filter.setFilterProcessesUrl(env.req.getRequestURI());
		env.filter.setPostLoginUrl("/success");
		
		ConnectionFactory<Object> factory = mock(MockConnectionFactory.class);
		when(factory.getProviderId()).thenReturn("mock");
		env.req.setRequestURI(env.req.getRequestURI() + "/" + factory.getProviderId());

		SocialAuthenticationService<Object> authService = mock(SocialAuthenticationService.class);
		when(authService.getConnectionCardinality()).thenReturn(ConnectionCardinality.ONE_TO_ONE);
		when(authService.getConnectionFactory()).thenReturn(factory);
		when(authService.getAuthToken(env.req, env.res)).thenReturn(env.auth);
		env.addAuthService(authService);

		when(env.authManager.authenticate(env.auth)).thenReturn(env.authSuccess);

		assertNull(SecurityContextHolder.getContext().getAuthentication());

		env.doFilter();

		assertNotNull(SecurityContextHolder.getContext().getAuthentication());
		
		assertEquals("/success", env.res.getRedirectedUrl());
	}

	@Test
	public void testFailedAuth_slashRegister() throws Exception {
		FilterTestEnv env = new FilterTestEnv("GET", "/auth", "/register");
		testFailedAuth(env);
	}

	@Test
	public void testFailedAuth_register() throws Exception {
		FilterTestEnv env = new FilterTestEnv("GET", "/auth", "register");
		testFailedAuth(env);
	}

	@Test
	public void testFailedAuth_fullyQualifiedUrlRegister() throws Exception {
		FilterTestEnv env = new FilterTestEnv("GET", "/auth", "http://localhost/register");
		testFailedAuth(env);
	}

	@SuppressWarnings("unchecked")
	private void testFailedAuth(FilterTestEnv env) throws Exception {
		env.filter.setFilterProcessesUrl(env.req.getRequestURI());
		env.filter.setPostLoginUrl("/success");
		
		ConnectionFactory<Object> factory = mock(MockConnectionFactory.class);
		when(factory.getProviderId()).thenReturn("mock");
		env.req.setRequestURI(env.req.getRequestURI() + "/" + factory.getProviderId());

		SocialAuthenticationService<Object> authService = mock(SocialAuthenticationService.class);
		when(authService.getConnectionCardinality()).thenReturn(ConnectionCardinality.ONE_TO_ONE);
		when(authService.getConnectionFactory()).thenReturn(factory);
		when(authService.getAuthToken(env.req, env.res)).thenReturn(env.auth);
		env.addAuthService(authService);

		when(env.authManager.authenticate(env.auth)).thenThrow(new BadCredentialsException("Failed"));

		assertNull(SecurityContextHolder.getContext().getAuthentication());

		env.doFilter();

		assertNull(SecurityContextHolder.getContext().getAuthentication());
		
		assertEquals("http://localhost/register", env.res.getRedirectedUrl());
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void addConnection() {
		UsersConnectionRepository usersConnectionRepository = mock(UsersConnectionRepository.class);
		SocialAuthenticationFilter filter = new SocialAuthenticationFilter(null, null, usersConnectionRepository, null);
		
		SocialAuthenticationService<Object> authService = mock(SocialAuthenticationService.class);
		ConnectionRepository connectionRepository = mock(ConnectionRepository.class);
		ConnectionFactory<Object> connectionFactory = mock(MockConnectionFactory.class);
		
		MockHttpServletRequest request = new MockHttpServletRequest();
		ConnectionData data = new ConnectionData("dummyprovider", "1234", null, null, null, null, null, null, null);
		String userId = "joe";
		
		DummyConnection<Object> connection = DummyConnection.dummy(data.getProviderId(), userId);
		
		when(usersConnectionRepository.findUserIdsConnectedTo(data.getProviderId(), set(data.getProviderUserId()))).thenReturn(empty(String.class));
		when(usersConnectionRepository.createConnectionRepository(userId)).thenReturn(connectionRepository);
		
		when(authService.getConnectionCardinality()).thenReturn(ConnectionCardinality.ONE_TO_ONE);
		when(authService.getConnectionFactory()).thenReturn(connectionFactory);
		when(authService.getConnectionAddedRedirectUrl(request, connection)).thenReturn("/redirect");
		
		when(connectionFactory.createConnection(data)).thenReturn(connection);
		
		Connection<?> addedConnection = filter.addConnection(authService, userId, data);
		assertNotNull(addedConnection);
		assertSame(connection, addedConnection);
		
		verify(connectionRepository).addConnection(connection);
	}
	
	private static <T> Set<T> empty(Class<T> cls) {
		return Collections.emptySet();
	}
	
	private static Set<String> set(String ... o) {
		return Collections.unmodifiableSet(new HashSet<String>(Arrays.asList(o)));
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
		private final AuthenticationManager authManager;

		private FilterTestEnv(String method, String requestURI, String signupUrl) {
			context = new MockServletContext();
			req = new MockHttpServletRequest(context, method, requestURI);
			res = new MockHttpServletResponse();
			chain = new MockFilterChain();
			authManager = mock(AuthenticationManager.class);

			filter = new SocialAuthenticationFilter(authManager, mock(UserIdSource.class), mock(UsersConnectionRepository.class), new SocialAuthenticationServiceRegistry());
			filter.setServletContext(context);
			filter.setRememberMeServices(new NullRememberMeServices());
			filter.setSignupUrl(signupUrl);
			
			ConnectionRepository repo = mock(ConnectionRepository.class);
			when(filter.getUsersConnectionRepository().createConnectionRepository(Mockito.anyString())).thenReturn(repo);
			
			auth = new SocialAuthenticationToken(DummyConnection.dummy("provider", "user"), null);

			Collection<? extends GrantedAuthority> authorities = Collections.emptyList();
			User user = new SocialUser("foo", "bar", authorities);
			authSuccess = new SocialAuthenticationToken(DummyConnection.dummy("provider", "user"), user, null, authorities);
		}

		private void addAuthService(SocialAuthenticationService<?> authenticationService) {
			((SocialAuthenticationServiceRegistry)filter.getAuthServiceLocator()).addAuthenticationService(authenticationService);
		}
		
		private void doFilter() throws Exception {

			filter.init(config);
			filter.doFilter(req, res, chain);
			filter.destroy();
		}
	}
}
