package org.springframework.social.provider.support;

import org.springframework.social.provider.ServiceProviderConnection;

public class ServiceProviderConnectionImpl<S> implements ServiceProviderConnection<S> {

	private final String id;
	
	private final S api;

	private final String accountId;
	
	private final String providerId;

	private final ConnectionRepository connectionRepository;
	
	public ServiceProviderConnectionImpl(String id, S api, String accountId, String providerId, ConnectionRepository connectionRepository) {
		this.id = id;
		this.api = api;
		this.connectionRepository = connectionRepository;
		this.accountId = accountId;
		this.providerId = providerId;
	}
	
	public String getId() {
		return id;
	}

	public S getApi() {
		return api;
	}

	public void disconnect() {
		connectionRepository.disconnect(accountId, providerId, id);
	}

}
