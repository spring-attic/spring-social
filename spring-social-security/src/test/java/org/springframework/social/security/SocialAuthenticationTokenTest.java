/*
 * Copyright 2012 the original author or authors.
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

import org.junit.Test;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.social.connect.ConnectionData;
import org.springframework.social.security.test.DummyUserDetails;

public class SocialAuthenticationTokenTest {

	@Test
	public void testUnauthenticated() {
		final ConnectionData data = data("user1");
		SocialAuthenticationToken token = new SocialAuthenticationToken(data, null);

		assertFalse(token.isAuthenticated());
		assertTrue(token.getPrincipal() instanceof ConnectionData);
		assertEquals(data.getProviderId(), token.getProviderId());
		assertEquals(data, token.getPrincipal());
		assertEquals(0, token.getAuthorities().size());
		assertEquals(0, token.getProviderAccountData().size());
		assertNull(token.getCredentials());
		assertNull(token.getDetails());
	}

	@Test
	public void testAuthenticated() {
		SocialAuthenticationToken token = new SocialAuthenticationToken("provider", new DummyUserDetails("user", "pass", "moderator"), null);

		assertTrue(token.isAuthenticated());
		assertTrue(token.getPrincipal() instanceof UserDetails);
		assertEquals(1, token.getAuthorities().size());
		assertEquals(0, token.getProviderAccountData().size());
		assertNull(token.getCredentials());
		assertNull(token.getDetails());
	}
	private static ConnectionData data(String providerUserId) {
		return new ConnectionData("provider", providerUserId, null, null, null, null, null, null, null);
	}

}
