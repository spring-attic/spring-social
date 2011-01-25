package org.springframework.social.provider.oauth2;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.List;

import org.junit.Test;
import org.springframework.security.oauth.client.oauth2.AccessGrant;
import org.springframework.security.oauth.client.oauth2.OAuth2Operations;
import org.springframework.social.provider.AuthorizationProtocol;
import org.springframework.social.provider.ServiceProviderConnection;
import org.springframework.social.provider.support.ConnectionRepository;
import org.springframework.social.provider.test.StubConnectionRepository;

public class OAuth2ServiceProviderTest {

	private ConnectionRepository connectionRepository = new StubConnectionRepository();

	private OAuth2ServiceProvider<TestApi> serviceProvider = new TestServiceProvider("54321", "65432", connectionRepository);

	@Test
	public void connectFlow() {
		// preconditions
		assertEquals(AuthorizationProtocol.OAUTH_2, serviceProvider.getAuthorizationProtocol());
		Long accountId = 1L;
		assertEquals(false, serviceProvider.isConnected(accountId));
		assertEquals(0, serviceProvider.getConnections(accountId).size());
		
		// oauth 2 dance
		OAuth2Operations oauthClient = serviceProvider.getOAuth2Operations();
		String authorizeUrl = oauthClient.buildAuthorizeUrl("http://localhost:8080/me", "READ_WRITE");
		assertEquals("http://springsource.org/oauth/authorize?scope=READ_WRITE", authorizeUrl);
		AccessGrant accessGrant = oauthClient.exchangeForAccess("authorizationGrant", "http://localhost:8080/me");

		// connect
		ServiceProviderConnection<TestApi> connection = serviceProvider.connect(accountId, accessGrant);
		TestApi api = connection.getApi();
		assertEquals("Hello Keith!", api.testOperation("Keith"));

		TestApiImpl impl = (TestApiImpl) api;
		assertEquals("12345", impl.getAccessToken());
		
		// additional postconditions
		assertEquals(true, serviceProvider.isConnected(accountId));
		List<ServiceProviderConnection<TestApi>> connections = serviceProvider.getConnections(accountId);
		assertEquals(1, connections.size());
		assertEquals("Hello Keith!", connections.get(0).getApi().testOperation("Keith"));

	}
	
	@Test
	public void equals() {
		Long accountId = 1L;
		AccessGrant accessGrant = new AccessGrant("12345", "23456");
		ServiceProviderConnection<TestApi> connection = serviceProvider.connect(accountId, accessGrant);
		List<ServiceProviderConnection<TestApi>> connections = serviceProvider.getConnections(accountId);		
		ServiceProviderConnection<TestApi> sameConnection = connections.get(0);
		assertEquals(connection, sameConnection);		
	}
	
	@Test
	public void disconnect() {
		Long accountId = 1L;
		AccessGrant accessGrant = new AccessGrant("12345", "23456");
		ServiceProviderConnection<TestApi> connection = serviceProvider.connect(accountId, accessGrant);	
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
		AccessGrant accessGrant = new AccessGrant("12345", "23456");
		serviceProvider.connect(accountId, accessGrant);
		try {
			serviceProvider.connect(accountId, accessGrant);
			fail("Should have failed on duplicate connection");
		} catch (IllegalArgumentException e) {
			
		}
	}
	
	static class TestServiceProvider extends AbstractOAuth2ServiceProvider<TestApi> {

		public TestServiceProvider(String consumerKey, String consumerSecret, ConnectionRepository connectionRepository) {
			super("test", connectionRepository, new StubOAuth2Operations());
		}

		protected TestApi getApi(String accessToken) {
			return new TestApiImpl(accessToken);
		}

	}
	
	interface TestApi {
		String testOperation(String arg);
	}
	
	static class TestApiImpl implements TestApi {

		private String accessToken;
		
		public TestApiImpl(String accessToken) {
			this.accessToken = accessToken;
		}
		
		public String getAccessToken() {
			return accessToken;
		}
		
		public String testOperation(String arg) {
			return "Hello " + arg + "!";
		}
		
	}

}
