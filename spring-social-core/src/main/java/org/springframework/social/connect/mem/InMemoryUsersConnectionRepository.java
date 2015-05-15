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
package org.springframework.social.connect.mem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionFactoryLocator;
import org.springframework.social.connect.ConnectionRepository;
import org.springframework.social.connect.ConnectionSignUp;
import org.springframework.social.connect.NoSuchConnectionException;
import org.springframework.social.connect.UsersConnectionRepository;

/**
 * {@link UsersConnectionRepository} that stores connections in memory.
 * Intended for testing and small-scale applications as a convenient alternative to JdbcUsersConnectionRepository and an in-memory H2 database.
 * Not intended for production use, as it will be cleared out when the application exits.
 * @author Craig Walls
 */
public class InMemoryUsersConnectionRepository implements UsersConnectionRepository {

	private ConnectionFactoryLocator connectionFactoryLocator;
	
	private Map<String, ConnectionRepository> connectionRepositories;

	private ConnectionSignUp connectionSignUp;

	public InMemoryUsersConnectionRepository(ConnectionFactoryLocator connectionFactoryLocator) {
		this.connectionFactoryLocator = connectionFactoryLocator;
		this.connectionRepositories= new HashMap<String, ConnectionRepository>(); 
	}
	
	/**
	 * The command to execute to create a new local user profile in the event no user id could be mapped to a connection.
	 * Allows for implicitly creating a user profile from connection data during a provider sign-in attempt.
	 * Defaults to null, indicating explicit sign-up will be required to complete the provider sign-in attempt.
	 * @param connectionSignUp an implementation of ConnectionSignUp for implicit sign-up
	 * @see #findUserIdsWithConnection(Connection)
	 */
	public void setConnectionSignUp(ConnectionSignUp connectionSignUp) {
		this.connectionSignUp = connectionSignUp;
	}
	
	public List<String> findUserIdsWithConnection(Connection<?> connection) {
		List<String> localUserIds = new ArrayList<String>();
		Set<Entry<String, ConnectionRepository>> connectionRepositoryEntries = connectionRepositories.entrySet();
		for (Entry<String, ConnectionRepository> connectionRepositoryEntry : connectionRepositoryEntries) {
			try {
				connectionRepositoryEntry.getValue().getConnection(connection.getKey());
				localUserIds.add(connectionRepositoryEntry.getKey());
			} catch (NoSuchConnectionException e) {}
		}
		if (localUserIds.size() == 0 && connectionSignUp != null) {
			String newUserId = connectionSignUp.execute(connection);
			if (newUserId != null)
			{
				createConnectionRepository(newUserId).addConnection(connection);
				return Arrays.asList(newUserId);
			}
		}
		return localUserIds;
	}

	public Set<String> findUserIdsConnectedTo(String providerId, Set<String> providerUserIds) {
		List<String> localUserIds = new ArrayList<String>();
		Set<Entry<String, ConnectionRepository>> connectionRepositoryEntries = connectionRepositories.entrySet();
		for (Entry<String, ConnectionRepository> connectionRepositoryEntry : connectionRepositoryEntries) {
			String localUserId = connectionRepositoryEntry.getKey();
			List<Connection<?>> providerConnections = connectionRepositoryEntry.getValue().findConnections(providerId);
			for (Connection<?> connection : providerConnections) {
				if (providerUserIds.contains(connection.getKey().getProviderUserId())) {
					localUserIds.add(localUserId);
				}
			}
		}
		return new HashSet<String>(localUserIds);
	}

	public ConnectionRepository createConnectionRepository(String userId) {
		if (!connectionRepositories.containsKey(userId)) {
			connectionRepositories.put(userId, new InMemoryConnectionRepository(connectionFactoryLocator));
		}
		return connectionRepositories.get(userId);
	}

}
