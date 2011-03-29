package org.springframework.social.connect.support;

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
		assertSame(twitter, registry.getServiceProvider("twitter"));
		assertSame(facebook, registry.getServiceProvider("facebook"));
		assertSame(pivotal, registry.getServiceProvider("pivotal"));		
	}
	
	@Test
	public void getServiceProviderByClass() {
		assertSame(twitter, registry.getServiceProvider(TwitterServiceProvider.class));
		assertSame(facebook, registry.getServiceProvider(FacebookServiceProvider.class));
		assertSame(pivotal, registry.getServiceProvider(PivotalTrackerServiceProvider.class));		
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void registerDuplicateId() {
		registry.addServiceProvider("twitter", new ServiceProvider<TwitterApi>() { });
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void registerDuplicateClass() {
		registry.addServiceProvider("twitter2", twitter);
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
