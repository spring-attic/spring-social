package org.springframework.social.connect.jdbc;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.sql.DataSource;

import org.h2.Driver;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.embedded.ConnectionProperties;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseConfigurer;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseFactory;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.security.crypto.encrypt.Encryptors;
import org.springframework.social.connect.ConnectionKey;
import org.springframework.social.connect.DuplicateConnectionException;
import org.springframework.social.connect.NoSuchConnectionException;
import org.springframework.social.connect.ApiAdapter;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionData;
import org.springframework.social.connect.ConnectionRepository;
import org.springframework.social.connect.ConnectionValues;
import org.springframework.social.connect.UserProfile;
import org.springframework.social.connect.UserProfileBuilder;
import org.springframework.social.connect.support.ConnectionFactoryRegistry;
import org.springframework.social.connect.support.OAuth1ConnectionFactory;
import org.springframework.social.connect.support.OAuth2ConnectionFactory;
import org.springframework.social.oauth1.OAuth1Operations;
import org.springframework.social.oauth1.OAuth1ServiceProvider;
import org.springframework.social.oauth2.AccessGrant;
import org.springframework.social.oauth2.GrantType;
import org.springframework.social.oauth2.OAuth2Operations;
import org.springframework.social.oauth2.OAuth2Parameters;
import org.springframework.social.oauth2.OAuth2ServiceProvider;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

public class JdbcUsersConnectionRepositoryTest {

	private EmbeddedDatabase database;

	private boolean testMySqlCompatiblity;
	
	private ConnectionFactoryRegistry connectionFactoryRegistry;
	
	private TestFacebookConnectionFactory connectionFactory;
	
	private JdbcTemplate dataAccessor;

	private JdbcUsersConnectionRepository usersConnectionRepository;

	private ConnectionRepository connectionRepository;

	@Before
	public void setUp() {
		EmbeddedDatabaseFactory factory = new EmbeddedDatabaseFactory();
		if (testMySqlCompatiblity) {
			factory.setDatabaseConfigurer(new MySqlCompatibleH2DatabaseConfigurer());	
		} else {
			factory.setDatabaseType(EmbeddedDatabaseType.H2);			
		}
		ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
		populator.addScript(new ClassPathResource("JdbcUsersConnectionRepository.sql", getClass()));
		factory.setDatabasePopulator(populator);
		database = factory.getDatabase();
		dataAccessor = new JdbcTemplate(database);
		connectionFactoryRegistry = new ConnectionFactoryRegistry();
		connectionFactory = new TestFacebookConnectionFactory();
		connectionFactoryRegistry.addConnectionFactory(connectionFactory);
		usersConnectionRepository = new JdbcUsersConnectionRepository(database, connectionFactoryRegistry, Encryptors.noOpText());
		connectionRepository = usersConnectionRepository.createConnectionRepository("1");
	}
	
	@After
	public void tearDown() {
		if (database != null) {
			database.shutdown();
		}
	}

	@Test
	public void findUserIdWithConnection() {
		insertFacebookConnection();
		String userId = usersConnectionRepository.findUserIdWithConnection(connectionRepository.findPrimaryConnectionToApi(TestFacebookApi.class));
		assertEquals("1", userId);
	}
	
	@Test
	public void findUserIdWithConnectionNoSuchConnection() {
		Connection<TestFacebookApi> connection = connectionFactory.createConnection(new AccessGrant("12345"));
		assertNull(usersConnectionRepository.findUserIdWithConnection(connection));
	}

	@Test
	public void findUserIdWithConnectionMultipleConnectionsToSameProviderUser() {
		insertFacebookConnection();
		insertFacebookConnectionSameFacebookUser();
		assertNull(usersConnectionRepository.findUserIdWithConnection(connectionRepository.findPrimaryConnectionToApi(TestFacebookApi.class)));
	}
	
	@Test
	public void findUserIdsConnectedTo() {
		insertFacebookConnection();
		insertFacebookConnection3();
		Set<String> localUserIds = usersConnectionRepository.findUserIdsConnectedTo("facebook", new HashSet<String>(Arrays.asList("9", "11")));
		assertEquals(2, localUserIds.size());
		assertTrue(localUserIds.contains("1"));
		assertTrue(localUserIds.contains("2"));		
	}
	
