package org.springframework.social.connect.support;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import org.junit.Before;
import org.junit.Test;
import org.springframework.social.ServiceProvider;
import org.springframework.social.oauth1.OAuth1Operations;
import org.springframework.social.oauth1.OAuth1ServiceProvider;
import org.springframework.social.oauth2.OAuth2Operations;
import org.springframework.social.oauth2.OAuth2ServiceProvider;

public class MapServiceProviderRegistryTest {

	private MapServiceProviderRegistry registry = new MapServiceProviderRegistry();
	
	private TwitterServiceProvider twitter =  new TwitterServiceProvider();
	
	private FacebookServiceProvider facebook = new FacebookServiceProvider();
	
	private PivotalTrackerServiceProvider pivotal = new PivotalTrackerServiceProvider();

	@Before
	public void registerSomeProviders() {
		registry.addServiceProvider("twitter", twitter);
		registry.addServiceProvider("facebook", facebook);
		registry.addServiceProvider("pivotal", pivotal);		
	}
	
	@Test
	public void getServiceProviderById() {
		assertSame(twitter, registry.getServiceProviderById("twitter"));
		assertSame(facebook, registry.getServiceProviderById("facebook"));
		assertSame(pivotal, registry.getServiceProviderById("pivotal"));		
	}
	
	@Test
	public void getServiceProviderByClass() {
		assertSame(twitter, registry.getServiceProviderByClass(TwitterServiceProvider.class));
		assertSame(facebook, registry.getServiceProviderByClass(FacebookServiceProvider.class));
		assertSame(pivotal, registry.getServiceProviderByClass(PivotalTrackerServiceProvider.class));		
	}
	
	@Test
	public void getServiceProviderByApi() {
		assertSame(twitter, registry.getServiceProviderByApi(TwitterApi.class));
		assertSame(facebook, registry.getServiceProviderByApi(FacebookApi.class));
		assertSame(pivotal, registry.getServiceProviderByApi(PivotalTrackerApi.class));		
	}
	
	@Test
	public void providerId() {
		assertEquals("twitter", registry.providerId(twitter));
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void registerDuplicateId() {
		registry.addServiceProvider("twitter", new ServiceProvider<TwitterApi>() { });
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void registerDuplicateClass() {
		registry.addServiceProvider("twitter2", twitter);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void registerDuplicateApi() {
		registry.addServiceProvider("twitter2", new TwitterServiceProvider2());
	}
	
	static class TwitterServiceProvider implements OAuth1ServiceProvider<TwitterApi> {

		public OAuth1Operations getOAuthOperations() {
			return null;
		}

		public TwitterApi getServiceApi(String accessToken, String secret) {
			return null;
		}
		
	}
	
	static class TwitterServiceProvider2 implements OAuth1ServiceProvider<TwitterApi> {

		public OAuth1Operations getOAuthOperations() {
			return null;
		}

		public TwitterApi getServiceApi(String accessToken, String secret) {
			return null;
		}
		
	}
	
	interface TwitterApi {
		
	}
	
	static class FacebookServiceProvider implements OAuth2ServiceProvider<FacebookApi> {

		public OAuth2Operations getOAuthOperations() {
			return null;
		}

		public FacebookApi getServiceApi(String accessToken) {
			return null;
		}
		
	}
	
	interface FacebookApi {
		
	}
	
	static class PivotalTrackerServiceProvider implements ServiceProvider<PivotalTrackerApi> {
		
	}
	
	interface PivotalTrackerApi {
		
	}
	
}
