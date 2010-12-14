package org.springframework.social.connect;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.security.encrypt.SearchableStringEncryptor;
import org.springframework.security.encrypt.StringEncryptor;
import org.springframework.social.test.utils.SpringSocialTestDatabaseBuilder;
import org.springframework.social.twitter.TwitterOperations;

// TODO This is testing more than just the jdbc account connection repository - factor out and focus on the data access logic
public class JdbcAccountConnectionRepositoryIntegrationTest {

	private EmbeddedDatabase db;

	private JdbcTemplate jdbcTemplate;

	private ServiceProvider<TwitterOperations> serviceProvider;

	private JdbcServiceProviderFactory providerFactory;

	private FakeAccountIdResolver accountIdResolver;

	@Before
	public void setup() {
		db = new SpringSocialTestDatabaseBuilder().connectedAccount().testData(getClass()).getDatabase();
		jdbcTemplate = new JdbcTemplate(db);
		StringEncryptor encryptor = new SearchableStringEncryptor("secret", "5b8bd7612cdab5ed");
		accountIdResolver = new FakeAccountIdResolver();
		providerFactory = new JdbcServiceProviderFactory(jdbcTemplate, encryptor, accountIdResolver);
		serviceProvider = providerFactory.getServiceProvider("twitter", TwitterOperations.class);
	}

	@After
	public void destroy() {
		if (db != null) {
			db.shutdown();
		}
	}

	@Test
	public void addConnection() {
		accountIdResolver.setAccountId(2L);
		assertFalse(serviceProvider.isConnected());
		serviceProvider.addConnection("accessToken", "kdonald");
		assertTrue(serviceProvider.isConnected());
		TwitterOperations api = serviceProvider.getServiceOperations();
		assertNotNull(api);
	}

	@Test
	public void connected() {
		assertTrue(serviceProvider.isConnected());
	}

	@Test
	public void notConnected() {
		accountIdResolver.setAccountId(2L);
		assertFalse(serviceProvider.isConnected());
	}

	@Test
	public void getApi() {
		TwitterOperations api = serviceProvider.getServiceOperations();
		assertNotNull(api);
	}

	@Test
	public void getApiNotConnected() {
		accountIdResolver.setAccountId(2L);
		TwitterOperations api = serviceProvider.getServiceOperations();
		assertNotNull(api);
	}

	@Test
	public void getConnectedAccountId() {
		assertEquals("habuma", serviceProvider.getProviderAccountId());
	}

	@Test
	public void getConnectedAccountIdNotConnected() {
		accountIdResolver.setAccountId(2L);
		assertNull(serviceProvider.getProviderAccountId());
	}

	@Test
	public void disconnect() {
		assertTrue(serviceProvider.isConnected());
		serviceProvider.disconnect();
		assertFalse(serviceProvider.isConnected());
	}

}