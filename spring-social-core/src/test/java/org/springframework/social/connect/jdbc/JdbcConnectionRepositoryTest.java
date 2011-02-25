/*
 * Copyright 2011 the original author or authors.
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
package org.springframework.social.connect.jdbc;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.Serializable;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseFactory;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.security.crypto.encrypt.Encryptors;
import org.springframework.social.connect.support.Connection;

public class JdbcConnectionRepositoryTest {
	
	private EmbeddedDatabase db;

	private JdbcConnectionRepository repository;
	
	@Before
	public void setUp() {
		EmbeddedDatabaseFactory factory = new EmbeddedDatabaseFactory();
		factory.setDatabaseType(EmbeddedDatabaseType.H2);
		ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
		populator.addScript(new ClassPathResource("ConnectionRepositorySchema.sql", getClass()));
		factory.setDatabasePopulator(populator);
		db = factory.getDatabase();
		this.repository = new JdbcConnectionRepository(db, Encryptors.noOpText());
	}
	
	@After
	public void tearDown() {
		if (db != null) {
			db.shutdown();
		}
	}
	
	@Test
	public void saveConnection() {
		assertEquals(false, repository.isConnected(1L, "facebook"));
		repository.saveConnection(1L, "facebook", Connection.oauth2("123456789", "987654321", "abcdefg"));
		assertEquals(true, repository.isConnected(1L, "facebook"));		
		List<Connection> connections = repository.findConnections(1L, "facebook");
		assertEquals(1, connections.size());
		Connection c1 = connections.get(0);
		assertEquals((Long) 1L, c1.getId());
		assertEquals("123456789", c1.getAccessToken());
		assertEquals("987654321", c1.getRefreshToken());
		assertEquals("abcdefg", c1.getProviderAccountId());
	}

	@Test
	public void duplicateConnection() {
		repository.saveConnection(1L, "facebook", Connection.oauth2("123456789", "987654321", "abcdefg"));
		try {
			repository.saveConnection(1L, "facebook", Connection.oauth2("123456789", "987654321", "abcdefg"));
			fail("Should have failed");
		} catch (IllegalArgumentException e) {
			
		}
	}

	@Test
	public void findMultipleConnections() {
		repository.saveConnection(1L, "facebook", Connection.oauth2("123456789", "987654321", "abcdefg"));
		repository.saveConnection(1L, "facebook", Connection.oauth2("023456789", "987654320", "gfedcba"));
		List<Connection> connections = repository.findConnections(1L, "facebook");
		assertEquals(2, connections.size());
		Connection c1 = connections.get(0);
		assertEquals((Long) 1L, c1.getId());
		assertEquals("123456789", c1.getAccessToken());
		assertEquals("987654321", c1.getRefreshToken());
		assertEquals("abcdefg", c1.getProviderAccountId());
		assertNull(c1.getSecret());
		Connection c2 = connections.get(1);
		assertEquals((Long) 2L, c2.getId());
		assertEquals("023456789", c2.getAccessToken());
		assertEquals("987654320", c2.getRefreshToken());
		assertEquals("gfedcba", c2.getProviderAccountId());
		assertNull(c1.getSecret());
	}
	
	@Test
	public void findNoConnections() {
		List<Connection> connections = repository.findConnections(1L, "facebook");
		assertEquals(0, connections.size());		
	}

	@Test
	public void removeConnection() {
		assertEquals(false, repository.isConnected(1L, "facebook"));		
		Connection connection = repository.saveConnection(1L, "facebook",
				Connection.oauth2("123456789", "987654321", "abcdefg"));
		assertEquals(true, repository.isConnected(1L, "facebook"));				
		repository.removeConnection(1L, "facebook", connection.getId());
		assertEquals(false, repository.isConnected(1L, "facebook"));
		List<Connection> connections = repository.findConnections(1L, "facebook");
		assertEquals(0, connections.size());		
	}

	@Test
	public void findAccountIdByConnectionAccessToken() {
		assertNull(repository.findAccountIdByConnectionAccessToken("facebook", "access_token"));
		assertEquals(false, repository.isConnected("rclarkson", "twitter"));
		repository.saveConnection("rclarkson", "twitter", Connection.oauth1("access_token", "token_secret", "abcdefg"));
		assertEquals(true, repository.isConnected("rclarkson", "twitter"));
		assertEquals("rclarkson", repository.findAccountIdByConnectionAccessToken("twitter", "access_token"));
	}

	@Test
	public void findAccountIdsForProviderAccountIds() {
		repository.saveConnection(1L, "facebook", Connection.oauth2("123456789", "987654321", "abcdefg"));
		repository.saveConnection(2L, "facebook", Connection.oauth2("023456789", "987654320", "gfedcba"));
		repository.saveConnection(3L, "facebook", Connection.oauth2("023456789", "987654320", "hijklmn"));

		List<Serializable> accountIds = repository.findAccountIdsForProviderAccountIds("facebook", asList("abcdefg", "gfedcba", "tuvwxyz"));
		assertEquals(2, accountIds.size());
		for (Serializable accountId : accountIds) {
			System.out.println(accountId);
		}
		assertTrue(accountIds.contains("1"));
		assertTrue(accountIds.contains("2"));
	}
}
