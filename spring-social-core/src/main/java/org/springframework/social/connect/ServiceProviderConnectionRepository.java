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

import java.util.List;

import org.springframework.util.MultiValueMap;

/**
 * Data access interface for saving and restoring ServiceProviderConnection objects from a persistent store.
 * The view is relative to a specific local user--it's not possible using this interface to access or update connections for multiple local users.
 * If you need that capability, see {@link MultiUserServiceProviderConnectionRepository}.
 * @author Keith Donald
 * @see MultiUserServiceProviderConnectionRepository
 */
public interface ServiceProviderConnectionRepository {

	/**
	 * Get a map of all connections for the current user.
	 * The returned map contains an entry for each provider the user is connected to.
	 * The key for each entry is the providerId, and the value is the list of ServiceProviderConnections that exist between the user and that provider.
	 * For example, if the user is connected once to Facebook and twice to Twitter, the returned map would contain two entries with the following structure:
	 * <pre>
	 * { 
	 *     "facebook" -&gt; ServiceProviderConnection("Keith Donald") ,
	 *     "twitter"  -&gt; ServiceProviderConnection("kdonald"), ServiceProviderConnection("springsource")
	 * }
	 * </pre>
	 * The returned map is sorted by providerId and entry values are ordered by rank.
	 * Returns an empty map if the user has no connections.
	 */
	MultiValueMap<String, ServiceProviderConnection<?>> findConnections();

	/**
	 * Get the connections the current user has to the provider registered by the given id.
	 * The returned list is ordered by connection rank.
	 * Returns an empty list if the user has no connections to the provider.
	 * @param providerId the provider id e.g. "facebook"
	 */
	List<ServiceProviderConnection<?>> findConnectionsToProvider(String providerId);

	/**
	 * Get the connections the current user has to the given provider users.
	 * The providerUsers parameter accepts a map containing an entry for each provider the caller is interested in.
	 * The key for each entry is the providerId e.g. "facebook", and the value is a list of provider user ids to fetch connections to e.g. ("126500", "34521", "127243").
	 * The returned map has the same structure and order, except the provider userId values have been replaced by ServiceProviderConnection instances.
	 * If no connection exists between the current user and a given provider user, a null value is returned for that position.
	 * @param providerUserIds the provider users map
	 * @return the provider user connection map 
	 */
	MultiValueMap<String, ServiceProviderConnection<?>> findConnectionsForUsers(MultiValueMap<String, String> providerUserIds);
	
	/**
	 * Find a single ServiceProviderConnection for the current user by its key, which consists of the providerId + providerUserId.
	 * @param connectionKey the service provider connection key
	 * @return the service provider connection
	 * @throws NoSuchServiceProviderConnectionException if no such connection exists for the current user
	 */
	ServiceProviderConnection<?> findConnection(ServiceProviderConnectionKey connectionKey);

	/**
	 * Find a single ServiceProviderConnection for the current user by its serviceApiType e.g. FacebookApi.class.
	 * If the user has multiple connections to the provider associated with the given serviceApiType, this method returns the one with the top rank (or priority).
	 * Useful for direct use by application code to obtain a parameterized ServiceProviderConnection instance.
	 * @param <S> the service api parameterized type
	 * @param serviceApiType the service api type e.g. FacebookApi.class or TwitterApi.class
	 * @return the service provider connection
	 */
	<S> ServiceProviderConnection<S> findConnectionByServiceApi(Class<S> serviceApiType);

	/**
	 * Find the ServiceProviderConnections for the current user by the given serviceApiType e.g. FacebookApi.class.
	 * Returns the equivalent of {@link #findConnectionsToProvider(String)}, but uses the serviceApiType as the provider key instead of the providerId.
	 * Useful for direct use by application code to obtain parameterized ServiceProviderConnection instances e.g. <code>List&lt;ServiceProviderConnection&lt;FacebookApi&gt;&gt;</code>.
	 * @param <S> the service api parameterized type
	 * @param serviceApiType the service api type e.g. FacebookApi.class or TwitterApi.class
	 * @return the service provider connection
	 */
	<S> List<ServiceProviderConnection<S>> findConnectionsByServiceApi(Class<S> serviceApiType);

	/**
	 * Find the ServiceProviderConnection between the current user and the given provider user.
	 * Returns the equivalent of {@link #findConnectionToProvider(ServiceProviderConnectionKey)}, but uses the serviceApiType as the provider key instead of the providerId.
	 * Useful for direct use by application code to obtain a parameterized ServiceProviderConnection instance.
	 * @param <S> the service api parameterized type
	 * @param serviceApiType the service api type e.g. FacebookApi.class or TwitterApi.class
	 * @param providerUserId the provider user e.g. "126500".
	 * @return the service provider connection
	 */
	<S> ServiceProviderConnection<S> findConnectionByServiceApiForUser(Class<S> serviceApiType, String providerUserId);

	/**
	 * Add a new connection for the current user to this repository.
	 * After the connection is added, it can be retrieved later using one of the finders defined by this interface.
	 * @param connection the service provider connection
	 * @throws DuplicateServiceProviderConnectionException if the user already has this connection
	 */
	void addConnection(ServiceProviderConnection<?> connection);

	/**
	 * Update a connection already added to this repository.
	 * Merges the field values of the given connection object with the values stored in the repository.
	 * @param connection the service provider connection
	 */
	void updateConnection(ServiceProviderConnection<?> connection);
	
	/**
	 * Remove all connections between the current user and the provider from this repository.
	 * Does nothing if no provider connections exist.
	 * @param providerId the provider id e.g. "facebook"
	 */
	void removeConnectionsToProvider(String providerId);

	/**
	 * Remove a single connection for the current user from this repository.
	 * Does nothing if no such connection exists.
	 */
	void removeConnection(ServiceProviderConnectionKey connectionKey);
	
}