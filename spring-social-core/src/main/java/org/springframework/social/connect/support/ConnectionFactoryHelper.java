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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionData;
import org.springframework.social.connect.ConnectionFactory;
import org.springframework.social.connect.ConnectionFactoryLocator;
import org.springframework.social.connect.ConnectionRepository;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

/**
 * helper class to support converting {@link ConnectionData} to {@link Connection}s. It's
 * main intention is to help with custom {@link ConnectionRepository} implementations.
 *
 * @author stf@molindo.at
 */
public class ConnectionFactoryHelper {

	private ConnectionFactoryLocator connectionFactoryLocator;

	public ConnectionFactoryHelper(ConnectionFactoryLocator connectionFactoryLocator) {
		if (connectionFactoryLocator == null) {
			throw new NullPointerException("connectionFactoryLocator");
		}
		this.connectionFactoryLocator = connectionFactoryLocator;
	}

	public final MultiValueMap<String, Connection<?>> toConnectionsMap(
			MultiValueMap<String, ConnectionData> connectionData) {

		MultiValueMap<String, Connection<?>> connections = new LinkedMultiValueMap<String, Connection<?>>();
		for (Map.Entry<String, List<ConnectionData>> e : connectionData.entrySet()) {
			connections.put(e.getKey(), toConnections(e.getValue()));
		}
		return connections;
	}

	public final MultiValueMap<String, Connection<?>> toConnectionsMap(List<ConnectionData> connectionData) {

		MultiValueMap<String, Connection<?>> connections = new LinkedMultiValueMap<String, Connection<?>>();
		for (ConnectionData data : connectionData) {
			connections.add(data.getProviderId(), toConnection(data));
		}
		return connections;
	}

	public final <A> List<Connection<A>> toTypedConnections(List<ConnectionData> connectionData) {
		List<Connection<A>> connections = new ArrayList<Connection<A>>(connectionData.size());

		for (ConnectionData data : connectionData) {
			Connection<A> connection = toConnection(data);
			if (connection != null) {
				connections.add(connection);
			}
		}

		return connections;
	}

	// how to to get a non-typed connection list without duplicating code?
	public final List<Connection<?>> toConnections(List<ConnectionData> connectionData) {
		List<Connection<?>> connections = new ArrayList<Connection<?>>(connectionData.size());

		for (ConnectionData data : connectionData) {
			Connection<?> connection = toConnection(data);
			if (connection != null) {
				connections.add(connection);
			}
		}

		return connections;
	}

	@SuppressWarnings("unchecked")
	public <A> Connection<A> toConnection(ConnectionData data) {
		if (data == null) {
			return null;
		}

		ConnectionFactory<?> factory = connectionFactoryLocator.getConnectionFactory(data.getProviderId());
		if (factory == null) {
			// TODO what's the expected behavior?
			return null;
		}

		return (Connection<A>) factory.createConnection(data);
	}

}