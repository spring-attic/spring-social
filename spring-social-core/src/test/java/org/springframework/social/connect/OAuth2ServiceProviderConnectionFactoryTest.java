package org.springframework.social.connect;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.springframework.social.connect.spi.ProviderProfile;
import org.springframework.social.connect.spi.ServiceApiAdapter;
import org.springframework.social.oauth2.AccessGrant;
import org.springframework.social.oauth2.OAuth2Operations;
import org.springframework.social.oauth2.OAuth2ServiceProvider;

public class OAuth2ServiceProviderConnectionFactoryTest {

	private FacebookServiceProviderConnectionFactory connectionFactory = new FacebookServiceProviderConnectionFactory();
	
	@Before
	public void setUp() {
	}
	
	@Test
	public void providerId() {
		assertEquals("facebook", connectionFactory.getProviderId());
	}
	
	@Test
	public void createConnectionFromAccessGrant() {
		AccessGrant accessGrant = new AccessGrant("123456789", "987654321");
		ServiceProviderConnection<FacebookApi> connection = connectionFactory.createConnection(accessGrant);
		assertConnection(connection);		
	}
	
	@Test
	public void createConnectionFromMemento() {
		ServiceProviderConnectionMemento memento = new ServiceProviderConnectionMemento(null, null, "facebook", null, null, null, null, true, "123456789", null, "987654321");
		ServiceProviderConnection<FacebookApi> connection = connectionFactory.createConnection(memento);
		connection.sync();
		assertConnection(connection);		
	}

	private void assertConnection(ServiceProviderConnection<FacebookApi> connection) {
		assertNull(connection.getId());
		assertNull(connection.getAccountId());
		assertEquals("facebook", connection.getProviderId());
		assertEquals("1", connection.getProviderAccountId());
		assertEquals("Keith Donald", connection.getProfileName());
		assertEquals("http://facebook.com/keith.donald", connection.getProfileUrl());
		assertEquals("http://facebook.com/keith.donald/picture", connection.getProfilePictureUrl());
		assertTrue(connection.allowSignIn());
		assertTrue(connection.test());
		FacebookApi api = connection.getServiceApi();
		assertNotNull(api);
		assertEquals("123456789", api.getAccessToken());
		assertEquals("123456789", connection.createMemento().getAccessToken());
		assertEquals("987654321", connection.createMemento().getRefreshToken());
	}
		
	static class FacebookServiceProviderConnectionFactory extends OAuth2ServiceProviderConnectionFactory<FacebookApi> {

		public FacebookServiceProviderConnectionFactory() {
			super("facebook", new FacebookServiceProvider(), new FacebookServiceApiAdapter(), true);
		}
		
	}

	static class FacebookServiceProvider implements OAuth2ServiceProvider<FacebookApi> {

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
		
	interface FacebookApi {
		
		String getAccessToken();
		
	}
	
	static class FacebookServiceApiAdapter implements ServiceApiAdapter<FacebookApi> {

		public boolean test(FacebookApi serviceApi) {
			return true;
		}

		public ProviderProfile getProfile(FacebookApi serviceApi) {
			return new ProviderProfile("1", "Keith Donald", "http://facebook.com/keith.donald", "http://facebook.com/keith.donald/picture");
		}

		public void updateStatus(FacebookApi serviceApi, String message) {
			
		}
		
	}
}
