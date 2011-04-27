package org.springframework.social.connect.support;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import org.junit.Before;
import org.junit.Test;
import org.springframework.social.connect.ConnectionFactory;
import org.springframework.social.oauth1.OAuth1Operations;
import org.springframework.social.oauth1.OAuth1ServiceProvider;

public class ConnectionFactoryRegistryTest {

	private ConnectionFactoryRegistry connectionFactoryLocator = new ConnectionFactoryRegistry();

	private TestTwitterConnectionFactory twitterConnectionFactory = new TestTwitterConnectionFactory();
	
	@Before
	public void setUp() {
		connectionFactoryLocator.addConnectionFactory(twitterConnectionFactory);
	}
	
	@Test
	public void getConnectionFactoryByProviderId() {
		ConnectionFactory<?> connectionFactory = connectionFactoryLocator.getConnectionFactory("twitter");
		assertSame(twitterConnectionFactory, connectionFactory);
		assertEquals("twitter", connectionFactory.getProviderId());
	}
	
	@Test
	public void getConnectionFactoryByApi() {
		ConnectionFactory<TestTwitterApi> connectionFactory = connectionFactoryLocator.getConnectionFactory(TestTwitterApi.class);
		assertSame(twitterConnectionFactory, connectionFactory);
		assertEquals("twitter", connectionFactory.getProviderId());
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void addDuplicateProviderId() {
		connectionFactoryLocator.addConnectionFactory(new TestTwitterConnectionFactory());
	}

	@Test(expected=IllegalArgumentException.class)
	public void addDuplicateApiType() {
		connectionFactoryLocator.addConnectionFactory(new TestTwitter2ConnectionFactory());
	}
	
	static class TestTwitterConnectionFactory extends OAuth1ConnectionFactory<TestTwitterApi> {

		public TestTwitterConnectionFactory() {
			super("twitter", new TestTwitterServiceProvider(), null);
		}
		
	}

	static class TestTwitter2ConnectionFactory extends OAuth1ConnectionFactory<TestTwitterApi> {

		public TestTwitter2ConnectionFactory() {
			super("twitter2", new TestTwitterServiceProvider(), null);
		}
		
	}
	
	static class TestTwitterServiceProvider implements OAuth1ServiceProvider<TestTwitterApi> {

		public OAuth1Operations getOAuthOperations() {
			return null;
		}

		public TestTwitterApi getApi(String accessToken, String secret) {
			return null;
		}
		
	}
		
	interface TestTwitterApi {
		
	}
}
