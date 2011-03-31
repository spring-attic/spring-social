package org.springframework.social.connect;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.springframework.social.connect.spi.ProviderProfile;
import org.springframework.social.connect.spi.ServiceApiAdapter;
import org.springframework.social.oauth1.OAuth1Operations;
import org.springframework.social.oauth1.OAuth1ServiceProvider;
import org.springframework.social.oauth1.OAuthToken;

public class OAuth1ServiceProviderConnectionFactoryTest {

	private TwitterServiceProviderConnectionFactory connectionFactory = new TwitterServiceProviderConnectionFactory();
	
	@Before
	public void setUp() {
	}
	
	@Test
	public void providerId() {
		assertEquals("twitter", connectionFactory.getProviderId());
	}
	
	@Test
	public void createConnectionFromAccessToken() {
		OAuthToken accessToken = new OAuthToken("123456789", "987654321");
		ServiceProviderConnection<TwitterApi> connection = connectionFactory.createConnection(accessToken);
		assertConnection(connection);		
	}
	
	@Test
	public void createConnectionFromMemento() {
		ServiceProviderConnectionMemento memento = new ServiceProviderConnectionMemento(null, null, "twitter", null, null, null, null, true, "123456789", "987654321", null);
		ServiceProviderConnection<TwitterApi> connection = connectionFactory.createConnection(memento);
		connection.sync();
		assertConnection(connection);		
	}

	private void assertConnection(ServiceProviderConnection<TwitterApi> connection) {
		assertNull(connection.getId());
		assertNull(connection.getAccountId());
		assertEquals("twitter", connection.getProviderId());
		assertEquals("1", connection.getProviderAccountId());
		assertEquals("kdonald", connection.getProfileName());
		assertEquals("http://twitter.com/kdonald", connection.getProfileUrl());
		assertEquals("http://twitter.com/kdonald/picture", connection.getProfilePictureUrl());
		assertTrue(connection.allowSignIn());
		assertTrue(connection.test());
		TwitterApi api = connection.getServiceApi();
		assertNotNull(api);
		assertEquals("123456789", api.getAccessToken());
		assertEquals("987654321", api.getSecret());
		assertEquals("123456789", connection.createMemento().getAccessToken());
		assertEquals("987654321", connection.createMemento().getSecret());
	}
		
	static class TwitterServiceProviderConnectionFactory extends OAuth1ServiceProviderConnectionFactory<TwitterApi> {

		public TwitterServiceProviderConnectionFactory() {
			super("twitter", new TwitterServiceProvider(), new TwitterServiceApiAdapter(), true);
		}
		
	}

	static class TwitterServiceProvider implements OAuth1ServiceProvider<TwitterApi> {

		public OAuth1Operations getOAuthOperations() {
			return null;
		}

		public TwitterApi getServiceApi(final String accessToken, final String secret) {
			return new TwitterApi() {
				public String getAccessToken() {
					return accessToken;
				}
				public String getSecret() {
					return secret;
				}
			};
		}
		
	}
		
	interface TwitterApi {
		
		String getAccessToken();
		
		String getSecret();
		
	}
	
	static class TwitterServiceApiAdapter implements ServiceApiAdapter<TwitterApi> {

		public boolean test(TwitterApi serviceApi) {
			return true;
		}

		public ProviderProfile getProfile(TwitterApi serviceApi) {
			return new ProviderProfile("1", "kdonald", "http://twitter.com/kdonald", "http://twitter.com/kdonald/picture");
		}

		public void updateStatus(TwitterApi serviceApi, String message) {
			
		}
		
	}
}
