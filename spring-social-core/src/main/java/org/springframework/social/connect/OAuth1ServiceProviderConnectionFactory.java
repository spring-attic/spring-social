package org.springframework.social.connect;

import org.springframework.social.connect.spi.ServiceApiAdapter;
import org.springframework.social.oauth1.OAuth1Operations;
import org.springframework.social.oauth1.OAuth1ServiceProvider;
import org.springframework.social.oauth1.OAuthToken;

public class OAuth1ServiceProviderConnectionFactory<S> extends ServiceProviderConnectionFactory<S> {
	
	public OAuth1ServiceProviderConnectionFactory(String providerId, OAuth1ServiceProvider<S> serviceProvider) {
		this(providerId, serviceProvider, null);
	}
	
	public OAuth1ServiceProviderConnectionFactory(String providerId, OAuth1ServiceProvider<S> serviceProvider, ServiceApiAdapter<S> serviceApiAdapter) {
		super(providerId, serviceProvider, serviceApiAdapter);
	}

	public OAuth1Operations getOAuthOperations() {
		return getOAuth1ServiceProvider().getOAuthOperations();
	}
	
	public ServiceProviderConnection<S> createConnection(OAuthToken accessToken) {
		return new ServiceProviderConnectionImpl<S>(getProviderId(), isAllowSignIn(),
				new ApiTokens(accessToken.getValue(), accessToken.getSecret(), null),
				getOAuth1ServiceProvider().getServiceApi(accessToken.getValue(), accessToken.getSecret()),
				getServiceApiAdapter());		
	}
	
	public ServiceProviderConnection<S> createConnection(ServiceProviderConnectionMemento connectionMemento) {
		return new ServiceProviderConnectionImpl<S>(connectionMemento,
				getOAuth1ServiceProvider().getServiceApi(connectionMemento.getAccessToken(), connectionMemento.getSecret()),
				getServiceApiAdapter());
	}
	
	// internal helpers
	
	private OAuth1ServiceProvider<S> getOAuth1ServiceProvider() {
		return (OAuth1ServiceProvider<S>) getServiceProvider();
	}
	
}