	@Test
	@SuppressWarnings("unchecked")
	public void findConnectionsToProviders() {
		connectionFactoryRegistry.addConnectionFactory(new TestTwitterConnectionFactory());
		insertTwitterConnection();
		insertFacebookConnection();
		MultiValueMap<String, Connection<?>> connections = connectionRepository.findConnections();
		assertEquals(2, connections.size());
		Connection<TestFacebookApi> facebook = (Connection<TestFacebookApi>) connections.getFirst("facebook");
		assertFacebookConnection(facebook);
		Connection<TestTwitterApi> twitter = (Connection<TestTwitterApi>) connections.getFirst("twitter");
		assertTwitterConnection(twitter);
	}

	@Test
	public void findAllConnectionsEmptyResult() {
		connectionFactoryRegistry.addConnectionFactory(new TestTwitterConnectionFactory());
		MultiValueMap<String, Connection<?>> connections = connectionRepository.findConnections();
		assertEquals(2, connections.size());
		assertEquals(0, connections.get("facebook").size());
		assertEquals(0, connections.get("twitter").size());		
	}

	@Test(expected=IllegalArgumentException.class)
	public void findAllConnectionsNoProviderRegistered() {
		insertTwitterConnection();
		connectionRepository.findConnections();	
	}

	@Test
	@SuppressWarnings("unchecked")
	public void findConnectionsToProvider() {
		connectionFactoryRegistry.addConnectionFactory(new TestTwitterConnectionFactory());
		insertTwitterConnection();
		List<Connection<?>> connections = connectionRepository.findConnectionsToProvider("twitter");
		assertEquals(1, connections.size());
		assertTwitterConnection((Connection<TestTwitterApi>) connections.get(0));
	}
	
	@Test
	public void findConnectionsToProviderEmptyResult() {
		assertTrue(connectionRepository.findConnectionsToProvider("facebook").isEmpty());
	}

	@Test
	@SuppressWarnings("unchecked")
	public void findConnectionsForUsersEmpty() {
		connectionFactoryRegistry.addConnectionFactory(new TestTwitterConnectionFactory());
		insertTwitterConnection();
		insertFacebookConnection();
		insertFacebookConnection2();
		MultiValueMap<String, String> providerUsers = new LinkedMultiValueMap<String, String>();
		providerUsers.add("facebook", "10");
		providerUsers.add("facebook", "9");
		providerUsers.add("twitter", "1");
		MultiValueMap<String, Connection<?>> connectionsForUsers = connectionRepository.findConnectionsForUsers(providerUsers);
		assertEquals(2, connectionsForUsers.size());
		assertEquals("10", connectionsForUsers.getFirst("facebook").getKey().getProviderUserId());
		assertFacebookConnection((Connection<TestFacebookApi>) connectionsForUsers.get("facebook").get(1));
		assertTwitterConnection((Connection<TestTwitterApi>) connectionsForUsers.getFirst("twitter"));
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
		assertFacebookConnection((Connection<TestFacebookApi>) connectionRepository.findConnection(new ConnectionKey("facebook", "9")));
	}
	
	@Test(expected=NoSuchConnectionException.class)
	public void findConnectionNoSuchConnection() {
		connectionRepository.findConnection(new ConnectionKey("facebook", "bogus"));
	}

	@Test
	public void findPrimaryConnectionToApi() {
		insertFacebookConnection();
		assertFacebookConnection(connectionRepository.findPrimaryConnectionToApi(TestFacebookApi.class));
	}

	@Test
	public void findPrimaryConnectionToApiSelectFromMultipleByRank() {
		insertFacebookConnection2();
		insertFacebookConnection();
		assertFacebookConnection(connectionRepository.findPrimaryConnectionToApi(TestFacebookApi.class));
	}

	@Test
	public void findPrimaryConnectionToApiNoSuchConnection() {
		assertNull(connectionRepository.findPrimaryConnectionToApi(TestFacebookApi.class));
	}

