package org.springframework.social.gowalla.provider;

import java.io.Serializable;

import org.springframework.social.gowalla.GowallaOperations;
import org.springframework.social.gowalla.GowallaTemplate;
import org.springframework.social.provider.OAuthToken;
import org.springframework.social.provider.support.AbstractOAuth2ServiceProvider;
import org.springframework.social.provider.support.AccountConnectionRepository;
import org.springframework.social.provider.support.ServiceProviderParameters;

public class GowallaServiceProvider extends AbstractOAuth2ServiceProvider<GowallaOperations> {

	public GowallaServiceProvider(ServiceProviderParameters parameters, AccountConnectionRepository connectionRepository) {
		super(parameters, connectionRepository);
	}

	@Override
	protected GowallaOperations createServiceOperations(OAuthToken accessToken) {
		if (accessToken == null) {
			throw new IllegalStateException("Cannot access Gowalla without an access token");
		}
		return new GowallaTemplate(accessToken.getValue());
	}

	@Override
	protected String fetchProviderAccountId(GowallaOperations gowalla) {
		return gowalla.getProfileId();
	}

	public Serializable getProviderUserProfile(OAuthToken accessToken) {
		return null;
	}

	@Override
	protected String buildProviderProfileUrl(String providerAccountId, GowallaOperations gowalla) {
		return "http://www.gowalla.com/users/" + providerAccountId;
	}

}
