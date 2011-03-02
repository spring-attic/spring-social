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
package org.springframework.social.connect.support;

import java.io.Serializable;
import java.util.List;

/**
 * Strategy for storing account connection information.
 * Delegated to by {@link AbstractServiceProvider} to decouple the provider implementation from any physical connection store.
 * @author Keith Donald
 */
public interface ConnectionRepository {

	/**
	 * True if a connection exists between the account and the provider, false otherwise.
	 * @param accountId the user's account identifier
	 * @param providerId the provider's identifier
	 */
	boolean isConnected(Serializable accountId, String providerId);

	/**
	 * Finds the connections between the account and the provider.
	 * @param accountId the user's account identifier
	 * @param providerId the provider's identifier
	 */
	List<Connection> findConnections(Serializable accountId, String providerId);

	/**
	 * Returns the id of the account connected to the provider by the access token.
	 * Designed to support sign-in by connection use cases.
	 * @param providerId the provider's identifier
	 * @param accessToken the access token
	 */
	Serializable findAccountIdByConnectionAccessToken(String providerId, String accessToken);

	/**
	 * Saves a connection.
	 * @param accountId the user's account identifier
	 * @param providerId the provider's identifier
	 * @param connection the connection to save
	 * @return the saved connection instance, guaranteed to have its persistent {@link Connection#getId() id} populated.
	 */
	Connection saveConnection(Serializable accountId, String providerId, Connection connection);

	/**
	 * Removes a connection.
	 * @param accountId the user's account identifier
	 * @param providerId the provider's identifier
	 * @param connectionId the internal id of the connection to remove
	 */
	void removeConnection(Serializable accountId, String providerId, Long connectionId);

}