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
package org.springframework.social.provider;

import java.io.Serializable;
import java.util.List;

/**
 * Models the provider of a service that local user accounts may connect to and invoke.
 * Exposes service provider metadata along with connection management operations that allow for account connections to be established.
 * Also acts as a factory for a strongly-typed service API (S).
 * Once a connection with this provider is established, the service API can be used by the application to invoke the service on behalf of the member.
 * @author Keith Donald
 * @param <S> The service hosted by this service provider.
 */
public interface ServiceProvider<S> {

	/**
	 * A label suitable for display in a UI, typically used to inform the user which service providers he or she has connected with / may connect with. e.g. Twitter.
	 */
	String getDisplayName();

	/**
	 * The authorization protocol.
	 */
	AuthorizationProtocol getAuthorizationProtocol();

	/**
	 * Returns true if the user account has one or more connections to this provider, false otherwise.
	 * @param accountId the application account ID to check for a connection with this provider.
	 */
	boolean isConnected(Serializable accountId);

	/**
	 * Get the connections established between a user account and this service provider.
	 * The connections are ordered by rank.
	 * The first connection in the list is the "primary" connection between the account and this service provider.
	 * TODO return semantics when no connections are established -- throw exception? return empty list? return null?
	 * @param accountId a user account id
	 * @return the account's service provider connections
	 */
	List<ServiceProviderConnection<S>> getConnections(Serializable accountId);
	
}