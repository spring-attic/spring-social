/*
 * Copyright 2011 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.social.connect.signin.web;

import java.io.Serializable;

import javax.inject.Provider;

import org.springframework.social.connect.ServiceProviderConnection;
import org.springframework.social.connect.ServiceProviderConnectionData;
import org.springframework.social.connect.ServiceProviderConnectionFactoryLocator;
import org.springframework.social.connect.ServiceProviderConnectionRepository;

/**
 * Models an attempt to sign-in to the application using a provider user identity.
 * Instances are created when the provider sign-in process could not be completed because no local user is associated with the provider user.
 * This could happen because the user has not yet signed up with the application, or has not yet connected their local application identity with the their provider identity.
 * For the former scenario, callers should invoke {@link #addConnection()} post-signup to establish a connection between a new user account and the provider account.
 * For the latter, existing users should sign-in using their local application credentials and formally connect to the provider they also wish to authenticate with.
 * @author Keith Donald
 */
@SuppressWarnings("serial")
public class ProviderSignInAttempt implements Serializable {

	/**
	 * Name of the session attribute ProviderSignInAttempt instances are indexed under.
	 */
	static final String SESSION_ATTRIBUTE = ProviderSignInAttempt.class.getName();

	private final ServiceProviderConnectionData connectionData;
	
	private final Provider<ServiceProviderConnectionFactoryLocator> connectionFactoryLocatorProvider;
	
	private final Provider<ServiceProviderConnectionRepository> connectionRepositoryProvider;
		
	public ProviderSignInAttempt(ServiceProviderConnection<?> connection, Provider<ServiceProviderConnectionFactoryLocator> connectionFactoryLocatorProvider, Provider<ServiceProviderConnectionRepository> connectionRepositoryProvider) {
		this.connectionData = connection.createData();
		this.connectionFactoryLocatorProvider = connectionFactoryLocatorProvider;
		this.connectionRepositoryProvider = connectionRepositoryProvider;		
	}
	
	/**
	 * Get the connection to the provider user account the client attempted to sign-in as.
	 * Using this connection you may fetch a {@link ServiceProviderConnection#fetchUserProfile() provider user profile} and use that to pre-populate a local user registration/signup form.
	 * You can also lookup the id of the provider and use that to display a provider-specific user-sign-in-attempt flash message e.g. "Your Facebook Account is not connected to a Local account. Please sign up."
	 */
	public ServiceProviderConnection<?> getConnection() {
		return connectionFactoryLocatorProvider.get().getConnectionFactory(connectionData.getProviderId()).createConnection(connectionData);
	}
	
	/**
	 * Connect the new local user to the provider.
	 */
	void addConnection() {
		connectionRepositoryProvider.get().addConnection(getConnection());
	}

}
