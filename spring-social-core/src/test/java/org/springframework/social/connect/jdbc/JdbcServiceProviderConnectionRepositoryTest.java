package org.springframework.social.connect.jdbc;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseFactory;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.security.crypto.encrypt.Encryptors;
import org.springframework.social.connect.MapServiceProviderConnectionFactoryRegistry;
import org.springframework.social.connect.OAuth2ServiceProviderConnectionFactory;
import org.springframework.social.connect.ServiceProviderConnection;
import org.springframework.social.connect.spi.ProviderProfile;
import org.springframework.social.connect.spi.ServiceApiAdapter;
import org.springframework.social.oauth2.AccessGrant;
import org.springframework.social.oauth2.OAuth2Operations;
import org.springframework.social.oauth2.OAuth2ServiceProvider;

public class JdbcServiceProviderConnectionRepositoryTest {

	private EmbeddedDatabase database;
	
	private JdbcServiceProviderConnectionRepository connectionRepository;

	private MapServiceProviderConnectionFactoryRegistry connectionFactoryRegistry;
	
	private FacebookServiceProviderConnectionFactory connectionFactory;
	
	@Before
	public void setUp() {
		EmbeddedDatabaseFactory factory = new EmbeddedDatabaseFactory();
		factory.setDatabaseType(EmbeddedDatabaseType.H2);
		ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
		populator.addScript(new ClassPathResource("JdbcServiceProviderConnectionRepositorySchema.sql", getClass()));
		factory.setDatabasePopulator(populator);
		database = factory.getDatabase();
		connectionFactoryRegistry = new MapServiceProviderConnectionFactoryRegistry();
		connectionFactory = new FacebookServiceProviderConnectionFactory();
		connectionFactoryRegistry.addConnectionFactory(connectionFactory);
		this.connectionRepository = new JdbcServiceProviderConnectionRepository(database, Encryptors.noOpText(), connectionFactoryRegistry);		
	}

	@After
	public void tearDown() {
		if (database != null) {
			database.shutdown();
		}
	}

	@Test
	public void findConnectionsEmptyResult() {
		assertTrue(connectionRepository.findConnections(1L).isEmpty());
	}
	
	@Test
	public void findConnectionsToProviderEmptyResult() {
		assertTrue(connectionRepository.findConnectionsToProvider(1L, "facebook").isEmpty());
	}
	
	@Test
	public void findConnectionsByIdEmptyResult() {
		assertTrue(connectionRepository.findConnectionsById(1L, Arrays.asList(new Long[] { 1L, 2L })).isEmpty());
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void findConnectionsByIdEmptyInput() {
		connectionRepository.findConnectionsById(1L, Arrays.asList(new Long[0]));
	}
	
	@Test
	public void findConnectionsToProviderAccountEmptyResult() {
		assertTrue(connectionRepository.findConnectionsToProviderAccount("twitter", "1").isEmpty());
	}
	
	@Test
	public void removeConnectionsNoOp() {
		connectionRepository.removeConnectionsToProvider(1L, "twitter");
	}

	@Test
	public void removeConnectionNoOp() {
		connectionRepository.removeConnection(1L, 1L);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void saveNewConnectionNoAccount() {
		connectionRepository.saveConnection(connectionFactory.createConnection(new AccessGrant("123456789", null)));
	}
	
	@Test
	public void saveNewConnectionAccount() {
		ServiceProviderConnection<FacebookApi> newConnection = connectionFactory.createConnection(new AccessGrant("123456789", "987654321")).assignAccountId(1L);
		ServiceProviderConnection<FacebookApi> connection = connectionRepository.saveConnection(newConnection);
		assertTrue(!newConnection.equals(connection));
		assertTrue(newConnection.hashCode() != connection.hashCode());
		assertEquals(connection, connection.assignAccountId(1L));
	}
	
	private static class FacebookServiceProviderConnectionFactory extends OAuth2ServiceProviderConnectionFactory<FacebookApi> {

		public FacebookServiceProviderConnectionFactory() {
			super("facebook", new FacebookServiceProvider(), new FacebookServiceApiAdapter(), true);
		}
		
	}

	private static class FacebookServiceProvider implements OAuth2ServiceProvider<FacebookApi> {

		public OAuth2Operations getOAuthOperations() {
			return null;
		}

		public FacebookApi getServiceApi(final String accessToken) {
			return new FacebookApi() {
				public String getAccessToken() {
					return accessToken;
				}
			};
		}
		
	}
		
	private interface FacebookApi {
		
		String getAccessToken();
		
	}
	
	private static class FacebookServiceApiAdapter implements ServiceApiAdapter<FacebookApi> {

		public boolean test(FacebookApi serviceApi) {
			return true;
		}

		public ProviderProfile getProfile(FacebookApi serviceApi) {
			return new ProviderProfile("1", "Keith Donald", "http://facebook.com/keith.donald", "http://facebook.com/keith.donald/picture");
		}

		public void updateStatus(FacebookApi serviceApi, String message) {
			
		}
		
	}
	
}
