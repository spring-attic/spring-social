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
package org.springframework.social.provider.test;

import java.io.Serializable;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.social.provider.support.Connection;
import org.springframework.social.provider.support.ConnectionRepository;

/**
 * Stub in-memory ConnectionRepository implementation useful for ServiceProvider unit testing.
 * @author Keith Donald
 */
public class StubConnectionRepository implements ConnectionRepository {

	private final SecureRandom secureRandom = new SecureRandom();
	
	private final List<Map<String, Object>> connections = new ArrayList<Map<String, Object>>();
	
	public boolean isConnected(Serializable accountId, String providerId) {
		for (Map<String, Object> connection : connections) {
			if (connection.get("accountId").equals(accountId) && connection.get("providerId").equals(providerId)) {
				return true;
			}
		}
		return false;
	}

	public List<Connection> findConnections(Serializable accountId, String providerId) {
		List<Connection> connectionList = new ArrayList<Connection>();
		for (Map<String, Object> connection : connections) {
			if (connection.get("accountId").equals(accountId) && connection.get("providerId").equals(providerId)) {
				connectionList.add(new Connection((Long) connection.get("id"), (String) connection.get("accessToken"), (String) connection.get("secret"), (String) connection.get("refreshToken")));
			}
		}
		return connectionList;
	}

	public Connection saveConnection(Serializable accountId, String providerId, Connection connection) {
		for (Iterator<Map<String, Object>> it = connections.iterator(); it.hasNext();) {
			Map<String, Object> conn = it.next();
			if (conn.get("accountId").equals(accountId) && conn.get("providerId").equals(providerId) && conn.get("accessToken").equals(connection.getAccessToken())) {
				throw new IllegalArgumentException("Duplicate connection");
			}
		}
		Long connectionId = secureRandom.nextLong();
		Map<String, Object> newConn = new HashMap<String, Object>();
		newConn.put("accountId", accountId);
		newConn.put("providerId", providerId);
		newConn.put("accessToken", connection.getAccessToken());
		newConn.put("secret", connection.getSecret());
		newConn.put("refreshToken", connection.getRefreshToken());
		newConn.put("id", connectionId);
		connections.add(newConn);
		return new Connection(connectionId, connection.getAccessToken(), connection.getSecret(), connection.getRefreshToken());
	}

	public void removeConnection(Serializable accountId, String providerId, Long connectionId) {
		for (Iterator<Map<String, Object>> it = connections.iterator(); it.hasNext();) {
			Map<String, Object> connection = it.next();
			if (connection.get("accountId").equals(accountId) && connection.get("providerId").equals(providerId) && connection.get("id").equals(connectionId)) {
				it.remove();
				break;
			}
		}
	}
	
}