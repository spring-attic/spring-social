package org.springframework.social.connect.jdbc;

import static org.hamcrest.Matchers.hasItems;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.springframework.social.connect.ApiAdapter;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionData;
import org.springframework.social.connect.ConnectionKey;
import org.springframework.social.connect.ConnectionRepository;
import org.springframework.social.connect.ConnectionSignUp;
import org.springframework.social.connect.ConnectionValues;
import org.springframework.social.connect.DuplicateConnectionException;
import org.springframework.social.connect.NoSuchConnectionException;
import org.springframework.social.connect.NotConnectedException;
import org.springframework.social.connect.UserProfile;
import org.springframework.social.connect.UserProfileBuilder;
import org.springframework.social.connect.UsersConnectionRepository;
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

public abstract class AbstractUsersConnectionRepositoryTest {

	protected static final String TWITTER_CONNECTION_1_PROVIDER_USER_ID = "8";
	protected static final String FACEBOOK_CONNECTION_1_PROVIDER_USER_ID = "9";
	protected static final String FACEBOOK_CONNECTION_2_PROVIDER_USER_ID = "10";
	protected static final String FACEBOOK_CONNECTION_3_PROVIDER_USER_ID = "11";
	
	protected static final ConnectionData TWITTER_DATA =
			new ConnectionData("twitter", TWITTER_CONNECTION_1_PROVIDER_USER_ID, "@kdonald", "http://twitter.com/kdonald", "http://twitter.com/kdonald/picture", "123456789", "987654321", "refresh_token", System.currentTimeMillis() + 3600000);
	protected static final ConnectionData FACEBOOK_DATA_1 =
			new ConnectionData("facebook", FACEBOOK_CONNECTION_1_PROVIDER_USER_ID, null, null, null, "234567890", null, "345678901", System.currentTimeMillis() + 3600000);
	protected static final ConnectionData FACEBOOK_DATA_2 =
			new ConnectionData("facebook", FACEBOOK_CONNECTION_2_PROVIDER_USER_ID, null, null, null, "456789012", null, "56789012", System.currentTimeMillis() + 3600000);
	protected static final ConnectionData FACEBOOK_DATA_3 =
			new ConnectionData("facebook", FACEBOOK_CONNECTION_3_PROVIDER_USER_ID, null, null, null, "234567890", null, "345678901", System.currentTimeMillis() + 3600000);

	
	protected static class TestFacebookConnectionFactory extends OAuth2ConnectionFactory<TestFacebookApi> {
	
			public TestFacebookConnectionFactory() {
				super("facebook", new TestFacebookServiceProvider(), new TestFacebookApiAdapter());
			}
			
		}

	private TestFacebookConnectionFactory facebookConnectionFactory;
	private TestTwitterConnectionFactory twitterConnectionFactory;
	private ConnectionFactoryRegistry connectionFactoryRegistry = new ConnectionFactoryRegistry();
	
	protected abstract void insertTwitterConnection();
	protected abstract void insertFacebookConnection1();
	protected abstract void insertFacebookConnection2();
	protected abstract void insertFacebookConnection3();
	protected abstract void insertFacebookConnectionSameFacebookUser();
	protected abstract String getUserId1();
	protected abstract String getUserId2();
	
	protected abstract UsersConnectionRepository getUsersConnectionRepository();
	protected abstract ConnectionRepository getConnectionRepository();
	
	protected TestFacebookConnectionFactory getFacebookConnectionFactory() {
		return facebookConnectionFactory;
	}
	
	public TestTwitterConnectionFactory getTwitterConnectionFactory() {
		return twitterConnectionFactory;
	}
	
	public ConnectionFactoryRegistry getConnectionFactoryRegistry() {
		return this.connectionFactoryRegistry;
	}
	
	@Before
	public void setUpConnectionFactory() {
		facebookConnectionFactory = new TestFacebookConnectionFactory();
		twitterConnectionFactory  = new TestTwitterConnectionFactory();
		connectionFactoryRegistry = new ConnectionFactoryRegistry();
		connectionFactoryRegistry.addConnectionFactory(facebookConnectionFactory);
		connectionFactoryRegistry.addConnectionFactory(twitterConnectionFactory);
	}
	
	@Test
	public void findUserIdWithConnection() {
		insertFacebookConnection1();
		List<String> userIds = getUsersConnectionRepository().findUserIdsWithConnection(getConnectionRepository().getPrimaryConnection(TestFacebookApi.class));
		assertEquals(getUserId1(), userIds.get(0));
	}

