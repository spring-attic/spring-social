package org.springframework.social.connect.support;

import org.springframework.social.connect.spi.ServiceApiAdapter;
import org.springframework.social.oauth2.OAuth2ServiceProvider;

public class OAuth2ServiceProviderConnection<S> extends AbstractServiceProviderConnection<S> {

	public OAuth2ServiceProviderConnection(String providerId, String providerUserId, OAuth2ServiceProvider<S> serviceProvider,
			String accessToken, String refreshToken, Long expiresTime, ServiceApiAdapter<S> serviceApiAdapter, boolean allowSignIn) {
		super(providerId, providerUserId, serviceProvider.getServiceApi(accessToken), serviceApiAdapter, allowSignIn);
	}

}
