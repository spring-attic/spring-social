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

/**
 * A connection between a local user and an external service provider user.
 * @author Keith Donald
 * @param <S> the service API
 */
public interface ServiceProviderConnection<S> {

	/**
	 * The key identifying this ServiceProviderConnection.
	 */
	ServiceProviderConnectionKey getKey();
	
	/**
	 * Information about the user on the provider's system.
	 */
	ServiceProviderUser getUser();
	
	/**
	 * Test this connection.
	 * If false, indicates that service API calls will fail.
	 */
	boolean test();
	
	/**
	 * Returns true if this connection has expired.
	 * Call {@link #refresh()} to renew the connection.
	 */
	boolean hasExpired();

	/**
	 * Refresh this connection.
	 */
	void refresh();
	
	/**
	 * Update the user's status on the provider's system.
	 * Allows a message to be broadcast from the local account to the remote account.
	 * This method will be a no-op if a status concept is not supported by the service provider.
	 * @param message the status message
	 */
	void updateStatus(String message);
	
	/**
	 * Sync's this connection object with the current state of the linked provider user.
	 * Will cause locally cached profile fields to update if they have changed on the provider's system. 
	 */
	void sync();
	
	/**
	 * A Java binding to the Service Provider's native API.
	 */
	public S getServiceApi();

	/**
	 * Creates a memento that can be used to persist the state of this connection.
	 */
	ServiceProviderConnectionMemento createMemento();

}