	@Test
	public void findUserIdWithConnectionNoSuchConnection() {
		Connection<TestFacebookApi> connection = getFacebookConnectionFactory().createConnection(new AccessGrant("12345"));
		assertEquals(0, getUsersConnectionRepository().findUserIdsWithConnection(connection).size());
	}

	@Test
	public void findUserIdWithConnectionMultipleConnectionsToSameProviderUser() {
		insertFacebookConnection1();
		insertFacebookConnectionSameFacebookUser();
		List<String> localUserIds = getUsersConnectionRepository().findUserIdsWithConnection(getConnectionRepository().getPrimaryConnection(TestFacebookApi.class));
		assertEquals(2, localUserIds.size());
		assertThat(localUserIds, hasItems(getUserId1(), getUserId2()));
	}

	@Test
	public void findUserIdWithConnectionNoConnection_withWorkingConnectionSignUp() {		
		Connection<TestFacebookApi> connection = facebookConnectionFactory.createConnection(new AccessGrant("12345"));
		getUsersConnectionRepository().setConnectionSignUp(new ConnectionSignUp() {
			@Override
			public String execute(Connection<?> connection) {
				return "batman";
			}
		});
		List<String> userIds = getUsersConnectionRepository().findUserIdsWithConnection(connection);
		assertEquals(1, userIds.size());
		assertEquals("batman", userIds.get(0));
	}

	@Test
	public void findUserIdWithConnectionNoConnection_withConnectionSignUpReturningNull() {		
		Connection<TestFacebookApi> connection = facebookConnectionFactory.createConnection(new AccessGrant("12345"));
		getUsersConnectionRepository().setConnectionSignUp(new ConnectionSignUp() {
			@Override
			public String execute(Connection<?> connection) {
				return null;
			}
		});
		List<String> userIds = getUsersConnectionRepository().findUserIdsWithConnection(connection);
		assertEquals(0, userIds.size());
	}

	@Test
	public void findUserIdsConnectedTo() {
		insertFacebookConnection1();
		insertFacebookConnection3();
		Set<String> localUserIds = getUsersConnectionRepository().findUserIdsConnectedTo("facebook",
				new HashSet<String>(Arrays.asList(FACEBOOK_CONNECTION_1_PROVIDER_USER_ID, FACEBOOK_CONNECTION_3_PROVIDER_USER_ID)));
		assertEquals(2, localUserIds.size());
		assertThat(localUserIds, hasItems(getUserId1(), getUserId2()));
	}

	@Test
	@SuppressWarnings("unchecked")
	public void findAllConnections() {
		insertTwitterConnection();
		insertFacebookConnection1();
		MultiValueMap<String, Connection<?>> connections = getConnectionRepository().findAllConnections();
		assertEquals(2, connections.size());
		Connection<TestFacebookApi> facebook = (Connection<TestFacebookApi>) connections.getFirst("facebook");
		assertFacebookConnection(facebook);
		Connection<TestTwitterApi> twitter = (Connection<TestTwitterApi>) connections.getFirst("twitter");
		assertTwitterConnection(twitter);
	}

	@Test
	public void findAllConnectionsMultipleConnectionResults() {
		insertTwitterConnection();
		insertFacebookConnection1();
		insertFacebookConnection2();
		MultiValueMap<String, Connection<?>> connections = getConnectionRepository().findAllConnections();
		assertEquals(2, connections.size());
		assertEquals(2, connections.get("facebook").size());
		assertEquals(1, connections.get("twitter").size());
	}

	@Test
	public void findAllConnectionsEmptyResult() {
		MultiValueMap<String, Connection<?>> connections = getConnectionRepository().findAllConnections();
		assertEquals(2, connections.size());
		assertEquals(0, connections.get("facebook").size());
		assertEquals(0, connections.get("twitter").size());		
	}

	@Test
	@SuppressWarnings("unchecked")
	public void findConnectionsByProviderId() {
		insertTwitterConnection();
		List<Connection<?>> connections = getConnectionRepository().findConnections("twitter");
		assertEquals(1, connections.size());
		assertTwitterConnection((Connection<TestTwitterApi>) connections.get(0));
	}

	@Test
	public void findConnectionsByProviderIdEmptyResult() {
		assertTrue(getConnectionRepository().findConnections("facebook").isEmpty());
	}

