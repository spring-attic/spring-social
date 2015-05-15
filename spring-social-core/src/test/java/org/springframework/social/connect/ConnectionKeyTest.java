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
package org.springframework.social.connect;

import static org.junit.Assert.*;

import org.junit.Test;

public class ConnectionKeyTest {
	
	@Test
	public void equals() {
		ConnectionKey key1 = new ConnectionKey("provider1", "providerUser1");
		ConnectionKey key2 = new ConnectionKey("provider1", "providerUser1");

		// reflexive
		assertEquals(key1, key1);
		
		// symmetric
		assertEquals(key1, key2);
		assertEquals(key2, key1);
	}

	@Test
	public void equals_nullProviderUserId() {
		ConnectionKey key1 = new ConnectionKey("provider1", null);
		ConnectionKey key2 = new ConnectionKey("provider1", null);

		// reflexive
		assertEquals(key1, key1);
		
		// symmetric
		assertEquals(key1, key2);
		assertEquals(key2, key1);
	}

	@Test
	public void equals_notEqual() {
		ConnectionKey key1 = new ConnectionKey("provider1", "providerUser1");
		ConnectionKey key2 = new ConnectionKey("provider2", "providerUser1");
		ConnectionKey key3 = new ConnectionKey("provider1", "providerUser2");
		ConnectionKey key4 = new ConnectionKey("provider1", null);
		
		assertFalse(key1.equals(key2));
		assertFalse(key2.equals(key1));
		assertFalse(key1.equals(key3));
		assertFalse(key3.equals(key1));
		assertFalse(key2.equals(key3));
		assertFalse(key3.equals(key2));
		assertFalse(key1.equals(key4));
		assertFalse(key4.equals(key1));
	}

	@Test
	public void hashCode_providerIdOnly() {
		ConnectionKey key = new ConnectionKey("provider1", null);
		assertEquals("provider1".hashCode(), key.hashCode());
	}

	@Test
	public void hashCode_providerIdAndProviderUserId() {
		ConnectionKey key = new ConnectionKey("provider1", "providerUser1");
		assertEquals("provider1".hashCode() + "providerUser1".hashCode(), key.hashCode());
	}

	@Test
	public void toString_providerIdAndProviderUserId() {
		ConnectionKey key = new ConnectionKey("provider1", "providerUser1");
		assertEquals("provider1:providerUser1", key.toString());
	}
	
	@Test
	public void toString_providerIdOnly() {
		ConnectionKey key = new ConnectionKey("provider1", null);
		assertEquals("provider1:null", key.toString());
	}
}
