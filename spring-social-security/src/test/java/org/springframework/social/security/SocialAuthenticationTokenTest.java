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

import org.junit.Test;
import org.springframework.social.connect.Connection;
import org.springframework.social.security.test.DummyConnection;

public class SocialAuthenticationTokenTest {

	@Test
	public void testUnauthenticated() {
		final Connection<Object> dummyConnection = dummyConnection("user1");
		SocialAuthenticationToken token = new SocialAuthenticationToken(dummyConnection, null);

		assertFalse(token.isAuthenticated());
		assertTrue(token.getConnection() instanceof Connection);
		assertEquals(dummyConnection.createData().getProviderId(), token.getProviderId());
		assertEquals(dummyConnection, token.getConnection());
		assertEquals(0, token.getAuthorities().size());
		assertEquals(0, token.getProviderAccountData().size());
		assertNull(token.getCredentials());
		assertNull(token.getDetails());
	}

    private static Connection<Object> dummyConnection(String providerUserId) {
        return DummyConnection.dummy("provider", providerUserId);
    }

}