	@Test
	public void findConnectionsToApi() {
		insertFacebookConnection();
		insertFacebookConnection2();
		List<Connection<TestFacebookApi>> connections = connectionRepository.findConnectionsToServiceApi(TestFacebookApi.class);
		assertEquals(2, connections.size());
		assertFacebookConnection(connections.get(0));
	}
	
	@Test
	public void findConnectionToApiForUser() {
		insertFacebookConnection();
		insertFacebookConnection2();	
		assertFacebookConnection(connectionRepository.findConnectionToApiForUser(TestFacebookApi.class, "9"));
		assertEquals("10", connectionRepository.findConnectionToApiForUser(TestFacebookApi.class, "10").getKey().getProviderUserId());
	}

	@Test(expected=NoSuchConnectionException.class)
	public void findConnectionToApiForUserNoSuchConnection() {
		assertFacebookConnection(connectionRepository.findConnectionToApiForUser(TestFacebookApi.class, "9"));
	}
	
	@Test
	public void removeConnectionsToProvider() {
		insertFacebookConnection();
		insertFacebookConnection2();
		assertTrue(dataAccessor.queryForObject("select exists (select 1 from UserConnection where providerId = 'facebook')", Boolean.class));
		connectionRepository.removeConnectionsToProvider("facebook");
		assertFalse(dataAccessor.queryForObject("select exists (select 1 from UserConnection where providerId = 'facebook')", Boolean.class));
	}
	
	@Test
	public void removeConnectionsToProviderNoOp() {
		connectionRepository.removeConnectionsToProvider("twitter");
	}

	@Test
	public void removeConnection() {
		insertFacebookConnection();
		assertTrue(dataAccessor.queryForObject("select exists (select 1 from UserConnection where providerId = 'facebook')", Boolean.class));
		connectionRepository.removeConnection(new ConnectionKey("facebook", "9"));
		assertFalse(dataAccessor.queryForObject("select exists (select 1 from UserConnection where providerId = 'facebook')", Boolean.class));		
	}

	@Test
	public void removeConnectionNoOp() {
		connectionRepository.removeConnection(new ConnectionKey("facebook", "1"));
	}

	@Test
	public void addConnection() {
		Connection<TestFacebookApi> connection = connectionFactory.createConnection(new AccessGrant("123456789", null, "987654321", 3600));
		connectionRepository.addConnection(connection);
		Connection<TestFacebookApi> restoredConnection = connectionRepository.findPrimaryConnectionToApi(TestFacebookApi.class);
		assertEquals(connection, restoredConnection);	
		assertNewConnection(restoredConnection);
	}
	
	@Test(expected=DuplicateConnectionException.class)
	public void addConnectionDuplicate() {
		Connection<TestFacebookApi> connection = connectionFactory.createConnection(new AccessGrant("123456789", null, "987654321", 3600));
		connectionRepository.addConnection(connection);
		connectionRepository.addConnection(connection);
	}
	
	@Test
	public void updateConnectionProfileFields() {
		connectionFactoryRegistry.addConnectionFactory(new TestTwitterConnectionFactory());		
		insertTwitterConnection();
		Connection<TestTwitterApi> twitter = connectionRepository.findPrimaryConnectionToApi(TestTwitterApi.class);
		assertEquals("http://twitter.com/kdonald/picture", twitter.getImageUrl());
		twitter.sync();
		assertEquals("http://twitter.com/kdonald/a_new_picture", twitter.getImageUrl());
		connectionRepository.updateConnection(twitter);
		Connection<TestTwitterApi> twitter2 = connectionRepository.findPrimaryConnectionToApi(TestTwitterApi.class);
		assertEquals("http://twitter.com/kdonald/a_new_picture", twitter2.getImageUrl());
	}
	
