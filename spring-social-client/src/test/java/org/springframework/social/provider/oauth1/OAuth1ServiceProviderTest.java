package org.springframework.social.provider.oauth1;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.Serializable;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.springframework.security.oauth.client.oauth1.AuthorizedRequestToken;
import org.springframework.security.oauth.client.oauth1.OAuth1Operations;
import org.springframework.security.oauth.client.oauth1.OAuthToken;
import org.springframework.social.provider.AuthorizationProtocol;
import org.springframework.social.provider.ServiceProviderConnection;
import org.springframework.social.provider.support.Connection;
import org.springframework.social.provider.support.ConnectionRepository;

public class OAuth1ServiceProviderTest {

	private ConnectionRepository connectionRepository = new StubConnectionRepository();

	private OAuth1ServiceProvider<TestApi> serviceProvider = new TestServiceProvider("54321", "65432", connectionRepository);

	@Test
	public void connectFlow() {
		// preconditions
		assertEquals(AuthorizationProtocol.OAUTH_1, serviceProvider.getAuthorizationProtocol());
		Long accountId = 1L;
		assertEquals(false, serviceProvider.isConnected(accountId));
		assertEquals(0, serviceProvider.getConnections(accountId).size());
		
		// oauth dance
		OAuth1Operations oauthClient = serviceProvider.getOAuth1Operations();
		OAuthToken requestToken = oauthClient.fetchNewRequestToken("http://localhost:8080/me");
		String authorizeUrl = oauthClient.buildAuthorizeUrl(requestToken.getValue());
		assertEquals("http://springsource.org/oauth/request_token?token=12345", authorizeUrl);
		OAuthToken accessToken = oauthClient.exchangeForAccessToken(new AuthorizedRequestToken(requestToken, "verifier"));

		// connect
		ServiceProviderConnection<TestApi> connection = serviceProvider.connect(accountId, accessToken);
		TestApi api = connection.getApi();
		assertEquals("Hello Keith!", api.testOperation("Keith"));

		TestApiImpl impl = (TestApiImpl) api;
		assertEquals("54321", impl.getConsumerKey());
		assertEquals("65432", impl.getConsumerSecret());
		assertEquals("34567", impl.getAccessToken());
		assertEquals("45678", impl.getSecret());
		
		// postconditions
		assertEquals(true, serviceProvider.isConnected(accountId));
		List<ServiceProviderConnection<TestApi>> connections = serviceProvider.getConnections(accountId);
		assertEquals(1, connections.size());
		ServiceProviderConnection<TestApi> sameConnection = connections.get(0);
		assertEquals(connection, sameConnection);
		assertEquals("Hello Keith!", connection.getApi().testOperation("Keith"));

		connection.disconnect();
		assertEquals(false, serviceProvider.isConnected(accountId));
		assertEquals(0, serviceProvider.getConnections(accountId).size());

		try {
			connection.getApi();
			fail("Should be disconnected");
		} catch (IllegalStateException e) {
			
		}
		
		try {
			connection.disconnect();
			fail("Should already be disconnected");
		} catch (IllegalStateException e) {
			
		}
		
	}
	
	static class StubConnectionRepository implements ConnectionRepository {

		private SecureRandom secureRandom = new SecureRandom();
		
		private List<Map<String, Object>> connections = new ArrayList<Map<String, Object>>();
		
		public boolean isConnected(Serializable accountId, String providerId) {
			for (Map<String, Object> connection : connections) {
				if (connection.get("accountId").equals(accountId) && connection.get("providerId").equals(providerId)) {
					return true;
				}
			}
			return false;
		}

		public List<Connection> findConnections(Serializable accountId, String providerId) {
			List<Connection> connectionList = new ArrayList<Connection>();
			for (Map<String, Object> connection : connections) {
				if (connection.get("accountId").equals(accountId) && connection.get("providerId").equals(providerId)) {
					connectionList.add(new Connection((Long) connection.get("id"), (String) connection.get("accessToken"), (String) connection.get("secret"), (String) connection.get("refreshToken")));
				}
			}
			return connectionList;
		}

		public Connection saveConnection(Serializable accountId, String providerId, Connection connection) {
			for (Iterator<Map<String, Object>> it = connections.iterator(); it.hasNext();) {
				Map<String, Object> conn = it.next();
				if (conn.get("accountId").equals(accountId) && conn.get("providerId").equals(providerId) && conn.get("accessToken").equals(connection.getAccessToken())) {
					throw new IllegalArgumentException("Duplicate connection");
				}
			}
			Long connectionId = secureRandom.nextLong();
			Map<String, Object> newConn = new HashMap<String, Object>();
			newConn.put("accountId", accountId);
			newConn.put("providerId", providerId);
			newConn.put("accessToken", connection.getAccessToken());
			newConn.put("secret", connection.getSecret());
			newConn.put("refreshToken", connection.getRefreshToken());
			newConn.put("id", connectionId);
			connections.add(newConn);
			return new Connection(connectionId, connection.getAccessToken(), connection.getSecret(), connection.getRefreshToken());
		}

		public void removeConnection(Serializable accountId, String providerId, Long connectionId) {
			for (Iterator<Map<String, Object>> it = connections.iterator(); it.hasNext();) {
				Map<String, Object> connection = it.next();
				if (connection.get("accountId").equals(accountId) && connection.get("providerId").equals(providerId) && connection.get("id").equals(connectionId)) {
					it.remove();
					break;
				}
			}
		}
		
	}
	
	static class TestServiceProvider extends AbstractOAuth1ServiceProvider<TestApi> {

		public TestServiceProvider(String consumerKey, String consumerSecret, ConnectionRepository connectionRepository) {
			super("test", connectionRepository, consumerKey, consumerSecret, new StubOAuth1Operations());
		}

		protected TestApi getApi(String consumerKey, String consumerSecret, String accessToken, String secret) {
			return new TestApiImpl(consumerKey, consumerSecret, accessToken, secret);
		}

	}
	
	static class StubOAuth1Operations implements OAuth1Operations {

		public OAuthToken fetchNewRequestToken(String callbackUrl) {
			return new OAuthToken("12345", "23456");
		}

		public String buildAuthorizeUrl(String requestToken) {
			return "http://springsource.org/oauth/request_token?token=" + requestToken;
		}

		public OAuthToken exchangeForAccessToken(AuthorizedRequestToken requestToken) {
			return new OAuthToken("34567", "45678");
		}
		
	}
	
	interface TestApi {
		String testOperation(String arg);
	}
	
	static class TestApiImpl implements TestApi {

		private String consumerKey;
		
		private String consumerSecret;
		
		private String accessToken;
		
		private String secret;
		
		public TestApiImpl(String consumerKey, String consumerSecret, String accessToken, String secret) {
			this.consumerKey = consumerKey;
			this.consumerSecret = consumerSecret;
			this.accessToken = accessToken;
			this.secret = secret;
		}

		public String getConsumerKey() {
			return consumerKey;
		}

		public String getConsumerSecret() {
			return consumerSecret;
		}

		public String getAccessToken() {
			return accessToken;
		}

		public String getSecret() {
			return secret;
		}

		public String testOperation(String arg) {
			return "Hello " + arg + "!";
		}
		
	}
}
