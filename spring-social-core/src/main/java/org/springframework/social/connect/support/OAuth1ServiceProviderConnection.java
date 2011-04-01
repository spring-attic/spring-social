package org.springframework.social.connect.support;

import org.springframework.social.connect.spi.ServiceApiAdapter;
import org.springframework.social.oauth1.OAuth1ServiceProvider;

public class OAuth1ServiceProviderConnection<S> extends AbstractServiceProviderConnection<S> {

	public OAuth1ServiceProviderConnection(String providerId, String providerUserId, OAuth1ServiceProvider<S> serviceProvider, String accessToken, String secret, ServiceApiAdapter<S> serviceApiAdapter, boolean allowSignIn) {
		super(providerId, providerUserId, serviceProvider.getServiceApi(accessToken, secret), serviceApiAdapter, allowSignIn);
	}

}
