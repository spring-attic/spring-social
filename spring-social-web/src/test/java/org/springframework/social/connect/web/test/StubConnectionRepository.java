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
package org.springframework.social.connect.web.test;

import java.util.Collections;
import java.util.List;

import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionKey;
import org.springframework.social.connect.ConnectionRepository;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

public class StubConnectionRepository implements ConnectionRepository {
	
	private MultiValueMap<String, Connection<?>> providerIdConnectionMap = new LinkedMultiValueMap<String, Connection<?>>();	

	private MultiValueMap<ConnectionKey, Connection<?>> connectionKeyConnectionMap = new LinkedMultiValueMap<ConnectionKey, Connection<?>>();	

	public MultiValueMap<String, Connection<?>> findAllConnections() {
		return providerIdConnectionMap;
	}

	public List<Connection<?>> findConnections(String providerId) {
		return providerIdConnectionMap.containsKey(providerId) ? providerIdConnectionMap.get(providerId) : Collections.<Connection<?>>emptyList();
	}

	public <A> List<Connection<A>> findConnections(Class<A> apiType) {
		return null;
	}

	public MultiValueMap<String, Connection<?>> findConnectionsToUsers(MultiValueMap<String, String> providerUserIds) {
		return null;
	}

	public Connection<?> getConnection(ConnectionKey connectionKey) {
		return null;
	}

	public <A> Connection<A> getConnection(Class<A> apiType, String providerUserId) {
		return null;
	}

	public <A> Connection<A> getPrimaryConnection(Class<A> apiType) {
		return null;
	}
	
	public <A> Connection<A> findPrimaryConnection(Class<A> apiType) {
		return null;
	}

	public void addConnection(Connection<?> connection) {
		providerIdConnectionMap.add(connection.getKey().getProviderId(), connection);
		connectionKeyConnectionMap.add(connection.getKey(), connection);
	}

	public void updateConnection(Connection<?> connection) {
	}

	public void removeConnections(String providerId) {
		providerIdConnectionMap.remove(providerId);
	}

	public void removeConnection(ConnectionKey connectionKey) {
		connectionKeyConnectionMap.remove(connectionKey);
		List<Connection<?>> connections = providerIdConnectionMap.get(connectionKey.getProviderId());
		providerIdConnectionMap.remove(connectionKey.getProviderId());
		for (Connection<?> connection : connections) {
			if (connection.getKey().equals(connectionKey)) {
				providerIdConnectionMap.add(connectionKey.getProviderId(), connection);
			}
		}
	}

}
