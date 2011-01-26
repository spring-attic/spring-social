package org.springframework.social.provider.jdbc;

import static org.junit.Assert.*;

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
import org.springframework.social.provider.support.Connection;

public class JdbcConnectionRepositoryTest {
	
	private EmbeddedDatabase db;

	private JdbcConnectionRepository repository;
	
	@Before
	public void setUp() {
		EmbeddedDatabaseFactory factory = new EmbeddedDatabaseFactory();
		factory.setDatabaseType(EmbeddedDatabaseType.H2);
		ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
		populator.addScript(new ClassPathResource("Schema.sql", getClass()));
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
		repository.saveConnection(1L, "facebook", Connection.oauth2("123456789", "987654321"));
		assertEquals(true, repository.isConnected(1L, "facebook"));		
		List<Connection> connections = repository.findConnections(1L, "facebook");
		assertEquals(1, connections.size());
		Connection c1 = connections.get(0);
		assertEquals((Long) 1L, c1.getId());
		assertEquals("123456789", c1.getAccessToken());
		assertEquals("987654321", c1.getRefreshToken());
	}

	@Test
	public void duplicateConnection() {
		repository.saveConnection(1L, "facebook", Connection.oauth2("123456789", "987654321"));
		try {
			repository.saveConnection(1L, "facebook", Connection.oauth2("123456789", "987654321"));
			fail("Should have failed");
		} catch (IllegalArgumentException e) {
			
		}
	}

	@Test
	public void findMultipleConnections() {
		repository.saveConnection(1L, "facebook", Connection.oauth2("123456789", "987654321"));
		repository.saveConnection(1L, "facebook", Connection.oauth2("023456789", "987654320"));
		List<Connection> connections = repository.findConnections(1L, "facebook");
		assertEquals(2, connections.size());
		Connection c1 = connections.get(0);
		assertEquals((Long) 1L, c1.getId());
		assertEquals("123456789", c1.getAccessToken());
		assertEquals("987654321", c1.getRefreshToken());
		assertNull(c1.getSecret());
		Connection c2 = connections.get(1);
		assertEquals((Long) 2L, c2.getId());
		assertEquals("023456789", c2.getAccessToken());
		assertEquals("987654320", c2.getRefreshToken());
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
		Connection connection = repository.saveConnection(1L, "facebook", Connection.oauth2("123456789", "987654321"));
		assertEquals(true, repository.isConnected(1L, "facebook"));				
		repository.removeConnection(1L, "facebook", connection.getId());
		assertEquals(false, repository.isConnected(1L, "facebook"));
		List<Connection> connections = repository.findConnections(1L, "facebook");
		assertEquals(0, connections.size());		
	}

}
