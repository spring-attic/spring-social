package org.springframework.social.provider.jdbc;

import java.io.Serializable;

import org.springframework.social.provider.AbstractOAuth1ServiceProvider;
import org.springframework.social.provider.AccountConnectionRepository;
import org.springframework.social.provider.OAuthToken;
import org.springframework.social.provider.ServiceProviderParameters;

public class TestServiceProvider extends AbstractOAuth1ServiceProvider<TestOperations> {

	public TestServiceProvider(ServiceProviderParameters parameters, AccountConnectionRepository connectionRepository) {
		super(parameters, connectionRepository);
	}

	public Serializable getProviderUserProfile(OAuthToken accessToken) {
		return null;
	}

	@Override
	protected TestOperations createServiceOperations(OAuthToken accessToken) {
		return null;
	}

	@Override
	protected String fetchProviderAccountId(TestOperations serviceOperations) {
		return null;
	}

	@Override
	protected String buildProviderProfileUrl(String providerAccountId, TestOperations serviceOperations) {
		return null;
	}

}
