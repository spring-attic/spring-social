package org.springframework.social.provider.jdbc;

import java.io.Serializable;

import org.springframework.social.provider.OAuthToken;
import org.springframework.social.provider.support.AbstractOAuth1ServiceProvider;
import org.springframework.social.provider.support.ConnectionRepository;
import org.springframework.social.provider.support.ServiceProviderParameters;

public class TestServiceProvider extends AbstractOAuth1ServiceProvider<TestOperations> {

	public TestServiceProvider(ServiceProviderParameters parameters, ConnectionRepository connectionRepository) {
		super(parameters, connectionRepository);
	}

	public Serializable getProviderUserProfile(AccessToken accessToken) {
		return null;
	}

	@Override
	protected TestOperations createServiceOperations(AccessToken accessToken) {
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
