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

import java.util.List;
import java.util.Set;

/**
 * A data access interface for managing a global store of users connections to service providers.
 * Provides data access operations that apply across multiple user records.
 * Also acts as a factory for a user-specific {@link ConnectionRepository}.
 * @author Keith Donald
 * @see ConnectionRepository
 */
public interface UsersConnectionRepository {

	/**
	 * Find the ids for local application users that have the given {@link Connection}.
	 * Used to support the ProviderSignIn scenario where the user id returned is used to sign a local application user in using his or her provider account.
	 * No entries indicates no application users are associated with the connection; ProviderSignInController will offer the user a signup page to register with the app.
	 * A single entry indicates that exactly one application user is associated with the connection and is used to sign in that user via ProviderSignInController.
	 * Multiple entries indicate that multiple application users are associated with the connection and handled as an error by ProviderSignInController.
	 * @param connection the service provider connection resulting from the provider sign-in attempt
	 * @return the user ids associated with the connection. 
	 */
	List<String> findUserIdsWithConnection(Connection<?> connection);

	/**
	 * Find the ids of the users who are connected to the specific provider user accounts.
	 * @param providerId the provider id, e.g. "facebook"
	 * @param providerUserIds the set of provider user ids e.g. ("125600", "131345", "54321").
	 * @return the set of user ids connected to those service provider users, or empty if none.
	 */
	Set<String> findUserIdsConnectedTo(String providerId, Set<String> providerUserIds);
	
	/**
	 * Create a single-user {@link ConnectionRepository} instance for the user assigned the given id.
	 * All operations on the returned repository instance are relative to the user.
	 * @param userId the id of the local user account.
	 * @return the ConnectionRepository, exposing a number of operations for accessing and updating the given user's provider connections.
	 */
	ConnectionRepository createConnectionRepository(String userId);
	
}
