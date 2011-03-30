package org.springframework.social.connect;

import org.springframework.social.ServiceProvider;
import org.springframework.social.connect.spi.ServiceApiAdapter;

public abstract class ServiceProviderConnectionFactory<S> {

	private String providerId;
	
	private ServiceProvider<S> serviceProvider;

	private ServiceApiAdapter<S> serviceApiAdapter;
	
	private boolean allowSignIn = true;
	
	public ServiceProviderConnectionFactory(String providerId, ServiceProvider<S> serviceProvider) {
		this(providerId, serviceProvider, null);
	}
	
	public ServiceProviderConnectionFactory(String providerId, ServiceProvider<S> serviceProvider, ServiceApiAdapter<S> serviceApiAdapter) {
		this.providerId = providerId;
		this.serviceProvider = serviceProvider;
		this.serviceApiAdapter = serviceApiAdapter != null ? serviceApiAdapter : serviceApiAdapter(serviceProvider);
	}

	protected String getProviderId() {
		return providerId;
	}

	protected ServiceProvider<S> getServiceProvider() {
		return serviceProvider;
	}

	protected ServiceApiAdapter<S> getServiceApiAdapter() {
		return serviceApiAdapter;
	}

	protected boolean isAllowSignIn() {
		return allowSignIn;
	}

	public abstract ServiceProviderConnection<S> createConnection(ServiceProviderConnectionMemento connectionMemento);

	// internal helpers
	
	@SuppressWarnings("unchecked")
	private ServiceApiAdapter<S> serviceApiAdapter(ServiceProvider<S> serviceProvider) {
		return (ServiceApiAdapter<S>) (serviceProvider instanceof ServiceApiAdapter ? serviceProvider : NullServiceApiAdapter.INSTANCE);
	}
	
}