	@Test
	public void updateConnectionAccessFields() {
		insertFacebookConnection();
		Connection<TestFacebookApi> facebook = connectionRepository.findPrimaryConnectionToApi(TestFacebookApi.class);
		assertEquals("234567890", facebook.getApi().getAccessToken());
		facebook.refresh();
		connectionRepository.updateConnection(facebook);
		Connection<TestFacebookApi> facebook2 = connectionRepository.findPrimaryConnectionToApi(TestFacebookApi.class);
		assertEquals("765432109", facebook2.getApi().getAccessToken());
		ConnectionData data = facebook.createData();
		assertEquals("654321098", data.getRefreshToken());
	}

		
	private void insertTwitterConnection() {
		dataAccessor.update("insert into UserConnection (userId, providerId, providerUserId, rank, displayName, profileUrl, imageUrl, accessToken, secret, refreshToken, expireTime) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
				"1", "twitter", "1", 1, "@kdonald", "http://twitter.com/kdonald", "http://twitter.com/kdonald/picture", "123456789", "987654321", null, null);
	}
	
	private void insertFacebookConnection() {
		dataAccessor.update("insert into UserConnection (userId, providerId, providerUserId, rank, displayName, profileUrl, imageUrl, accessToken, secret, refreshToken, expireTime) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
				"1", "facebook", "9", 1, null, null, null, "234567890", null, "345678901", System.currentTimeMillis() + 3600000);
	}
	
	private void insertFacebookConnection2() {
		dataAccessor.update("insert into UserConnection (userId, providerId, providerUserId, rank, displayName, profileUrl, imageUrl, accessToken, secret, refreshToken, expireTime) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
				"1", "facebook", "10", 2, null, null, null, "456789012", null, "56789012", System.currentTimeMillis() + 3600000);
	}

	private void insertFacebookConnection3() {
		dataAccessor.update("insert into UserConnection (userId, providerId, providerUserId, rank, displayName, profileUrl, imageUrl, accessToken, secret, refreshToken, expireTime) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
				"2", "facebook", "11", 2, null, null, null, "456789012", null, "56789012", System.currentTimeMillis() + 3600000);
	}

	private void insertFacebookConnectionSameFacebookUser() {
		dataAccessor.update("insert into UserConnection (userId, providerId, providerUserId, rank, displayName, profileUrl, imageUrl, accessToken, secret, refreshToken, expireTime) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
				"2", "facebook", "9", 1, null, null, null, "234567890", null, "345678901", System.currentTimeMillis() + 3600000);
	}

	private void assertNewConnection(Connection<TestFacebookApi> connection) {
		assertEquals("facebook", connection.getKey().getProviderId());
		assertEquals("9", connection.getKey().getProviderUserId());
		assertEquals("Keith Donald", connection.getDisplayName());
		assertEquals("http://facebook.com/keith.donald", connection.getProfileUrl());
		assertEquals("http://facebook.com/keith.donald/picture", connection.getImageUrl());
		assertTrue(connection.test());
		TestFacebookApi api = connection.getApi();
		assertNotNull(api);
		assertEquals("123456789", api.getAccessToken());
		assertEquals("123456789", connection.createData().getAccessToken());
		assertEquals("987654321", connection.createData().getRefreshToken());
	}

	private void assertTwitterConnection(Connection<TestTwitterApi> twitter) {
		assertEquals(new ConnectionKey("twitter", "1"), twitter.getKey());
		assertEquals("@kdonald", twitter.getDisplayName());
		assertEquals("http://twitter.com/kdonald", twitter.getProfileUrl());
		assertEquals("http://twitter.com/kdonald/picture", twitter.getImageUrl());
		TestTwitterApi twitterApi = twitter.getApi();
		assertEquals("123456789", twitterApi.getAccessToken());		
		assertEquals("987654321", twitterApi.getSecret());
		twitter.sync();
		assertEquals("http://twitter.com/kdonald/a_new_picture", twitter.getImageUrl());
	}

	private void assertFacebookConnection(Connection<TestFacebookApi> facebook) {
		assertEquals(new ConnectionKey("facebook", "9"), facebook.getKey());
		assertEquals(null, facebook.getDisplayName());
		assertEquals(null, facebook.getProfileUrl());
		assertEquals(null, facebook.getImageUrl());
		TestFacebookApi facebookApi = facebook.getApi();
		assertEquals("234567890", facebookApi.getAccessToken());
		facebook.sync();
		assertEquals("Keith Donald", facebook.getDisplayName());
		assertEquals("http://facebook.com/keith.donald", facebook.getProfileUrl());
		assertEquals("http://facebook.com/keith.donald/picture", facebook.getImageUrl());		
	}
	
