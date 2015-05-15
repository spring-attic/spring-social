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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.UsersConnectionRepository;
import org.springframework.social.security.test.DummyConnection;
import org.springframework.social.security.test.DummyUserDetails;

public class SocialAuthenticationProviderTest {

	@Test
	public void supports() {
		SocialAuthenticationProvider provider = new SocialAuthenticationProvider(null, null);
		assertTrue(provider.supports(SocialAuthenticationToken.class));
		assertFalse(provider.supports(Authentication.class));
	}

	@Test
	public void toUserId() {
		UsersConnectionRepository repo = mock(UsersConnectionRepository.class);
		SocialAuthenticationProvider provider = new SocialAuthenticationProvider(repo, null);
		Connection<Object> dummyConnection1 = dummyConnection("providerUser1");
		Connection<Object> dummyConnection2 = dummyConnection("providerUser2");
		
		Mockito.when(repo.findUserIdsWithConnection(dummyConnection1)).thenReturn(list("user1"));
        Mockito.when(repo.findUserIdsWithConnection(dummyConnection2)).thenReturn(list("user1", "user2"));
        
		assertEquals("user1", provider.toUserId(dummyConnection1));
		assertNull(provider.toUserId(dummyConnection2));
	}

	@Test
	public void authenticate() {
		UsersConnectionRepository repo = mock(UsersConnectionRepository.class);
		SocialUserDetailsService userDetailsService = mock(SocialUserDetailsService.class);
		SocialAuthenticationProvider provider = new SocialAuthenticationProvider(repo, userDetailsService);

		DummyUserDetails userDetails = new DummyUserDetails("user1", "pass", "moderator");
		Connection<Object> dummyConnection1 = dummyConnection("providerUser1");
		Connection<Object> dummyConnection2 = dummyConnection("providerUser2");
		Connection<Object> dummyConnection3 = dummyConnection("providerUser3");
		
        // mapping from Connection to userId
        Mockito.when(repo.findUserIdsWithConnection(dummyConnection1)).thenReturn(list("user1"));
        Mockito.when(repo.findUserIdsWithConnection(dummyConnection2)).thenReturn(list("user1", "user2"));
        Mockito.when(repo.findUserIdsWithConnection(dummyConnection3)).thenReturn(list("user3"));
		// mapping from userId to userDetails
		Mockito.when(userDetailsService.loadUserByUserId("user1")).thenReturn(userDetails);
		Mockito.when(userDetailsService.loadUserByUserId("user2")).thenReturn(new DummyUserDetails("user2", "pass", "moderator"));
		
		// success
		SocialAuthenticationToken token = new SocialAuthenticationToken(dummyConnection1, null);
		SocialAuthenticationToken authToken = (SocialAuthenticationToken) provider.authenticate(token);
		
		assertNotNull(authToken);
		assertTrue(token.getPrincipal() == null);
		assertEquals(token.getConnection(), dummyConnection1);
		assertTrue(authToken.getPrincipal() instanceof UserDetails);
		assertEquals(userDetails.getUsername(), authToken.getName());
		assertEquals(token.getProviderId(), authToken.getProviderId());
		assertEquals(token.getProviderAccountData(), authToken.getProviderAccountData());
	
		// fail - unknown providerUserId
		try {
			provider.authenticate(new SocialAuthenticationToken(dummyConnection("someUnknownProviderUser"), null));
			fail();
		} catch (BadCredentialsException e) {
			// expected			
		}
		
		// fail - multiple local users connected
		try {
			provider.authenticate(new SocialAuthenticationToken(dummyConnection2, null));
			fail();
		} catch (BadCredentialsException e) {
			// expected
		}
		
		// fail - no details for connected user
		try {
			provider.authenticate(new SocialAuthenticationToken(dummyConnection3, null));
			fail();
		} catch (UsernameNotFoundException e) {
			// expected
		}
	}

    private static Connection<Object> dummyConnection(String providerUserId) {
        return DummyConnection.dummy("provider", providerUserId);
    }

    private static List<String> list(String... t) {
        return Collections.unmodifiableList(new ArrayList<String>(Arrays.asList(t)));
    }
}
