package org.springframework.social.connect.support;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import org.junit.Before;
import org.junit.Test;
import org.springframework.social.connect.ServiceProviderConnectionFactory;
import org.springframework.social.oauth1.OAuth1Operations;
import org.springframework.social.oauth1.OAuth1ServiceProvider;

public class MapServiceProviderConnectionFactoryRegistryTest {

	private MapServiceProviderConnectionFactoryRegistry connectionFactoryLocator = new MapServiceProviderConnectionFactoryRegistry();

	private TestTwitterServiceProviderConnectionFactory twitterConnectionFactory = new TestTwitterServiceProviderConnectionFactory();
	
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
		ServiceProviderConnectionFactory<TestTwitterApi> connectionFactory = connectionFactoryLocator.getConnectionFactory(TestTwitterApi.class);
		assertSame(twitterConnectionFactory, connectionFactory);
		assertEquals("twitter", connectionFactory.getProviderId());
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void addDuplicateProviderId() {
		connectionFactoryLocator.addConnectionFactory(new TestTwitterServiceProviderConnectionFactory());
	}

	@Test(expected=IllegalArgumentException.class)
	public void addDuplicateServiceApiType() {
		connectionFactoryLocator.addConnectionFactory(new TestTwitter2ServiceProviderConnectionFactory());
	}
	
	static class TestTwitterServiceProviderConnectionFactory extends OAuth1ServiceProviderConnectionFactory<TestTwitterApi> {

		public TestTwitterServiceProviderConnectionFactory() {
			super("twitter", new TestTwitterServiceProvider(), null);
		}
		
	}

	static class TestTwitter2ServiceProviderConnectionFactory extends OAuth1ServiceProviderConnectionFactory<TestTwitterApi> {

		public TestTwitter2ServiceProviderConnectionFactory() {
			super("twitter2", new TestTwitterServiceProvider(), null);
		}
		
	}
	
	static class TestTwitterServiceProvider implements OAuth1ServiceProvider<TestTwitterApi> {

		public OAuth1Operations getOAuthOperations() {
			return null;
		}

		public TestTwitterApi getServiceApi(String accessToken, String secret) {
			return null;
		}
		
	}
		
	interface TestTwitterApi {
		
	}
}
