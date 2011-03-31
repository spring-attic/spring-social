package org.springframework.social.twitter;

import org.springframework.social.connect.OAuth1ServiceProviderConnectionFactory;
import org.springframework.social.connect.spi.ProviderProfile;
import org.springframework.social.connect.spi.ServiceApiAdapter;

public class TwitterServiceProviderConnectionFactory extends OAuth1ServiceProviderConnectionFactory<TwitterApi> {

	public TwitterServiceProviderConnectionFactory(String consumerKey, String consumerSecret) {
		super("twitter", new TwitterServiceProvider(consumerKey, consumerSecret), new TwitterServiceApiAdapter(), true);
	}
	
	private static class TwitterServiceApiAdapter implements ServiceApiAdapter<TwitterApi> {

		public boolean test(TwitterApi serviceApi) {
			// TODO call whatever api method should be used for testing
			return true;
		}

		public ProviderProfile getProfile(TwitterApi serviceApi) {
			TwitterProfile profile = serviceApi.getUserProfile();
			return new ProviderProfile(Long.toString(profile.getId()), profile.getScreenName(), profile.getProfileUrl(), profile.getProfileImageUrl());
		}

		public void updateStatus(TwitterApi serviceApi, String message) {
			serviceApi.updateStatus(message);	
		}

	}
	
}