	// test facebook provider
	
	private static class TestFacebookConnectionFactory extends OAuth2ConnectionFactory<TestFacebookApi> {

		public TestFacebookConnectionFactory() {
			super("facebook", new TestFacebookServiceProvider(), new TestFacebookApiAdapter());
		}
		
	}

	private static class TestFacebookServiceProvider implements OAuth2ServiceProvider<TestFacebookApi> {

		public OAuth2Operations getOAuthOperations() {
			return new OAuth2Operations() {
				public String buildAuthorizeUrl(GrantType grantType, OAuth2Parameters params) {
					return null;
				}
				public String buildAuthenticateUrl(GrantType grantType, OAuth2Parameters params) {
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

		public TestFacebookApi getApi(final String accessToken) {
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
	
	private static class TestFacebookApiAdapter implements ApiAdapter<TestFacebookApi> {

		private String accountId = "9";
		
		private String name = "Keith Donald";
		
		private String profileUrl = "http://facebook.com/keith.donald";
		
		private String profilePictureUrl = "http://facebook.com/keith.donald/picture";
		
		public boolean test(TestFacebookApi serviceApi) {
			return true;
		}

		public void setConnectionValues(TestFacebookApi serviceApi, ConnectionValues values) {
			values.setProviderUserId(accountId);
			values.setDisplayName(name);
			values.setProfileUrl(profileUrl);
			values.setImageUrl(profilePictureUrl);
		}

		public UserProfile fetchUserProfile(TestFacebookApi serviceApi) {
			return new UserProfileBuilder().setName(name).setEmail("keith@interface21.com").setUsername("Keith.Donald").build();
		}

		public void updateStatus(TestFacebookApi serviceApi, String message) {
			
		}
		
	}
	
	// test twitter provider
	
	private static class TestTwitterConnectionFactory extends OAuth1ConnectionFactory<TestTwitterApi> {

		public TestTwitterConnectionFactory() {
			super("twitter", new TestTwitterServiceProvider(), new TestTwitterApiAdapter());
		}
		
	}

	private static class TestTwitterServiceProvider implements OAuth1ServiceProvider<TestTwitterApi> {

		public OAuth1Operations getOAuthOperations() {
			return null;
		}

		public TestTwitterApi getApi(final String accessToken, final String secret) {
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
	
	private static class TestTwitterApiAdapter implements ApiAdapter<TestTwitterApi> {

		private String accountId = "1";
		
		private String name = "@kdonald";
		
		private String profileUrl = "http://twitter.com/kdonald";
		
		private String profilePictureUrl = "http://twitter.com/kdonald/a_new_picture";
		
		public boolean test(TestTwitterApi serviceApi) {
			return true;
		}

		public void setConnectionValues(TestTwitterApi serviceApi, ConnectionValues values) {
			values.setProviderUserId(accountId);
			values.setDisplayName(name);
			values.setProfileUrl(profileUrl);
			values.setImageUrl(profilePictureUrl);
		}

		public UserProfile fetchUserProfile(TestTwitterApi serviceApi) {
			return new UserProfileBuilder().setName(name).setUsername("kdonald").build();			
		}
		
		public void updateStatus(TestTwitterApi serviceApi, String message) {
		}
		
	}
	
	private static class MySqlCompatibleH2DatabaseConfigurer implements EmbeddedDatabaseConfigurer {
		public void shutdown(DataSource dataSource, String databaseName) {
			try {
				java.sql.Connection connection = dataSource.getConnection();
				Statement stmt = connection.createStatement();
				stmt.execute("SHUTDOWN");
			}
			catch (SQLException ex) {
			}
		}
		
		public void configureConnectionProperties(ConnectionProperties properties, String databaseName) {
			properties.setDriverClass(Driver.class);
			properties.setUrl(String.format("jdbc:h2:mem:%s;MODE=MYSQL;DB_CLOSE_DELAY=-1", databaseName));
			properties.setUsername("sa");
			properties.setPassword("");
		}
	}		
	
}
