/*
 * Copyright 2015 the original author or authors.
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
 * A link to a service provider user.
 * Allows the client application to access or update user information using the provider's API.
 * Exposes a set of operations that are common across all service providers, including
 * the ability to {@link #fetchUserProfile() access user profile information} and {@link #updateStatus(String) update user status}. 
 * @author Keith Donald
 * @param <A> a strongly-typed binding to the service provider's API
 */
public interface Connection<A> extends Serializable {

	/**
	 * The key identifying this connection.
	 * @return A composite key that consists of the "providerId" plus "providerUserId"; for example, "facebook" and "125660". 
	 */
	ConnectionKey getKey();
	
	/**
	 * A display name or label for this connection.
	 * Should be suitable for display on a UI and distinguish this connection from others with the same provider.
	 * Generally the full name or screen name of the connected provider user e.g. "Keith Donald" or "@kdonald".
	 * May be null if this information is not public or not provided.
	 * The value of this property may change if the user updates his or her profile.
	 * @return the displayable name for the connection
	 * @see #sync()
	 */
	String getDisplayName();

	/**
	 * The public URL of the connected user's profile at the provider's site.
	 * A client application may use this value along with the displayName to generate a link to the user's profile on the provider's system.
	 * May be null if this information is not public or not provided.
	 * The value of this property may change if the user updates his or her profile.
	 * @return the public URL for the connected user
	 * @see #sync()
	 */
	String getProfileUrl();

	/**
	 * A link to a image that visualizes this connection.
	 * Should visually distinguish this connection from others with the same provider.
	 * Generally the small/thumbnail version of the connected provider user's profile picture.
	 * May be null if this information is not public or not provided.
	 * The value of this property may change if the user updates his or her profile.
	 * @return a String containing the URL to the connection image
	 * @see #sync()
	 */
	String getImageUrl();

	/**
	 * Sync's this connection object with the current state of the external user's profile.
	 * Triggers locally cached profile fields to update if they have changed on the provider's system. 
	 */
	void sync();
	
	/**
	 * Test this connection.
	 * If false, indicates calls to the {@link #getApi() api} will fail.
	 * Used to proactively test authorization credentials such as an API access token before invoking the service API.
	 * @return true if the connection is valid
	 */
	boolean test();
	
	/**
	 * Returns true if this connection has expired.
	 * An expired connection cannot be used; calls to {@link #test()} return false, and any service API invocations fail.
	 * If expired, you may call {@link #refresh()} to renew the connection.
	 * Not supported by all Connection implementations; always returns false if not supported.
	 * @return true if the connection has expired
	 */
	boolean hasExpired();

	/**
	 * Refresh this connection.
	 * Used to renew an expired connection.
	 * If the refresh operation is successful, {@link #hasExpired()} returns false.
	 * Not supported by all connection implementations; if not supported, this method is a no-op.
	 */
	void refresh();
	
	/**
	 * Fetch a normalized model of the user's profile on the provider system.
	 * Capable of exposing the user's name, email, and username.
	 * What is actually exposed depends on the provider and scope of this connection.
	 * @return a normalized user profile associated with this connection.
	 */
	UserProfile fetchUserProfile();
	
	/**
	 * Update the user's status on the provider's system.
	 * This method is a no-op if a status concept is not supported by the service provider.
	 * @param message the status message
	 */
	void updateStatus(String message);
	
	/**
	 * A Java binding to the service provider's native API.
	 * @return the provider-specific API binding
	 */
	A getApi();

	/**
	 * Creates a data transfer object that can be used to persist the state of this connection.
	 * Used to support the transfer of connection state between layers of the application, such as to the database layer.
	 * @return a data transfer object containing details about the connection.
	 */
	ConnectionData createData();

}
