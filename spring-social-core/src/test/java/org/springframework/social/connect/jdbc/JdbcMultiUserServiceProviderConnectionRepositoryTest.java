package org.springframework.social.connect.jdbc;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

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
import org.springframework.social.connect.DuplicateServiceProviderConnectionException;
import org.springframework.social.connect.NoSuchServiceProviderConnectionException;
import org.springframework.social.connect.ServiceProviderConnection;
import org.springframework.social.connect.ServiceProviderConnectionData;
import org.springframework.social.connect.ServiceProviderConnectionKey;
import org.springframework.social.connect.ServiceProviderConnectionRepository;
import org.springframework.social.connect.ServiceProviderUser;
import org.springframework.social.connect.spi.ServiceApiAdapter;
import org.springframework.social.connect.support.MapServiceProviderConnectionFactoryRegistry;
import org.springframework.social.connect.support.OAuth1ServiceProviderConnectionFactory;
import org.springframework.social.connect.support.OAuth2ServiceProviderConnectionFactory;
import org.springframework.social.oauth1.OAuth1Operations;
import org.springframework.social.oauth1.OAuth1ServiceProvider;
import org.springframework.social.oauth2.AccessGrant;
import org.springframework.social.oauth2.GrantType;
import org.springframework.social.oauth2.OAuth2Operations;
import org.springframework.social.oauth2.OAuth2ServiceProvider;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

public class JdbcMultiUserServiceProviderConnectionRepositoryTest {

	private EmbeddedDatabase database;
	
	private MapServiceProviderConnectionFactoryRegistry connectionFactoryRegistry;
	
	private TestFacebookServiceProviderConnectionFactory connectionFactory;
	
	private JdbcTemplate dataAccessor;

	private JdbcMultiUserServiceProviderConnectionRepository usersConnectionRepository;

