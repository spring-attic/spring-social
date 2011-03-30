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
package org.springframework.social.connect;

import java.io.Serializable;

/**
 * A connection between a local user account and an external service provider account.
 * @author Keith Donald
 * @param <S> the service API
 */
public interface ServiceProviderConnection<S> {

	/**
	 * The locally assigned id of this connection.
	 * Unique across all connections.
	 * This should never change.
	 * Initially null if the connection has not yet been saved.
	 */
	public Long getId();
	
	/**
	 * The local account id representing this end of the connection.
	 * This should never change. 
	 * Initially null if the connection has not yet been saved.
	 */
	public Serializable getAccountId();

	/**
	 * The id of the provider as it is registered in the system.
	 * This should never change. 
	 * Initially null if the connection has not yet been saved.
	 */
	public String getProviderId();
	
	/**
	 * The id of the external provider account representing the remote end of the connection.
	 * May be null if this information is not exposed by the provider.
	 * If present, this value should never change.
	 * Must be present to support sign-in with the provider account.
	 */
	String getProviderAccountId();
	
	/**
	 * The display name for the user's profile on the provider's system.
	 * May be null if this information is not public or not provided.
	 * This information may change if the user updates his or her profile.
	 */
	String getProfileName();

	/**
	 * The public URL of the user's profile at the provider's site.
	 * May be null if this information is not public or not provided.
	 * This information may change if the user updates his or her profile.
	 */
	String getProfileUrl();

	/**
	 * A link to the user's profile picture at the provider's site.
	 * May be null if this information is not public or not provided.
	 * This information may change if the user updates his or her profile.
	 */
	String getProfilePictureUrl();
	
	/**
	 * If this connection can be used to sign the local user in.
	 * True if sign-in support was specified when the connection was established and nobody else is connected to the {@link #getProviderAccountId() providerAccount}.
	 */
	boolean allowSignIn();

	/**
	 * Test this connection.
	 * If false, indicates that service API calls will fail.
	 */
	boolean test();
	
	/**
	 * Update the user's status on the provider's system.
	 * Allows a message to be broadcast from the local account to the remote account.
	 * This method will be a no-op if a status concept is not supported by the service provider.
	 * @param message the status message
	 */
	void updateStatus(String message);
	
	/**
	 * Sync's this connection object with the current state of the linked provider account.
	 * Will cause locally cached profile fields to update if they have changed on the provider's system. 
	 */
	void sync();
	
	/**
	 * A Java binding to the Service Provider's native API.
	 */
	public S getServiceApi();

	// mutators

	/**
	 * Creates a copy of this connection with the local accountId property set to the value provided.
	 */
	public ServiceProviderConnection<S> assignAccountId(Serializable accountId);
	
	/**
	 * Creates a memento can be used to persist the state of this connection for restoration later.
	 */
	public ServiceProviderConnectionMemento createMemento();

	/**
	 * Creates a copy of this connection with the id property set to the value provided.
	 */
	public ServiceProviderConnection<S> assignId(Long id);
}