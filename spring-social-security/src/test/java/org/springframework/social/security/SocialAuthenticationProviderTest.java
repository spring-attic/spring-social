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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.social.connect.ConnectionData;
import org.springframework.social.connect.UsersConnectionRepository;
import org.springframework.social.security.test.DummyUserDetails;

public class SocialAuthenticationProviderTest {

	@Test
	public void supports() {
		SocialAuthenticationProvider provider = new SocialAuthenticationProvider();
		assertTrue(provider.supports(SocialAuthenticationToken.class));
		assertFalse(provider.supports(Authentication.class));
	}

	@Test
	public void toUserId() {
		SocialAuthenticationProvider provider = new SocialAuthenticationProvider();
		UsersConnectionRepository repo = mock(UsersConnectionRepository.class);
		provider.setUsersConnectionRepository(repo);

		Mockito.when(repo.findUserIdsConnectedTo("provider", set("providerUser1"))).thenReturn(set("user1"));
		Mockito.when(repo.findUserIdsConnectedTo("provider", set("providerUser2"))).thenReturn(set("user1", "user2"));

		assertEquals("user1", provider.toUserId(data("providerUser1")));
		assertNull(provider.toUserId(data("providerUser2")));
	}

	@Test
	public void authenticate() {
		final SocialAuthenticationProvider provider = new SocialAuthenticationProvider();
		final UsersConnectionRepository repo = mock(UsersConnectionRepository.class);
		final SocialUserDetailsService userDetailsService = mock(SocialUserDetailsService.class);
		provider.setUsersConnectionRepository(repo);
		provider.setUserDetailsService(userDetailsService);

		final DummyUserDetails userDetails = new DummyUserDetails("user1", "pass", "moderator");
		
		// mapping from providerUserId to userId
		Mockito.when(repo.findUserIdsConnectedTo("provider", set("providerUser1"))).thenReturn(set("user1"));
		Mockito.when(repo.findUserIdsConnectedTo("provider", set("providerUser2"))).thenReturn(set("user1", "user2"));
		Mockito.when(repo.findUserIdsConnectedTo("provider", set("providerUser3"))).thenReturn(set("user3"));
		// mapping from userId to userDetails
		Mockito.when(userDetailsService.loadUserByUserId("user1")).thenReturn(userDetails);
		Mockito.when(userDetailsService.loadUserByUserId("user2")).thenReturn(new DummyUserDetails("user2", "pass", "moderator"));
		
		// success
		SocialAuthenticationToken token = new SocialAuthenticationToken(data("providerUser1"), null);
		SocialAuthenticationToken authToken = (SocialAuthenticationToken) provider.authenticate(token);
		
		assertNotNull(authToken);
		assertTrue(token.getPrincipal() instanceof ConnectionData);
		assertTrue(authToken.getPrincipal() instanceof UserDetails);
		assertEquals(userDetails.getUsername(), authToken.getName());
		assertEquals(token.getProviderId(), authToken.getProviderId());
		assertEquals(token.getProviderAccountData(), authToken.getProviderAccountData());
	
		// fail - unknown providerUserId
		try {
			provider.authenticate(new SocialAuthenticationToken(data("someUnknownProviderUser"), null));
			fail();
		} catch (BadCredentialsException e) {
			// expected			
		}
		
		// fail - multiple local users connected
		try {
			provider.authenticate(new SocialAuthenticationToken(data("providerUser2"), null));
			fail();
		} catch (BadCredentialsException e) {
			// expected
		}
		
		// fail - no details for connected user
		try {
			provider.authenticate(new SocialAuthenticationToken(data("providerUser3"), null));
			fail();
		} catch (UsernameNotFoundException e) {
			// expected
		}
	}

	private static ConnectionData data(String providerUserId) {
		return new ConnectionData("provider", providerUserId, null, null, null, null, null, null, null);
	}

	private static <T> Set<T> set(T... t) {
		return Collections.unmodifiableSet(new HashSet<T>(Arrays.asList(t)));
	}
}
