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
package org.springframework.social.connect.oauth1;

import static org.junit.Assert.*;

import org.junit.Test;
import org.springframework.social.connect.ConnectionData;
import org.springframework.social.connect.FakeApi;
import org.springframework.social.connect.FakeApiAdapter;
import org.springframework.social.connect.support.OAuth1Connection;
import org.springframework.util.SerializationUtils;

public class OAuth1ConnectionTest {
	@Test
	public void oauth1ConnectionSerializable() throws Exception {
		OAuth1Connection<FakeApi> connection = new OAuth1Connection<FakeApi>(
				new ConnectionData("a", "b", "c", "d", "e", "f", "g", "h", 123L), 
				new FakeServiceProvider("i", "j"), new FakeApiAdapter());

		byte[] byteArray = SerializationUtils.serialize(connection);

		@SuppressWarnings("unchecked")
		OAuth1Connection<FakeApi> connectionFromArray = (OAuth1Connection<FakeApi>) SerializationUtils.deserialize(byteArray);
		assertEquals(connection, connectionFromArray);
	}
}
