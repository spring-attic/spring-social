package org.springframework.social.provider.oauth1;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.List;

import org.junit.Test;
import org.springframework.security.oauth.client.oauth1.AuthorizedRequestToken;
import org.springframework.security.oauth.client.oauth1.OAuth1Operations;
import org.springframework.security.oauth.client.oauth1.OAuthToken;
import org.springframework.social.provider.AuthorizationProtocol;
import org.springframework.social.provider.ServiceProviderConnection;
import org.springframework.social.provider.support.ConnectionRepository;
import org.springframework.social.provider.test.StubConnectionRepository;

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
		
		// oauth 1 dance
		OAuth1Operations oauthClient = serviceProvider.getOAuth1Operations();
		OAuthToken requestToken = oauthClient.fetchNewRequestToken("http://localhost:8080/me");
		String authorizeUrl = oauthClient.buildAuthorizeUrl(requestToken.getValue());
		assertEquals("http://springsource.org/oauth/authorize?request_token=12345", authorizeUrl);
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
	
	@Test
	public void duplicateConnection() {
		Long accountId = 1L;
		OAuthToken accessToken = new OAuthToken("12345", "23456");
		serviceProvider.connect(accountId, accessToken);
		try {
			serviceProvider.connect(accountId, accessToken);
			fail("Should have failed on duplicate connection");
		} catch (IllegalArgumentException e) {
			
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
