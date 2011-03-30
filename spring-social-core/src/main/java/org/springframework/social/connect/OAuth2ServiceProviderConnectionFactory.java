package org.springframework.social.connect;

import org.springframework.social.connect.spi.ServiceApiAdapter;
import org.springframework.social.oauth2.AccessGrant;
import org.springframework.social.oauth2.OAuth2Operations;
import org.springframework.social.oauth2.OAuth2ServiceProvider;

public final class OAuth2ServiceProviderConnectionFactory<S> extends ServiceProviderConnectionFactory<S> {
	
	public OAuth2ServiceProviderConnectionFactory(String providerId, OAuth2ServiceProvider<S> serviceProvider) {
		this(providerId, serviceProvider, null);
	}
	
	public OAuth2ServiceProviderConnectionFactory(String providerId, OAuth2ServiceProvider<S> serviceProvider, ServiceApiAdapter<S> serviceApiAdapter) {
		super(providerId, serviceProvider, serviceApiAdapter);
	}

	public OAuth2Operations getOAuthOperations() {
		return getOAuth2ServiceProvider().getOAuthOperations();
	}
	
	public ServiceProviderConnection<S> createConnection(AccessGrant accessGrant) {
		return new ServiceProviderConnectionImpl<S>(getProviderId(), isAllowSignIn(),
				new ApiTokens(accessGrant.getAccessToken(), null, accessGrant.getRefreshToken()),
				getOAuth2ServiceProvider().getServiceApi(accessGrant.getAccessToken()),
				getServiceApiAdapter());		
	}
	
	public ServiceProviderConnection<S> createConnection(ServiceProviderConnectionMemento connectionMemento) {
		return new ServiceProviderConnectionImpl<S>(connectionMemento,
				getOAuth2ServiceProvider().getServiceApi(connectionMemento.getAccessToken()),
				getServiceApiAdapter());
	}
	
	// internal helpers
	
	private OAuth2ServiceProvider<S> getOAuth2ServiceProvider() {
		return (OAuth2ServiceProvider<S>) getServiceProvider();
	}
	
}