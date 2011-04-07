package org.springframework.social.connect.jdbc;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
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
import org.springframework.social.connect.support.OAuth1ServiceProviderConnectionFactory;
import org.springframework.social.connect.support.OAuth2ServiceProviderConnectionFactory;
import org.springframework.social.oauth1.OAuth1Operations;
import org.springframework.social.oauth1.OAuth1ServiceProvider;
import org.springframework.social.oauth2.AccessGrant;
import org.springframework.social.oauth2.OAuth2Operations;
import org.springframework.social.oauth2.OAuth2ServiceProvider;

public class JdbcServiceProviderConnectionRepositoryTest {

	private EmbeddedDatabase database;
	
	private JdbcServiceProviderConnectionRepository connectionRepository;

	private MapServiceProviderConnectionFactoryRegistry connectionFactoryRegistry;
	
	private TestFacebookServiceProviderConnectionFactory connectionFactory;
	
	private Long localUserId = 1L;
	
	private JdbcTemplate dataAccessor;
	
	@Before
	public void setUp() {
		EmbeddedDatabaseFactory factory = new EmbeddedDatabaseFactory();
		factory.setDatabaseType(EmbeddedDatabaseType.H2);
		ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
		populator.addScript(new ClassPathResource("JdbcServiceProviderConnectionRepositorySchema.sql", getClass()));
		factory.setDatabasePopulator(populator);
		database = factory.getDatabase();
		dataAccessor = new JdbcTemplate(database);
		connectionFactoryRegistry = new MapServiceProviderConnectionFactoryRegistry();
		connectionFactory = new TestFacebookServiceProviderConnectionFactory();
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
	@SuppressWarnings("unchecked")
	public void findAllConnections() {
		connectionFactoryRegistry.addConnectionFactory(new TestTwitterServiceProviderConnectionFactory());
		insertTwitterConnection();
		insertFacebookConnection();
		List<ServiceProviderConnection<?>> connections = connectionRepository.findAllConnections();
		assertEquals(2, connections.size());
		ServiceProviderConnection<TestFacebookApi> facebook = (ServiceProviderConnection<TestFacebookApi>) connections.get(0);
		assertFacebookConnection(facebook);
		ServiceProviderConnection<TestTwitterApi> twitter = (ServiceProviderConnection<TestTwitterApi>) connections.get(1);
		assertTwitterConnection(twitter);
	}

	@Test
	public void findAllConnectionsEmptyResult() {
		assertTrue(connectionRepository.findAllConnections().isEmpty());
	}

	@Test(expected=IllegalArgumentException.class)
	public void findAllConnectionsNoProviderRegistered() {
		insertTwitterConnection();
		connectionRepository.findAllConnections();	
	}

	@Test
	@SuppressWarnings("unchecked")
	public void findConnectionsToProvider() {
		connectionFactoryRegistry.addConnectionFactory(new TestTwitterServiceProviderConnectionFactory());
		insertTwitterConnection();
		List<ServiceProviderConnection<?>> connections = connectionRepository.findConnectionsToProvider("twitter");
		assertEquals(1, connections.size());
		assertTwitterConnection((ServiceProviderConnection<TestTwitterApi>) connections.get(0));
	}
	
	@Test
	public void findConnectionsToProviderEmptyResult() {
		assertTrue(connectionRepository.findConnectionsToProvider("facebook").isEmpty());
	}

	@Test
	@SuppressWarnings("unchecked")
	public void findConnectionsForUsersEmpty() {
		connectionFactoryRegistry.addConnectionFactory(new TestTwitterServiceProviderConnectionFactory());
		insertTwitterConnection();
		insertFacebookConnection();
		insertFacebookConnection2();
		Map<String, List<String>> providerUsers = new LinkedHashMap<String, List<String>>();
		providerUsers.put("facebook", Arrays.asList("9", "10"));
		providerUsers.put("twitter", Arrays.asList("1"));
		List<ServiceProviderConnection<?>> connections = connectionRepository.findConnectionsForUsers(providerUsers);
		assertEquals(3, connections.size());
		assertFacebookConnection((ServiceProviderConnection<TestFacebookApi>) connections.get(0));
	}
	
	@Test
	public void findConnectionsForUsersEmptyResult() {
		Map<String, List<String>> providerUsers = new HashMap<String, List<String>>();
		providerUsers.put("facebook", Collections.singletonList("1"));
		assertTrue(connectionRepository.findConnectionsForUsers(providerUsers).isEmpty());
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void findConnectionsForUsersEmptyInput() {
		Map<String, List<String>> providerUsers = new HashMap<String, List<String>>();
		connectionRepository.findConnectionsForUsers(providerUsers);
	}
	
	@Test
	public void removeConnectionsToProviderNoOp() {
		connectionRepository.removeConnectionsToProvider("twitter");
	}

	@Test
	public void removeConnectionNoOp() {
		connectionRepository.removeConnection(new ServiceProviderConnectionKey("facebook", "1"));
	}

	@Test
	public void addConnection() {
		ServiceProviderConnection<TestFacebookApi> connection = connectionFactory.createConnection(new AccessGrant("123456789", null, "987654321", System.currentTimeMillis() + 3600 * 1000));
		connectionRepository.addConnection(connection);
		ServiceProviderConnection<TestFacebookApi> restoredConnection = connectionRepository.findConnectionByServiceApi(TestFacebookApi.class);
		assertEquals(connection, restoredConnection);	
		assertNewConnection(restoredConnection);
	}
		
	private void insertTwitterConnection() {
		dataAccessor.update("insert into ServiceProviderConnection (localUserId, providerId, providerUserId, rank, profileName, profileUrl, profilePictureUrl, accessToken, secret, refreshToken, expireTime) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
				1L, "twitter", "1", 1, "@kdonald", "http://twitter.com/kdonald", "http://twitter.com/kdonald/picture", "123456789", "987654321", null, null);
	}
	
	private void insertFacebookConnection() {
		dataAccessor.update("insert into ServiceProviderConnection (localUserId, providerId, providerUserId, rank, profileName, profileUrl, profilePictureUrl, accessToken, secret, refreshToken, expireTime) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
				1L, "facebook", "9", 1, null, null, null, "234567890", null, "345678901", System.currentTimeMillis() + 3600000);
	}
	
	private void insertFacebookConnection2() {
		dataAccessor.update("insert into ServiceProviderConnection (localUserId, providerId, providerUserId, rank, profileName, profileUrl, profilePictureUrl, accessToken, secret, refreshToken, expireTime) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
				1L, "facebook", "10", 2, null, null, null, "456789012", null, "56789012", System.currentTimeMillis() + 3600000);
	}
	
	private void assertNewConnection(ServiceProviderConnection<TestFacebookApi> connection) {
		assertEquals("facebook", connection.getKey().getProviderId());
		assertEquals("9", connection.getKey().getProviderUserId());
		ServiceProviderUser user = connection.getUser();
		assertEquals("Keith Donald", user.getProfileName());
		assertEquals("http://facebook.com/keith.donald", user.getProfileUrl());
		assertEquals("http://facebook.com/keith.donald/picture", user.getProfilePictureUrl());
		assertTrue(connection.test());
		TestFacebookApi api = connection.getServiceApi();
		assertNotNull(api);
		assertEquals("123456789", api.getAccessToken());
		assertEquals("123456789", connection.createData().getAccessToken());
		assertEquals("987654321", connection.createData().getRefreshToken());
	}

	private void assertTwitterConnection(ServiceProviderConnection<TestTwitterApi> twitter) {
		assertEquals(new ServiceProviderConnectionKey("twitter", "1"), twitter.getKey());
		assertEquals("@kdonald", twitter.getUser().getProfileName());
		assertEquals("http://twitter.com/kdonald", twitter.getUser().getProfileUrl());
		assertEquals("http://twitter.com/kdonald/picture", twitter.getUser().getProfilePictureUrl());
		TestTwitterApi twitterApi = twitter.getServiceApi();
		assertEquals("123456789", twitterApi.getAccessToken());		
		assertEquals("987654321", twitterApi.getSecret());
		twitter.sync();
		assertEquals("http://twitter.com/kdonald/a_new_picture", twitter.getUser().getProfilePictureUrl());
	}

	private void assertFacebookConnection(ServiceProviderConnection<TestFacebookApi> facebook) {
		assertEquals(new ServiceProviderConnectionKey("facebook", "9"), facebook.getKey());
		assertEquals("9", facebook.getUser().getId());
		assertEquals(null, facebook.getUser().getProfileName());
		assertEquals(null, facebook.getUser().getProfileUrl());
		assertEquals(null, facebook.getUser().getProfilePictureUrl());
		TestFacebookApi facebookApi = facebook.getServiceApi();
		assertEquals("234567890", facebookApi.getAccessToken());
		facebook.sync();
		assertEquals("9", facebook.getUser().getId());
		assertEquals("Keith Donald", facebook.getUser().getProfileName());
		assertEquals("http://facebook.com/keith.donald", facebook.getUser().getProfileUrl());
		assertEquals("http://facebook.com/keith.donald/picture", facebook.getUser().getProfilePictureUrl());		
	}
	
	// test facebook provider
	
	private static class TestFacebookServiceProviderConnectionFactory extends OAuth2ServiceProviderConnectionFactory<TestFacebookApi> {

		public TestFacebookServiceProviderConnectionFactory() {
			super("facebook", new TestFacebookServiceProvider(), new TestFacebookServiceApiAdapter());
		}
		
	}

	private static class TestFacebookServiceProvider implements OAuth2ServiceProvider<TestFacebookApi> {

		public OAuth2Operations getOAuthOperations() {
			return null;
		}

		public TestFacebookApi getServiceApi(final String accessToken) {
			return new TestFacebookApi() {
				public String getAccessToken() {
					return accessToken;
				}
			};
		}
		
	}
		
	public interface TestFacebookApi {
		
		String getAccessToken();
		
	}
	
	private static class TestFacebookServiceApiAdapter implements ServiceApiAdapter<TestFacebookApi> {

		private String accountId = "9";
		
		private String name = "Keith Donald";
		
		private String profileUrl = "http://facebook.com/keith.donald";
		
		private String profilePicture = "http://facebook.com/keith.donald/picture";
		
		public boolean test(TestFacebookApi serviceApi) {
			return true;
		}

		public ServiceProviderUser getUser(TestFacebookApi serviceApi) {
			return new ServiceProviderUser(accountId, name, profileUrl, profilePicture);
		}

		public void updateStatus(TestFacebookApi serviceApi, String message) {
			
		}
		
	}
	
	// test twitter provider
	
	private static class TestTwitterServiceProviderConnectionFactory extends OAuth1ServiceProviderConnectionFactory<TestTwitterApi> {

		public TestTwitterServiceProviderConnectionFactory() {
			super("twitter", new TestTwitterServiceProvider(), new TestTwitterServiceApiAdapter());
		}
		
	}

	private static class TestTwitterServiceProvider implements OAuth1ServiceProvider<TestTwitterApi> {

		public OAuth1Operations getOAuthOperations() {
			return null;
		}

		public TestTwitterApi getServiceApi(final String accessToken, final String secret) {
			return new TestTwitterApi() {
				public String getAccessToken() {
					return accessToken;
				}
				public String getSecret() {
					return secret;
				}
			};
		}
		
	}
		
	public interface TestTwitterApi {
		
		String getAccessToken();
		
		String getSecret();
		
	}
	
	private static class TestTwitterServiceApiAdapter implements ServiceApiAdapter<TestTwitterApi> {

		private String accountId = "1";
		
		private String name = "@kdonald";
		
		private String profileUrl = "http://twitter.com/kdonald";
		
		private String profilePicture = "http://twitter.com/kdonald/a_new_picture";
		
		public boolean test(TestTwitterApi serviceApi) {
			return true;
		}

		public ServiceProviderUser getUser(TestTwitterApi serviceApi) {
			return new ServiceProviderUser(accountId, name, profileUrl, profilePicture);
		}

		public void updateStatus(TestTwitterApi serviceApi, String message) {
		}
		
	}
	
}