	@Test
	public void findConnectionsByApi() {
		insertFacebookConnection1();
		insertFacebookConnection2();
		List<Connection<TestFacebookApi>> connections = getConnectionRepository().findConnections(TestFacebookApi.class);
		assertEquals(2, connections.size());
		assertFacebookConnection(connections.get(0));
	}

	@Test
	public void findConnectionsByApiEmptyResult() {
		assertTrue(getConnectionRepository().findConnections(TestFacebookApi.class).isEmpty());
	}

	@Test
	@SuppressWarnings("unchecked")
	public void findConnectionsToUsers() {
		insertTwitterConnection();
		insertFacebookConnection2();
		insertFacebookConnection1();
		MultiValueMap<String, String> providerUsers = new LinkedMultiValueMap<String, String>();
		providerUsers.add("twitter", TWITTER_CONNECTION_1_PROVIDER_USER_ID);
		providerUsers.add("facebook", FACEBOOK_CONNECTION_2_PROVIDER_USER_ID);
		providerUsers.add("facebook", FACEBOOK_CONNECTION_1_PROVIDER_USER_ID);
		MultiValueMap<String, Connection<?>> connectionsForUsers = getConnectionRepository().findConnectionsToUsers(providerUsers);
		assertEquals(2, connectionsForUsers.size());
		assertEquals(FACEBOOK_CONNECTION_2_PROVIDER_USER_ID, connectionsForUsers.getFirst("facebook").getKey().getProviderUserId());
		assertFacebookConnection((Connection<TestFacebookApi>) connectionsForUsers.get("facebook").get(1));
		assertTwitterConnection((Connection<TestTwitterApi>) connectionsForUsers.getFirst("twitter"));
	}

	@Test
	public void findConnectionsToUsersEmptyResult() {
		MultiValueMap<String, String> providerUsers = new LinkedMultiValueMap<String, String>();
		providerUsers.add("facebook", FACEBOOK_CONNECTION_1_PROVIDER_USER_ID);
		assertTrue(getConnectionRepository().findConnectionsToUsers(providerUsers).isEmpty());
	}

	@Test(expected = IllegalArgumentException.class)
	public void findConnectionsToUsersEmptyInput() {
		MultiValueMap<String, String> providerUsers = new LinkedMultiValueMap<String, String>();
		getConnectionRepository().findConnectionsToUsers(providerUsers);
	}

	@Test
	@SuppressWarnings("unchecked")
	public void findConnectionByKey() {
		insertFacebookConnection1();
		assertFacebookConnection((Connection<TestFacebookApi>) getConnectionRepository().getConnection(new ConnectionKey("facebook", "9")));
	}

	@Test(expected = NoSuchConnectionException.class)
	public void findConnectionByKeyNoSuchConnection() {
		getConnectionRepository().getConnection(new ConnectionKey("facebook", "bogus"));
	}

	@Test
	public void findConnectionByApiToUser() {
		insertFacebookConnection1();
		insertFacebookConnection2();	
		assertFacebookConnection(getConnectionRepository().getConnection(TestFacebookApi.class, FACEBOOK_CONNECTION_1_PROVIDER_USER_ID));
		assertEquals(FACEBOOK_CONNECTION_2_PROVIDER_USER_ID, getConnectionRepository().getConnection(TestFacebookApi.class, FACEBOOK_CONNECTION_2_PROVIDER_USER_ID).getKey().getProviderUserId());
	}

	@Test(expected = NoSuchConnectionException.class)
	public void findConnectionByApiToUserNoSuchConnection() {
		assertFacebookConnection(getConnectionRepository().getConnection(TestFacebookApi.class, FACEBOOK_CONNECTION_1_PROVIDER_USER_ID));
	}

	@Test
	public void findPrimaryConnection() {
		insertFacebookConnection1();
		assertFacebookConnection(getConnectionRepository().getPrimaryConnection(TestFacebookApi.class));
	}

	@Test
	public void findPrimaryConnectionSelectFromMultipleByRank() {
		insertFacebookConnection1();
		insertFacebookConnection2();
		assertFacebookConnection(getConnectionRepository().getPrimaryConnection(TestFacebookApi.class));
	}

	@Test(expected = NotConnectedException.class)
	public void findPrimaryConnectionNotConnected() {
		getConnectionRepository().getPrimaryConnection(TestFacebookApi.class);
	}

	@Test
	public void removeConnections() {
		insertFacebookConnection1();
		insertFacebookConnection2();
		assertEquals(2, getConnectionRepository().findConnections("facebook").size());
		getConnectionRepository().removeConnections("facebook");
		assertEquals(0, getConnectionRepository().findConnections("facebook").size());
	}

