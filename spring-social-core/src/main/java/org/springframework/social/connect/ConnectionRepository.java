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
 * Data access interface for saving and restoring Connection objects from a persistent store.
 * The view is relative to a specific local user--it's not possible using this interface to access or update connections for multiple local users.
 * If you need that capability, see {@link UsersConnectionRepository}.
 * @author Keith Donald
 * @see UsersConnectionRepository
 */
public interface ConnectionRepository {

	/**
	 * Find a map of all Connections for the current user.
	 * The returned map contains an entry for each provider the user is connected to.
	 * The key for each entry is the providerId, and the value is the list of {@link Connection}s that exist between the user and that provider.
	 * For example, if the user is connected once to Facebook and twice to Twitter, the returned map would contain two entries with the following structure:
	 * <pre>
	 * { 
	 *     "facebook" -&gt; Connection("Keith Donald") ,
	 *     "twitter"  -&gt; Connection("kdonald"), Connection("springsource")
	 * }
	 * </pre>
	 * The returned map is sorted by providerId and entry values are ordered by rank.
	 * Returns an empty map if the user has no connections.
	 */
	MultiValueMap<String, Connection<?>> findConnections();

	/**
	 * Find the Connections the current user has to the provider registered by the given id.
	 * The returned list is ordered by connection rank.
	 * Returns an empty list if the user has no connections to the provider.
	 * @param providerId the provider id e.g. "facebook"
	 */
	List<Connection<?>> findConnectionsToProvider(String providerId);

	/**
	 * Find the Connections the current user has to the given provider users.
	 * The providerUsers parameter accepts a map containing an entry for each provider the caller is interested in.
	 * The key for each entry is the providerId e.g. "facebook", and the value is a list of provider user ids to fetch connections to e.g. ("126500", "34521", "127243").
	 * The returned map has the same structure and order, except the provider userId values have been replaced by Connection instances.
	 * If no connection exists between the current user and a given provider user, a null value is returned for that position.
	 * @param providerUserIds the provider users map
	 * @return the provider user connection map 
	 */
	MultiValueMap<String, Connection<?>> findConnectionsForUsers(MultiValueMap<String, String> providerUserIds);
	
	/**
	 * Find a single Connection for the current user by its key, which consists of the providerId + providerUserId.
	 * @param connectionKey the service provider connection key
	 * @return the service provider connection
	 * @throws NoSuchConnectionException if no such connection exists for the current user
	 */
	Connection<?> findConnection(ConnectionKey connectionKey);

	/**
	 * Find a single Connection for the current user by its apiType e.g. FacebookApi.class.
	 * If the user has multiple connections to the provider associated with the given apiType, this method returns the one with the top rank (or priority).
	 * Useful for direct use by application code to obtain a parameterized Connection instance.
	 * @param <A> the service api parameterized type
	 * @param apiType the service api type e.g. FacebookApi.class or TwitterApi.class
	 * @return the connection
	 */
	<A> Connection<A> findPrimaryConnectionToApi(Class<A> apiType);

	/**
	 * Find the Connection between the current user and the given provider user.
	 * Returns the equivalent of {@link #findConnection(ConnectionKey)}, but uses the apiType as the provider key instead of the providerId.
	 * Useful for direct use by application code to obtain a parameterized Connection instance.
	 * @param <A> the api parameterized type
	 * @param apiType the service api type e.g. FacebookApi.class or TwitterApi.class
	 * @param providerUserId the provider user e.g. "126500".
	 * @return the connection
	 */
	<A> Connection<A> findConnectionToApiForUser(Class<A> apiType, String providerUserId);

	/**
	 * Find the Connections for the current user by the given apiType e.g. FacebookApi.class.
	 * Returns the equivalent of {@link #findConnectionsToProvider(String)}, but uses the apiType as the provider key instead of the providerId.
	 * Useful for direct use by application code to obtain parameterized Connection instances e.g. <code>List&lt;Connection&lt;FacebookApi&gt;&gt;</code>.
	 * @param <A> the api parameterized type
	 * @param apiType the service api type e.g. FacebookApi.class or TwitterApi.class
	 * @return the connections
	 */
	<A> List<Connection<A>> findConnectionsToApi(Class<A> apiType);
	
	/**
	 * Add a new Connection for the current user to this repository.
	 * After the connection is added, it can be retrieved later using one of the finders defined by this interface.
	 * @param connection connection
	 * @throws DuplicateConnectionException if the user already has this connection
	 */
	void addConnection(Connection<?> connection);

	/**
	 * Update a Connection already added to this repository.
	 * Merges the field values of the given connection object with the values stored in the repository.
	 * @param connection the connection
	 */
	void updateConnection(Connection<?> connection);
	
	/**
	 * Remove all Connections between the current user and the provider from this repository.
	 * Does nothing if no provider connections exist.
	 * @param providerId the provider id e.g. "facebook"
	 */
	void removeConnectionsToProvider(String providerId);

	/**
	 * Remove a single Connection for the current user from this repository.
	 * Does nothing if no such connection exists.
	 * @param connectionKey the connection key
	 */
	void removeConnection(ConnectionKey connectionKey);
	
}