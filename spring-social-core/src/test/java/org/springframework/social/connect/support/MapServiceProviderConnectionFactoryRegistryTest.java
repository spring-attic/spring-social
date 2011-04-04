package org.springframework.social.connect.support;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import org.junit.Before;
import org.junit.Test;
import org.springframework.social.connect.ServiceProviderConnectionFactory;
import org.springframework.social.connect.support.MapServiceProviderConnectionFactoryRegistry;
import org.springframework.social.connect.support.OAuth1ServiceProviderConnectionFactory;
import org.springframework.social.oauth1.OAuth1Operations;
import org.springframework.social.oauth1.OAuth1ServiceProvider;

public class MapServiceProviderConnectionFactoryRegistryTest {

	private MapServiceProviderConnectionFactoryRegistry connectionFactoryLocator = new MapServiceProviderConnectionFactoryRegistry();

	private TwitterServiceProviderConnectionFactory twitterConnectionFactory = new TwitterServiceProviderConnectionFactory();
	
	@Before
	public void setUp() {
		connectionFactoryLocator.addConnectionFactory(twitterConnectionFactory);
	}
	
	@Test
	public void getConnectionFactoryByProviderId() {
		ServiceProviderConnectionFactory<?> connectionFactory = connectionFactoryLocator.getConnectionFactory("twitter");
		assertSame(twitterConnectionFactory, connectionFactory);
		assertEquals("twitter", connectionFactory.getProviderId());
	}
	
	@Test
	public void getConnectionFactoryByServiceApi() {
		ServiceProviderConnectionFactory<TwitterApi> connectionFactory = connectionFactoryLocator.getConnectionFactory(TwitterApi.class);
		assertSame(twitterConnectionFactory, connectionFactory);
		assertEquals("twitter", connectionFactory.getProviderId());
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void addDuplicateProviderId() {
		connectionFactoryLocator.addConnectionFactory(new TwitterServiceProviderConnectionFactory());
	}

	@Test(expected=IllegalArgumentException.class)
	public void addDuplicateServiceApiType() {
		connectionFactoryLocator.addConnectionFactory(new Twitter2ServiceProviderConnectionFactory());
	}
	
	static class TwitterServiceProviderConnectionFactory extends OAuth1ServiceProviderConnectionFactory<TwitterApi> {

		public TwitterServiceProviderConnectionFactory() {
			super("twitter", new TwitterServiceProvider(), null);
		}
		
	}

	static class Twitter2ServiceProviderConnectionFactory extends OAuth1ServiceProviderConnectionFactory<TwitterApi> {

		public Twitter2ServiceProviderConnectionFactory() {
			super("twitter2", new TwitterServiceProvider(), null);
		}
		
	}
	
	static class TwitterServiceProvider implements OAuth1ServiceProvider<TwitterApi> {

		public OAuth1Operations getOAuthOperations() {
			return null;
		}

		public TwitterApi getServiceApi(String accessToken, String secret) {
			return null;
		}
		
	}
		
	interface TwitterApi {
		
	}
}
