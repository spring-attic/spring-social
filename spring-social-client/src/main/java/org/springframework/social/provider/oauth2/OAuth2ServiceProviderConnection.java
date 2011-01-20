package org.springframework.social.provider.oauth2;

import org.springframework.social.provider.ServiceProviderConnection;
import org.springframework.social.provider.support.AccountConnectionRepository;

public class OAuth2ServiceProviderConnection<S> implements ServiceProviderConnection<S> {

	private final AccountConnectionRepository connectionRepository;
	private final String accountId;
	private final String provider;
	private final S api;

	public OAuth2ServiceProviderConnection(AccountConnectionRepository connectionRepository, String accountId,
			String provider, S api) {
		this.connectionRepository = connectionRepository;
		this.accountId = accountId;
		this.provider = provider;
		this.api = api;
	}
	
	public String getId() {
		// TODO Auto-generated method stub
		return null;
	}

	public S getApi() {
		return api;
	}

	public void disconnect() {
		connectionRepository.disconnect(accountId, provider);
	}

}