	private ServiceProviderConnectionRepository connectionRepository;

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
		usersConnectionRepository = new JdbcMultiUserServiceProviderConnectionRepository(database, connectionFactoryRegistry, Encryptors.noOpText());
		connectionRepository = usersConnectionRepository.createConnectionRepository("1");
	}

	@After
	public void tearDown() {
		if (database != null) {
			database.shutdown();
		}
	}

	@Test
	public void findLocalUserIdConnectedTo() {
		insertFacebookConnection();
		String localUserId = usersConnectionRepository.findLocalUserIdConnectedTo(new ServiceProviderConnectionKey("facebook", "9"));
		assertEquals("1", localUserId);
	}
	
	@Test
	public void findLocalUserIdConnectedToNoSuchConnection() {
		assertNull(usersConnectionRepository.findLocalUserIdConnectedTo(new ServiceProviderConnectionKey("facebook", "9")));
	}
	
	@Test
	public void findLocalUserIdsConnectedTo() {
		insertFacebookConnection();
		insertFacebookConnection3();
		Set<String> localUserIds = usersConnectionRepository.findLocalUserIdsConnectedTo("facebook", Arrays.asList("9", "11"));
		assertEquals(2, localUserIds.size());
		assertTrue(localUserIds.contains("1"));
		assertTrue(localUserIds.contains("2"));		
	}
	
	@Test
	@SuppressWarnings("unchecked")
	public void findConnectionsToProviders() {
		connectionFactoryRegistry.addConnectionFactory(new TestTwitterServiceProviderConnectionFactory());
		insertTwitterConnection();
		insertFacebookConnection();
		MultiValueMap<String, ServiceProviderConnection<?>> connections = connectionRepository.findConnectionsToProviders();
		assertEquals(2, connections.size());
		ServiceProviderConnection<TestFacebookApi> facebook = (ServiceProviderConnection<TestFacebookApi>) connections.getFirst("facebook");
		assertFacebookConnection(facebook);
		ServiceProviderConnection<TestTwitterApi> twitter = (ServiceProviderConnection<TestTwitterApi>) connections.getFirst("twitter");
		assertTwitterConnection(twitter);
	}

	@Test
	public void findAllConnectionsEmptyResult() {
		connectionFactoryRegistry.addConnectionFactory(new TestTwitterServiceProviderConnectionFactory());
		MultiValueMap<String, ServiceProviderConnection<?>> connections = connectionRepository.findConnectionsToProviders();
		assertEquals(2, connections.size());
		assertEquals(0, connections.get("facebook").size());
		assertEquals(0, connections.get("twitter").size());		
	}

	@Test(expected=IllegalArgumentException.class)
	public void findAllConnectionsNoProviderRegistered() {
		insertTwitterConnection();
		connectionRepository.findConnectionsToProviders();	
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
		MultiValueMap<String, String> providerUsers = new LinkedMultiValueMap<String, String>();
		providerUsers.add("facebook", "10");
		providerUsers.add("facebook", "9");
		providerUsers.add("twitter", "1");
		MultiValueMap<String, ServiceProviderConnection<?>> connectionsForUsers = connectionRepository.findConnectionsForUsers(providerUsers);
		assertEquals(2, connectionsForUsers.size());
		assertEquals("10", connectionsForUsers.getFirst("facebook").getKey().getProviderUserId());
		assertFacebookConnection((ServiceProviderConnection<TestFacebookApi>) connectionsForUsers.get("facebook").get(1));
		assertTwitterConnection((ServiceProviderConnection<TestTwitterApi>) connectionsForUsers.getFirst("twitter"));
	}
	
	@Test
	public void findConnectionsForUsersEmptyResult() {
		MultiValueMap<String, String> providerUsers = new LinkedMultiValueMap<String, String>();
		providerUsers.add("facebook", "1");
		assertTrue(connectionRepository.findConnectionsForUsers(providerUsers).isEmpty());
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void findConnectionsForUsersEmptyInput() {
		MultiValueMap<String, String> providerUsers = new LinkedMultiValueMap<String, String>();
		connectionRepository.findConnectionsForUsers(providerUsers);
	}
	
	@Test
	@SuppressWarnings("unchecked")
	public void findConnection() {
		insertFacebookConnection();
		assertFacebookConnection((ServiceProviderConnection<TestFacebookApi>) connectionRepository.findConnection(new ServiceProviderConnectionKey("facebook", "9")));
	}
	
	@Test(expected=NoSuchServiceProviderConnectionException.class)
	public void findConnectionNoSuchConnection() {
		connectionRepository.findConnection(new ServiceProviderConnectionKey("facebook", "bogus"));
	}

	@Test
	public void findConnectionByServiceApi() {
		insertFacebookConnection();
		assertFacebookConnection(connectionRepository.findConnectionByServiceApi(TestFacebookApi.class));
	}

	@Test
	public void findConnectionByServiceApiSelectFromMultipleByRank() {
		insertFacebookConnection2();
		insertFacebookConnection();
		assertFacebookConnection(connectionRepository.findConnectionByServiceApi(TestFacebookApi.class));
	}

	@Test
	public void findConnectionByServiceApiNoSuchConnection() {
		assertNull(connectionRepository.findConnectionByServiceApi(TestFacebookApi.class));
	}

	@Test
	public void findConnectionsByServiceApi() {
		insertFacebookConnection();
		insertFacebookConnection2();
		List<ServiceProviderConnection<TestFacebookApi>> connections = connectionRepository.findConnectionsByServiceApi(TestFacebookApi.class);
		assertEquals(2, connections.size());
		assertFacebookConnection(connections.get(0));
	}
	
	@Test
	public void findConnectionByServiceApiForUser() {
		insertFacebookConnection();
		insertFacebookConnection2();	
		assertFacebookConnection(connectionRepository.findConnectionByServiceApiForUser(TestFacebookApi.class, "9"));
		assertEquals("10", connectionRepository.findConnectionByServiceApiForUser(TestFacebookApi.class, "10").getKey().getProviderUserId());
	}

	@Test(expected=NoSuchServiceProviderConnectionException.class)
	public void findConnectionByServiceApiForUserNoSuchConnection() {
		assertFacebookConnection(connectionRepository.findConnectionByServiceApiForUser(TestFacebookApi.class, "9"));
	}
	
	@Test
	public void removeConnectionsToProvider() {
		insertFacebookConnection();
		insertFacebookConnection2();
		assertTrue(dataAccessor.queryForObject("select exists (select 1 from ServiceProviderConnection where providerId = 'facebook')", Boolean.class));
		connectionRepository.removeConnectionsToProvider("facebook");
		assertFalse(dataAccessor.queryForObject("select exists (select 1 from ServiceProviderConnection where providerId = 'facebook')", Boolean.class));
	}
	
	@Test
	public void removeConnectionsToProviderNoOp() {
		connectionRepository.removeConnectionsToProvider("twitter");
	}

	@Test
	public void removeConnection() {
		insertFacebookConnection();
		assertTrue(dataAccessor.queryForObject("select exists (select 1 from ServiceProviderConnection where providerId = 'facebook')", Boolean.class));
		connectionRepository.removeConnection(new ServiceProviderConnectionKey("facebook", "9"));
		assertFalse(dataAccessor.queryForObject("select exists (select 1 from ServiceProviderConnection where providerId = 'facebook')", Boolean.class));		
	}

	@Test
	public void removeConnectionNoOp() {
		connectionRepository.removeConnection(new ServiceProviderConnectionKey("facebook", "1"));
	}

	@Test
	public void addConnection() {
		ServiceProviderConnection<TestFacebookApi> connection = connectionFactory.createConnection(new AccessGrant("123456789", null, "987654321", 3600));
		connectionRepository.addConnection(connection);
		ServiceProviderConnection<TestFacebookApi> restoredConnection = connectionRepository.findConnectionByServiceApi(TestFacebookApi.class);
		assertEquals(connection, restoredConnection);	
		assertNewConnection(restoredConnection);
	}
	
	@Test(expected=DuplicateServiceProviderConnectionException.class)
	public void addConnectionDuplicate() {
		ServiceProviderConnection<TestFacebookApi> connection = connectionFactory.createConnection(new AccessGrant("123456789", null, "987654321", 3600));
		connectionRepository.addConnection(connection);
		connectionRepository.addConnection(connection);
	}
	
	@Test
	public void updateConnectionProfileFields() {
		connectionFactoryRegistry.addConnectionFactory(new TestTwitterServiceProviderConnectionFactory());		
		insertTwitterConnection();
		ServiceProviderConnection<TestTwitterApi> twitter = connectionRepository.findConnectionByServiceApi(TestTwitterApi.class);
		assertEquals("http://twitter.com/kdonald/picture", twitter.getUser().getProfilePictureUrl());
		twitter.sync();
		assertEquals("http://twitter.com/kdonald/a_new_picture", twitter.getUser().getProfilePictureUrl());
		connectionRepository.updateConnection(twitter);
		ServiceProviderConnection<TestTwitterApi> twitter2 = connectionRepository.findConnectionByServiceApi(TestTwitterApi.class);
		assertEquals("http://twitter.com/kdonald/a_new_picture", twitter2.getUser().getProfilePictureUrl());
	}
	
	@Test
	public void updateConnectionAccessFields() {
		insertFacebookConnection();
		ServiceProviderConnection<TestFacebookApi> facebook = connectionRepository.findConnectionByServiceApi(TestFacebookApi.class);
		assertEquals("234567890", facebook.getServiceApi().getAccessToken());
		facebook.refresh();
		connectionRepository.updateConnection(facebook);
		ServiceProviderConnection<TestFacebookApi> facebook2 = connectionRepository.findConnectionByServiceApi(TestFacebookApi.class);
		assertEquals("765432109", facebook2.getServiceApi().getAccessToken());
		ServiceProviderConnectionData data = facebook.createData();
		assertEquals("654321098", data.getRefreshToken());
	}

		
	private void insertTwitterConnection() {
		dataAccessor.update("insert into ServiceProviderConnection (localUserId, providerId, providerUserId, rank, profileName, profileUrl, profilePictureUrl, accessToken, secret, refreshToken, expireTime) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
				"1", "twitter", "1", 1, "@kdonald", "http://twitter.com/kdonald", "http://twitter.com/kdonald/picture", "123456789", "987654321", null, null);
	}
	
	private void insertFacebookConnection() {
		dataAccessor.update("insert into ServiceProviderConnection (localUserId, providerId, providerUserId, rank, profileName, profileUrl, profilePictureUrl, accessToken, secret, refreshToken, expireTime) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
				"1", "facebook", "9", 1, null, null, null, "234567890", null, "345678901", System.currentTimeMillis() + 3600000);
	}
	
	private void insertFacebookConnection2() {
		dataAccessor.update("insert into ServiceProviderConnection (localUserId, providerId, providerUserId, rank, profileName, profileUrl, profilePictureUrl, accessToken, secret, refreshToken, expireTime) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
				"1", "facebook", "10", 2, null, null, null, "456789012", null, "56789012", System.currentTimeMillis() + 3600000);
	}

	private void insertFacebookConnection3() {
		dataAccessor.update("insert into ServiceProviderConnection (localUserId, providerId, providerUserId, rank, profileName, profileUrl, profilePictureUrl, accessToken, secret, refreshToken, expireTime) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
				"2", "facebook", "11", 2, null, null, null, "456789012", null, "56789012", System.currentTimeMillis() + 3600000);
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
			return new OAuth2Operations() {
				public String buildAuthorizeUrl(String redirectUri, String scope, String state, GrantType grantType, MultiValueMap<String, String> additionalParameters) {
					return null;
				}
				public String buildAuthenticateUrl(String redirectUri, String state, GrantType grantType, MultiValueMap<String, String> additionalParameters) {
					return null;
				}
				public AccessGrant exchangeForAccess(String authorizationGrant, String redirectUri, MultiValueMap<String, String> additionalParameters) {
					return null;
				}
				public AccessGrant refreshAccess(String refreshToken, String scope, MultiValueMap<String, String> additionalParameters) {
					return new AccessGrant("765432109", "read", "654321098", 3600);
				}								
			};
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
