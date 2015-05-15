/*
 * Copyright 2015 the original author or authors.
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
package org.springframework.social.connect.mem;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.After;
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

public class InMemoryUsersConnectionRepositoryTest {

	private ConnectionFactoryRegistry connectionFactoryRegistry;
	
	private ConnectionRepository connectionRepository;

	private TestFacebookConnectionFactory facebookConnectionFactory;

	private TestTwitterConnectionFactory twitterConnectionFactory;

	private InMemoryUsersConnectionRepository usersConnectionRepository;

	@Before
	public void setUp() {
		connectionFactoryRegistry = new ConnectionFactoryRegistry();
		usersConnectionRepository = new InMemoryUsersConnectionRepository(connectionFactoryRegistry);
		connectionRepository = usersConnectionRepository.createConnectionRepository("1");
		facebookConnectionFactory = registerFacebookConnectionFactory();
		twitterConnectionFactory = registerTwitterConnectionFactory();
	}
	
	@After
	public void tearDown() {
		connectionFactoryRegistry = null;
	}
	
	@Test
	public void findUserIdWithConnection() {
		insertFacebookConnection();
		List<String> userIds = usersConnectionRepository.findUserIdsWithConnection(connectionRepository.getPrimaryConnection(TestFacebookApi.class));
		assertEquals("1", userIds.get(0));
	}

	@Test
	public void findUserIdWithConnectionNoSuchConnection() {
		Connection<TestFacebookApi> connection = facebookConnectionFactory.createConnection(new AccessGrant("12345"));
		assertEquals(0, usersConnectionRepository.findUserIdsWithConnection(connection).size());
	}
	
	@Test
	public void findUserIdWithConnectionMultipleConnectionsToSameProviderUser() {
		insertFacebookConnection();
		insertFacebookConnectionSameFacebookUser();
		List<String> localUserIds = usersConnectionRepository.findUserIdsWithConnection(connectionRepository.getPrimaryConnection(TestFacebookApi.class));
		assertEquals(2, localUserIds.size());
		assertTrue(localUserIds.contains("1"));
		assertTrue(localUserIds.contains("2"));
	}

	@Test
	public void findUserIdWithConnectionNoConnection_withWorkingConnectionSignUp() {		
		Connection<TestFacebookApi> connection = facebookConnectionFactory.createConnection(new AccessGrant("12345"));
		usersConnectionRepository.setConnectionSignUp(new ConnectionSignUp() {
			public String execute(Connection<?> connection) {
				return "batman";
			}
		});
		List<String> userIds = usersConnectionRepository.findUserIdsWithConnection(connection);
		assertEquals(1, userIds.size());
		assertEquals("batman", userIds.get(0));
	}

	@Test
	public void findUserIdWithConnectionNoConnection_withConnectionSignUpReturningNull() {		
		Connection<TestFacebookApi> connection = facebookConnectionFactory.createConnection(new AccessGrant("12345"));
		usersConnectionRepository.setConnectionSignUp(new ConnectionSignUp() {
			public String execute(Connection<?> connection) {
				return null;
			}
		});
		List<String> userIds = usersConnectionRepository.findUserIdsWithConnection(connection);
		assertEquals(0, userIds.size());
	}
	
	@Test
	public void findUserIdsConnectedTo() {
		insertFacebookConnection();
		insertFacebookConnection3();
		Set<String> localUserIds = usersConnectionRepository.findUserIdsConnectedTo("facebook", new HashSet<String>(Arrays.asList("12345", "45678")));
		assertEquals(2, localUserIds.size());
		assertTrue(localUserIds.contains("1"));
		assertTrue(localUserIds.contains("2"));		
	}
	
	@Test
	@SuppressWarnings("unchecked")
	public void findAllConnections() {
		insertTwitterConnection();
		insertFacebookConnection();
		MultiValueMap<String, Connection<?>> connections = connectionRepository.findAllConnections();
		assertEquals(2, connections.size());
		Connection<TestTwitterApi> twitter = (Connection<TestTwitterApi>) connections.getFirst("twitter");
		assertTwitterConnection(twitter);
		Connection<TestFacebookApi> facebook = (Connection<TestFacebookApi>) connections.getFirst("facebook");
		assertFacebookConnection(facebook);
	}

	@Test
	public void findAllConnectionsMultipleConnectionResults() {
		insertTwitterConnection();
		insertFacebookConnection();
		insertFacebookConnection2();
		MultiValueMap<String, Connection<?>> connections = connectionRepository.findAllConnections();
		assertEquals(1, connections.get("twitter").size());
		assertEquals(2, connections.size());
		assertEquals(2, connections.get("facebook").size());
	}
	
	@Test
	public void findAllConnectionsEmptyResult() {
		MultiValueMap<String, Connection<?>> connections = connectionRepository.findAllConnections();
		assertEquals(0, connections.size());
	}	
	
	@Test
	@SuppressWarnings("unchecked")
	public void findConnectionsByProviderId() {
		insertTwitterConnection();
		List<Connection<?>> connections = connectionRepository.findConnections("twitter");
		assertEquals(1, connections.size());
		assertTwitterConnection((Connection<TestTwitterApi>) connections.get(0));
	}

	@Test
	public void findConnectionsByProviderIdEmptyResult() {
		assertTrue(connectionRepository.findConnections("facebook").isEmpty());
	}
	
	@Test
	public void findConnectionsByApi() {
		insertFacebookConnection();
		insertFacebookConnection2();
		List<Connection<TestFacebookApi>> connections = connectionRepository.findConnections(TestFacebookApi.class);
		assertEquals(2, connections.size());
		assertFacebookConnection(connections.get(0));
	}
	
	@Test
	public void findConnectionsByApiEmptyResult() {
		assertTrue(connectionRepository.findConnections(TestFacebookApi.class).isEmpty());
	}

	@Test
	@SuppressWarnings("unchecked")
	public void findConnectionsToUsers() {
		insertTwitterConnection();
		insertFacebookConnection();
		insertFacebookConnection2();
		MultiValueMap<String, String> providerUsers = new LinkedMultiValueMap<String, String>();
		providerUsers.add("facebook", "9");
		providerUsers.add("facebook", "12345");
		providerUsers.add("twitter", "habuma");
		MultiValueMap<String, Connection<?>> connectionsForUsers = connectionRepository.findConnectionsToUsers(providerUsers);
		assertEquals(2, connectionsForUsers.size());
		assertEquals("12345", connectionsForUsers.getFirst("facebook").getKey().getProviderUserId());
		assertFacebookConnection((Connection<TestFacebookApi>) connectionsForUsers.get("facebook").get(0));
		assertTwitterConnection((Connection<TestTwitterApi>) connectionsForUsers.getFirst("twitter"));
	}

	@Test
	public void findConnectionsToUsersEmptyResult() {
		MultiValueMap<String, String> providerUsers = new LinkedMultiValueMap<String, String>();
		providerUsers.add("facebook", "12345");
		assertTrue(connectionRepository.findConnectionsToUsers(providerUsers).isEmpty());
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void findConnectionsToUsersEmptyInput() {
		MultiValueMap<String, String> providerUsers = new LinkedMultiValueMap<String, String>();
		connectionRepository.findConnectionsToUsers(providerUsers);
	}

	@Test
	@SuppressWarnings("unchecked")
	public void findConnectionByKey() {
		insertFacebookConnection();
		assertFacebookConnection((Connection<TestFacebookApi>) connectionRepository.getConnection(new ConnectionKey("facebook", "12345")));
	}
	
	@Test(expected=NoSuchConnectionException.class)
	public void findConnectionByKeyNoSuchConnection() {
		connectionRepository.getConnection(new ConnectionKey("facebook", "bogus"));
	}
	
	@Test
	public void findConnectionByApiToUser() {
		insertFacebookConnection();
		insertFacebookConnection2();	
		assertFacebookConnection(connectionRepository.getConnection(TestFacebookApi.class, "12345"));
		assertEquals("54321", connectionRepository.getConnection(TestFacebookApi.class, "54321").getKey().getProviderUserId());
	}

	@Test(expected=NoSuchConnectionException.class)
	public void findConnectionByApiToUserNoSuchConnection() {
		assertFacebookConnection(connectionRepository.getConnection(TestFacebookApi.class, "54321"));
	}
	
	@Test
	public void findPrimaryConnection() {
		insertFacebookConnection();
		assertFacebookConnection(connectionRepository.getPrimaryConnection(TestFacebookApi.class));
	}

	@Test(expected=NotConnectedException.class)
	public void findPrimaryConnectionNotConnected() {
		connectionRepository.getPrimaryConnection(TestFacebookApi.class);
	}

	@Test
	public void removeConnections() {
		insertFacebookConnection();
		insertFacebookConnection2();
		assertEquals(2, connectionRepository.findConnections("facebook").size());
		connectionRepository.removeConnections("facebook");
		assertEquals(0, connectionRepository.findConnections("facebook").size());
	}

	@Test
	public void removeConnectionsToProviderNoOp() {
		connectionRepository.removeConnections("twitter");
	}
	
	@Test
	public void removeConnection() {
		insertFacebookConnection();
		insertFacebookConnection2();
		assertEquals(2, connectionRepository.findConnections("facebook").size());
		connectionRepository.removeConnection(new ConnectionKey("facebook", "12345"));
		assertEquals(1, connectionRepository.findConnections("facebook").size());
	}

	@Test
	public void removeConnectionNoOp() {
		connectionRepository.removeConnection(new ConnectionKey("facebook", "1"));
	}
	
	@Test
	public void addConnection() {
		Connection<TestFacebookApi> connection = facebookConnectionFactory.createConnection(new AccessGrant("123456789", null, "987654321", 3600L));
		connectionRepository.addConnection(connection);
		Connection<TestFacebookApi> restoredConnection = connectionRepository.getPrimaryConnection(TestFacebookApi.class);
		assertEquals(connection, restoredConnection);	
		assertNewConnection(restoredConnection);
	}

	@Test(expected=DuplicateConnectionException.class)
	public void addConnectionDuplicate() {
		Connection<TestFacebookApi> connection = facebookConnectionFactory.createConnection(new AccessGrant("123456789", null, "987654321", 3600L));
		connectionRepository.addConnection(connection);
		connectionRepository.addConnection(connection);
	}

	@Test
	public void updateConnectionProfileFields() {
		insertTwitterConnection();
		Connection<TestTwitterApi> twitter = connectionRepository.getPrimaryConnection(TestTwitterApi.class);
		assertEquals("http://twitter.com/habuma/picture", twitter.getImageUrl());
		twitter.sync();
		assertEquals("http://twitter.com/habuma/a_new_picture", twitter.getImageUrl());
		connectionRepository.updateConnection(twitter);
		Connection<TestTwitterApi> twitter2 = connectionRepository.getPrimaryConnection(TestTwitterApi.class);
		assertEquals("http://twitter.com/habuma/a_new_picture", twitter2.getImageUrl());
	}
	
	@Test
	public void updateConnectionAccessFields() {
		insertFacebookConnection();
		Connection<TestFacebookApi> facebook = connectionRepository.getPrimaryConnection(TestFacebookApi.class);
		assertEquals("ACCESS_TOKEN", facebook.getApi().getAccessToken());
		facebook.refresh();
		connectionRepository.updateConnection(facebook);
		Connection<TestFacebookApi> facebook2 = connectionRepository.getPrimaryConnection(TestFacebookApi.class);
		assertEquals("765432109", facebook2.getApi().getAccessToken());
		ConnectionData data = facebook.createData();
		assertEquals("654321098", data.getRefreshToken());
	}


	// PRIVATE SUPPORT METHODS

	private TestFacebookConnectionFactory registerFacebookConnectionFactory() {
		TestFacebookConnectionFactory facebookConnectionFactory = new TestFacebookConnectionFactory();
		connectionFactoryRegistry.addConnectionFactory(facebookConnectionFactory);
		return facebookConnectionFactory;
	}

	private void insertFacebookConnection() {
		Connection<TestFacebookApi> facebookConnection = facebookConnectionFactory.createConnection(new ConnectionData("facebook", "12345", "Craig Walls", "http://facebook.com/habuma", "http://facebook.com/habuma/picture", "ACCESS_TOKEN", "SECRET", null, null));		
		connectionRepository.addConnection(facebookConnection);
	}

	private void insertFacebookConnection2() {
		Connection<TestFacebookApi> facebookConnection = facebookConnectionFactory.createConnection(new ConnectionData("facebook", "54321", "Chuck Wagon", "http://facebook.com/cwagon", "http://facebook.com/cwagon/picture", "ACCESS_TOKEN2", "SECRET", null, null));		
		connectionRepository.addConnection(facebookConnection);
	}

	private void insertFacebookConnection3() {
		Connection<TestFacebookApi> facebookConnection = facebookConnectionFactory.createConnection(new ConnectionData("facebook", "45678", "Art Names", "http://facebook.com/art", "http://facebook.com/art/picture", "ACCESS_TOKEN3", "SECRET", null, null));		
		usersConnectionRepository.createConnectionRepository("2").addConnection(facebookConnection);
	}

	private void insertFacebookConnectionSameFacebookUser() {
		Connection<TestFacebookApi> facebookConnection = facebookConnectionFactory.createConnection(new ConnectionData("facebook", "12345", "Craig Walls", "http://facebook.com/habuma", "http://facebook.com/habuma/picture", "ACCESS_TOKEN", "SECRET", null, null));		
		usersConnectionRepository.createConnectionRepository("2").addConnection(facebookConnection);
	}

	private void insertTwitterConnection() {
		Connection<TestTwitterApi> twitterConnection = twitterConnectionFactory.createConnection(new ConnectionData("twitter", "habuma", "@habuma", "http://twitter.com/habuma", "http://twitter.com/habuma/picture", "ACCESS_TOKEN", "SECRET", "REFRESH_TOKEN", null));
		connectionRepository.addConnection(twitterConnection);
	}

	private TestTwitterConnectionFactory registerTwitterConnectionFactory() {
		TestTwitterConnectionFactory twitterConnectionFactory = new TestTwitterConnectionFactory();
		connectionFactoryRegistry.addConnectionFactory(twitterConnectionFactory);
		return twitterConnectionFactory;
	}

	
	private void assertTwitterConnection(Connection<TestTwitterApi> twitter) {
		assertEquals(new ConnectionKey("twitter", "habuma"), twitter.getKey());
		assertEquals("@habuma", twitter.getDisplayName());
		assertEquals("http://twitter.com/habuma", twitter.getProfileUrl());
		assertEquals("http://twitter.com/habuma/picture", twitter.getImageUrl());
		TestTwitterApi twitterApi = twitter.getApi();
		assertEquals("ACCESS_TOKEN", twitterApi.getAccessToken());		
		assertEquals("SECRET", twitterApi.getSecret());
		twitter.sync();
		assertEquals("http://twitter.com/habuma/a_new_picture", twitter.getImageUrl());
	}
	
	private void assertFacebookConnection(Connection<TestFacebookApi> facebook) {
		assertEquals(new ConnectionKey("facebook", "12345"), facebook.getKey());
		assertEquals("Craig Walls", facebook.getDisplayName());
		assertEquals("http://facebook.com/habuma", facebook.getProfileUrl());
		assertEquals("http://facebook.com/habuma/picture", facebook.getImageUrl());
		TestFacebookApi facebookApi = facebook.getApi();
		assertEquals("ACCESS_TOKEN", facebookApi.getAccessToken());
		facebook.sync();
		assertEquals("Craig Walls", facebook.getDisplayName());
		assertEquals("http://facebook.com/habuma", facebook.getProfileUrl());
		assertEquals("http://facebook.com/habuma/picture", facebook.getImageUrl());		
	}

	private void assertNewConnection(Connection<TestFacebookApi> connection) {
		assertEquals("facebook", connection.getKey().getProviderId());
		assertEquals("12345", connection.getKey().getProviderUserId());
		assertEquals("Craig Walls", connection.getDisplayName());
		assertEquals("http://facebook.com/habuma", connection.getProfileUrl());
		assertEquals("http://facebook.com/habuma/picture", connection.getImageUrl());
		assertTrue(connection.test());
		TestFacebookApi api = connection.getApi();
		assertNotNull(api);
		assertEquals("123456789", api.getAccessToken());
		assertEquals("123456789", connection.createData().getAccessToken());
		assertEquals("987654321", connection.createData().getRefreshToken());
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
				public String buildAuthorizeUrl(OAuth2Parameters params) {
					return null;
				}
				public String buildAuthenticateUrl(OAuth2Parameters params) {
					return null;
				}
				public AccessGrant exchangeForAccess(String authorizationGrant, String redirectUri, MultiValueMap<String, String> additionalParameters) {
					return null;
				}				
				public AccessGrant exchangeCredentialsForAccess(String username, String password, MultiValueMap<String, String> additionalParameters) {
					return null;
				}
				public AccessGrant refreshAccess(String refreshToken, MultiValueMap<String, String> additionalParameters) {
					return new AccessGrant("765432109", "read", "654321098", 3600L);
				}
				@Deprecated
				public AccessGrant refreshAccess(String refreshToken, String scope, MultiValueMap<String, String> additionalParameters) {
					return new AccessGrant("765432109", "read", "654321098", 3600L);
				}
				public AccessGrant authenticateClient() {
					return null;
				}
				public AccessGrant authenticateClient(String scope) {
					return null;
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

		private String accountId = "12345";
		
		private String name = "Craig Walls";
		
		private String profileUrl = "http://facebook.com/habuma";
		
		private String profilePictureUrl = "http://facebook.com/habuma/picture";
		
		public boolean test(TestFacebookApi api) {
			return true;
		}

		public void setConnectionValues(TestFacebookApi api, ConnectionValues values) {
			values.setProviderUserId(accountId);
			values.setDisplayName(name);
			values.setProfileUrl(profileUrl);
			values.setImageUrl(profilePictureUrl);
		}

		public UserProfile fetchUserProfile(TestFacebookApi api) {
			return new UserProfileBuilder().setName(name).setEmail("cwalls@gopivotal.com").setUsername("Craig Walls").build();
		}

		public void updateStatus(TestFacebookApi api, String message) {
			
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
		
		private String name = "@habuma";
		
		private String profileUrl = "http://twitter.com/habuma";
		
		private String profilePictureUrl = "http://twitter.com/habuma/a_new_picture";
		
		public boolean test(TestTwitterApi api) {
			return true;
		}

		public void setConnectionValues(TestTwitterApi api, ConnectionValues values) {
			values.setProviderUserId(accountId);
			values.setDisplayName(name);
			values.setProfileUrl(profileUrl);
			values.setImageUrl(profilePictureUrl);
		}

		public UserProfile fetchUserProfile(TestTwitterApi api) {
			return new UserProfileBuilder().setName(name).setUsername("habuma").build();			
		}
		
		public void updateStatus(TestTwitterApi api, String message) {
		}
		
	}

}
