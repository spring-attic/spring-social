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
 * A link to a service provider user.
 * Allows the client application to access or update user information using the provider's API.
 * Exposes a set of operations that are common across all service providers, including
 * the ability to {@link #getUser() access user profile information} and {@link #updateStatus(String) update user status}. 
 * @author Keith Donald
 * @param <S> a strongly-typed binding to the service provider's API
 */
public interface ServiceProviderConnection<S> {

	/**
	 * The key that identifies this ServiceProviderConnection.
	 * A composite key that consists of the "providerId" plus "providerUserId"; for example, "facebook" and "125660". 
	 */
	ServiceProviderConnectionKey getKey();
	
	/**
	 * Information about the user on the provider's system.
	 * Exposes the user's id, profileName, profileUrl, pictureUrl, among other common properties.
	 */
	ServiceProviderUser getUser();
	
	/**
	 * Test this connection.
	 * If false, indicates calls to the {@link #getServiceApi() serviceApi} will fail.
	 * Used to support proactively test authorization credentials such as an API access token before invoking the service API.
	 */
	boolean test();
	
	/**
	 * Returns true if this connection has expired.
	 * An expired connection cannot be used; calls to {@link #test()} return false, and any service API invocations fail.
	 * If expired, you may call {@link #refresh()} to renew the connection.
	 * Not supported by all ServiceProviderConnection implementations; if not supported, will always return false.
	 */
	boolean hasExpired();

	/**
	 * Refresh this connection.
	 * Used to renew an expired connection.
	 * If the refresh operation is successful, {@link #hasExpired()} returns false.
	 * Not supported by all ServiceProviderConnection implementations; if not supported, this method is a no-op.
	 */
	void refresh();
	
	/**
	 * Update the user's status on the provider's system.
	 * This method is a no-op if a status concept is not supported by the service provider.
	 * @param message the status message
	 */
	void updateStatus(String message);
	
	/**
	 * Sync's this connection object with the current state of the external user's profile.
	 * Triggers locally cached profile fields to update if they have changed on the provider's system. 
	 */
	void sync();
	
	/**
	 * A Java binding to the Service Provider's native API.
	 */
	public S getServiceApi();

	/**
	 * Creates a data transfer object that can be used to persist the state of this connection.
	 * Used to support the transfer of connection state between layers of the application, such as to the database layer.
	 */
	ServiceProviderConnectionData createData();

}