package org.springframework.social.connect.jdbc;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseFactory;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.security.crypto.encrypt.Encryptors;
import org.springframework.social.connect.ServiceProviderConnection;
import org.springframework.social.connect.ServiceProviderConnectionKey;
import org.springframework.social.connect.ServiceProviderUser;
import org.springframework.social.connect.spi.ServiceApiAdapter;
import org.springframework.social.connect.support.LocalUserIdLocator;
import org.springframework.social.connect.support.MapServiceProviderConnectionFactoryRegistry;
import org.springframework.social.connect.support.OAuth2ServiceProviderConnectionFactory;
import org.springframework.social.oauth2.AccessGrant;
import org.springframework.social.oauth2.OAuth2Operations;
import org.springframework.social.oauth2.OAuth2ServiceProvider;

public class JdbcServiceProviderConnectionRepositoryTest {

	private EmbeddedDatabase database;
	
	private JdbcServiceProviderConnectionRepository connectionRepository;

	private MapServiceProviderConnectionFactoryRegistry connectionFactoryRegistry;
	
	private FacebookServiceProviderConnectionFactory connectionFactory;
	
	private Long localUserId = 1L;
	
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
		connectionRepository = new JdbcServiceProviderConnectionRepository(database, connectionFactoryRegistry, new LocalUserIdLocator() {
			public Serializable getLocalUserId() {
				return localUserId;
			}
		}, Encryptors.noOpText());		
	}

	@After
	public void tearDown() {
		if (database != null) {
			database.shutdown();
		}
	}

	@Test
	public void findConnectionsEmptyResult() {
		assertTrue(connectionRepository.findConnections().isEmpty());
	}
	
	@Test
	public void findConnectionsToProviderEmptyResult() {
		assertTrue(connectionRepository.findConnectionsToProvider("facebook").isEmpty());
	}
	
	@Test
	public void findConnectionsByProviderUserEmptyResult() {
		Map<String, List<String>> providerUsers = new HashMap<String, List<String>>();
		providerUsers.put("facebook", Collections.singletonList("1"));
		assertTrue(connectionRepository.findConnectionsForUsers(providerUsers).isEmpty());
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void findConnectionsByIdEmptyInput() {
		Map<String, List<String>> providerUsers = new HashMap<String, List<String>>();
		connectionRepository.findConnectionsForUsers(providerUsers);
	}
	
	@Test
	public void removeConnectionsNoOp() {
		connectionRepository.removeConnectionsToProvider("twitter");
	}

	@Test
	public void removeConnectionNoOp() {
		connectionRepository.removeConnectionWithKey(new ServiceProviderConnectionKey("facebook", "1"));
	}

	@Test
	public void addConnection() {
		ServiceProviderConnection<FacebookApi> connection = connectionFactory.createConnection(new AccessGrant("123456789", 3600, "987654321", null, null));
		connectionRepository.addConnection(connection);
		ServiceProviderConnection<FacebookApi> restoredConnection = connectionRepository.findConnectionByServiceApi(FacebookApi.class);
		assertEquals(connection, restoredConnection);	
		assertConnection(restoredConnection);
	}
		
	private void assertConnection(ServiceProviderConnection<FacebookApi> connection) {
		assertEquals("facebook", connection.getKey().getProviderId());
		assertEquals("1", connection.getKey().getProviderUserId());
		ServiceProviderUser user = connection.getUser();
		assertEquals("Keith Donald", user.getProfileName());
		assertEquals("http://facebook.com/keith.donald", user.getProfileUrl());
		assertEquals("http://facebook.com/keith.donald/picture", user.getProfilePictureUrl());
		assertTrue(connection.test());
		FacebookApi api = connection.getServiceApi();
		assertNotNull(api);
		assertEquals("123456789", api.getAccessToken());
		assertEquals("123456789", connection.createData().getAccessToken());
		assertEquals("987654321", connection.createData().getRefreshToken());
	}
	
	private static class FacebookServiceProviderConnectionFactory extends OAuth2ServiceProviderConnectionFactory<FacebookApi> {

		public FacebookServiceProviderConnectionFactory() {
			super("facebook", new FacebookServiceProvider(), new FacebookServiceApiAdapter());
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
		
	public interface FacebookApi {
		
		String getAccessToken();
		
	}
	
	private static class FacebookServiceApiAdapter implements ServiceApiAdapter<FacebookApi> {

		private String accountId = "1";
		
		private String name = "Keith Donald";
		
		private String profileUrl = "http://facebook.com/keith.donald";
		
		private String profilePicture = "http://facebook.com/keith.donald/picture";
		
		public boolean test(FacebookApi serviceApi) {
			return true;
		}

		public ServiceProviderUser getUser(FacebookApi serviceApi) {
			return new ServiceProviderUser(accountId, name, profileUrl, profilePicture);
		}

		public void updateStatus(FacebookApi serviceApi, String message) {
			
		}
		
	}
	
}