	@Test
	public void removeConnectionsToProviderNoOp() {
		getConnectionRepository().removeConnections("twitter");
	}

	@Test
	public void removeConnection() {
		insertFacebookConnection1();
		insertFacebookConnection2();
		assertEquals(2, getConnectionRepository().findConnections("facebook").size());
		getConnectionRepository().removeConnection(new ConnectionKey("facebook", FACEBOOK_CONNECTION_1_PROVIDER_USER_ID));
		assertEquals(1, getConnectionRepository().findConnections("facebook").size());		
	}

	@Test
	public void removeConnectionNoOp() {
		getConnectionRepository().removeConnection(new ConnectionKey("facebook", "1111111111"));
	}

	@Test
	public void addConnection() {
		Connection<TestFacebookApi> connection = facebookConnectionFactory.createConnection(new AccessGrant("123456789", null, "987654321", 3600L));
		getConnectionRepository().addConnection(connection);
		Connection<TestFacebookApi> restoredConnection = getConnectionRepository().getPrimaryConnection(TestFacebookApi.class);
		assertEquals(connection, restoredConnection);	
		assertNewConnection(restoredConnection);
	}

	@Test(expected = DuplicateConnectionException.class)
	public void addConnectionDuplicate() {
		Connection<TestFacebookApi> connection = facebookConnectionFactory.createConnection(new AccessGrant("123456789", null, "987654321", 3600L));
		getConnectionRepository().addConnection(connection);
		getConnectionRepository().addConnection(connection);
	}

	@Test
	public void updateConnectionProfileFields() {
		insertTwitterConnection();
		Connection<TestTwitterApi> twitter = getConnectionRepository().getPrimaryConnection(TestTwitterApi.class);
		assertEquals("http://twitter.com/kdonald/picture", twitter.getImageUrl());
		twitter.sync();
		assertEquals("http://twitter.com/kdonald/a_new_picture", twitter.getImageUrl());
		getConnectionRepository().updateConnection(twitter);
		Connection<TestTwitterApi> twitter2 = getConnectionRepository().getPrimaryConnection(TestTwitterApi.class);
		assertEquals("http://twitter.com/kdonald/a_new_picture", twitter2.getImageUrl());
	}

	@Test
	public void updateConnectionAccessFields() {
		insertFacebookConnection1();
		Connection<TestFacebookApi> facebook = getConnectionRepository().getPrimaryConnection(TestFacebookApi.class);
		assertEquals("234567890", facebook.getApi().getAccessToken());
		facebook.refresh();
		getConnectionRepository().updateConnection(facebook);
		Connection<TestFacebookApi> facebook2 = getConnectionRepository().getPrimaryConnection(TestFacebookApi.class);
		assertEquals("765432109", facebook2.getApi().getAccessToken());
		ConnectionData data = facebook.createData();
		assertEquals("654321098", data.getRefreshToken());
	}

	@Test
	public void findPrimaryConnection_afterRemove() {
		insertFacebookConnection1();
		insertFacebookConnection2();    
		// 9 is the providerUserId of the first Facebook connection
		getConnectionRepository().removeConnection(new ConnectionKey("facebook", FACEBOOK_CONNECTION_1_PROVIDER_USER_ID));
		assertEquals(1, getConnectionRepository().findConnections(TestFacebookApi.class).size());
		assertNotNull(getConnectionRepository().findPrimaryConnection(TestFacebookApi.class));
	}

	protected String getTablePrefix() {
		return "";
	}

	protected String getSchemaSql() {
		return "JdbcUsersConnectionRepository.sql";
	}

