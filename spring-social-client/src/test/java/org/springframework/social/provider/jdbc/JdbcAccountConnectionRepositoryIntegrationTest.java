package org.springframework.social.provider.jdbc;

import static org.junit.Assert.*;

import java.util.Collection;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.security.encrypt.SearchableStringEncryptor;
import org.springframework.security.encrypt.StringEncryptor;
import org.springframework.social.provider.AccountConnection;
import org.springframework.social.provider.ServiceProvider;
import org.springframework.social.test.utils.SpringSocialTestDatabaseBuilder;

// TODO This is testing more than just the JDBC account connection repository - factor out and focus on the data access logic
public class JdbcAccountConnectionRepositoryIntegrationTest {

	private EmbeddedDatabase db;

	private JdbcTemplate jdbcTemplate;

	private ServiceProvider<TestOperations> serviceProvider;

	private JdbcServiceProviderFactory providerFactory;

	@Before
	public void setup() {
		db = new SpringSocialTestDatabaseBuilder().connectedAccount().testData(getClass()).getDatabase();
		jdbcTemplate = new JdbcTemplate(db);
		StringEncryptor encryptor = new SearchableStringEncryptor("secret", "5b8bd7612cdab5ed");
		providerFactory = new JdbcServiceProviderFactory(jdbcTemplate, encryptor);
		serviceProvider = providerFactory.getServiceProvider("twitter", TestOperations.class);
	}

	@After
	public void destroy() {
		if (db != null) {
			db.shutdown();
		}
	}

	@Test
	public void addConnection() {
		assertFalse(serviceProvider.isConnected(2L));
		serviceProvider.addConnection(2L, "accessToken", "kdonald");
		assertTrue(serviceProvider.isConnected(2L));
	}

	@Test
	public void connected() {
		assertTrue(serviceProvider.isConnected(1L));
	}

	@Test
	public void notConnected() {
		assertFalse(serviceProvider.isConnected(2L));
	}

	@Test
	public void getConnectedAccountId() {
		assertEquals("cwalls", serviceProvider.getProviderAccountId(1L));
	}

	@Test
	public void getConnectedAccountIdNotConnected() {
		assertNull(serviceProvider.getProviderAccountId(2L));
	}

	@Test
	public void disconnect() {
		assertTrue(serviceProvider.isConnected(1L));
		serviceProvider.disconnect(1L);
		assertFalse(serviceProvider.isConnected(1L));
	}

	@Test
	public void getConnections() {
		Collection<AccountConnection> connections = serviceProvider.getConnections(1L);
		assertEquals(2, connections.size());
	}

}