	private void assertNewConnection(Connection<TestFacebookApi> connection) {
		assertEquals("facebook", connection.getKey().getProviderId());
		assertEquals(FACEBOOK_CONNECTION_1_PROVIDER_USER_ID, connection.getKey().getProviderUserId());
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
		assertEquals(new ConnectionKey("twitter", TWITTER_CONNECTION_1_PROVIDER_USER_ID), twitter.getKey());
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
		assertEquals(new ConnectionKey("facebook", FACEBOOK_CONNECTION_1_PROVIDER_USER_ID), facebook.getKey());
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

	protected static class TestFacebookServiceProvider implements OAuth2ServiceProvider<TestFacebookApi> {
	
			@Override
			public OAuth2Operations getOAuthOperations() {
				return new OAuth2Operations() {
					@Override
					public String buildAuthorizeUrl(GrantType grantType, OAuth2Parameters params) {
						return null;
					}
					@Override
					public String buildAuthenticateUrl(GrantType grantType, OAuth2Parameters params) {
						return null;
					}
					@Override
					public String buildAuthorizeUrl(OAuth2Parameters params) {
						return null;
					}
					@Override
					public String buildAuthenticateUrl(OAuth2Parameters params) {
						return null;
					}
					@Override
					public AccessGrant exchangeForAccess(String authorizationGrant, String redirectUri, MultiValueMap<String, String> additionalParameters) {
						return null;
					}				
					@Override
					public AccessGrant exchangeCredentialsForAccess(String username, String password, MultiValueMap<String, String> additionalParameters) {
						return null;
					}
					@Override
					public AccessGrant refreshAccess(String refreshToken, MultiValueMap<String, String> additionalParameters) {
						return new AccessGrant("765432109", "read", "654321098", 3600L);
					}
					@Override
					public AccessGrant refreshAccess(String refreshToken, String scope, MultiValueMap<String, String> additionalParameters) {
						return new AccessGrant("765432109", "read", "654321098", 3600L);
					}
					@Override
					public AccessGrant authenticateClient() {
						return null;
					}
					@Override
					public AccessGrant authenticateClient(String scope) {
						return null;
					}
	            };
			}
	
			@Override
			public TestFacebookApi getApi(final String accessToken) {
				return new TestFacebookApi() {
					@Override
					public String getAccessToken() {
						return accessToken;
					}
				};
			}
			
		}

	public interface TestFacebookApi {
		
		String getAccessToken();
		
	}

	protected static class TestFacebookApiAdapter implements ApiAdapter<TestFacebookApi> {
	
			private final String accountId = FACEBOOK_CONNECTION_1_PROVIDER_USER_ID;
			
			private final String name = "Keith Donald";
			
			private final String profileUrl = "http://facebook.com/keith.donald";
			
			private final String profilePictureUrl = "http://facebook.com/keith.donald/picture";
			
			@Override
			public boolean test(TestFacebookApi api) {
				return true;
			}
	
			@Override
			public void setConnectionValues(TestFacebookApi api, ConnectionValues values) {
				values.setProviderUserId(accountId);
				values.setDisplayName(name);
				values.setProfileUrl(profileUrl);
				values.setImageUrl(profilePictureUrl);
			}
	
			@Override
			public UserProfile fetchUserProfile(TestFacebookApi api) {
				return new UserProfileBuilder().setName(name).setEmail("keith@interface21.com").setUsername("Keith.Donald").build();
			}
	
			@Override
			public void updateStatus(TestFacebookApi api, String message) {
				
			}
			
		}

	protected static class TestTwitterConnectionFactory extends OAuth1ConnectionFactory<TestTwitterApi> {
	
			public TestTwitterConnectionFactory() {
				super("twitter", new TestTwitterServiceProvider(), new TestTwitterApiAdapter());
			}
			
		}

	protected static class TestTwitterServiceProvider implements OAuth1ServiceProvider<TestTwitterApi> {
	
			@Override
			public OAuth1Operations getOAuthOperations() {
				return null;
			}
	
			@Override
			public TestTwitterApi getApi(final String accessToken, final String secret) {
				return new TestTwitterApi() {
					@Override
					public String getAccessToken() {
						return accessToken;
					}
					@Override
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

	protected static class TestTwitterApiAdapter implements ApiAdapter<TestTwitterApi> {
	
			private final String accountId = TWITTER_CONNECTION_1_PROVIDER_USER_ID;
			
			private final String name = "@kdonald";
			
			private final String profileUrl = "http://twitter.com/kdonald";
			
			private final String profilePictureUrl = "http://twitter.com/kdonald/a_new_picture";
			
			@Override
			public boolean test(TestTwitterApi api) {
				return true;
			}
	
			@Override
			public void setConnectionValues(TestTwitterApi api, ConnectionValues values) {
				values.setProviderUserId(accountId);
				values.setDisplayName(name);
				values.setProfileUrl(profileUrl);
				values.setImageUrl(profilePictureUrl);
			}
	
			@Override
			public UserProfile fetchUserProfile(TestTwitterApi api) {
				return new UserProfileBuilder().setName(name).setUsername("kdonald").build();			
			}
			
			@Override
			public void updateStatus(TestTwitterApi api, String message) {
			}
			
		}

	public AbstractUsersConnectionRepositoryTest() {
		super();
	}

}