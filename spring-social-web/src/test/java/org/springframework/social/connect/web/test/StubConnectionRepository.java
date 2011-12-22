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
package org.springframework.social.connect.web.test;

import java.util.List;

import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionKey;
import org.springframework.social.connect.ConnectionRepository;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

public class StubConnectionRepository implements ConnectionRepository {
	
	private MultiValueMap<String, Connection<?>> providerIdConnectionMap = new LinkedMultiValueMap<String, Connection<?>>();	

	public MultiValueMap<String, Connection<?>> findAllConnections() {
		// TODO Auto-generated method stub
		return null;
	}

	public List<Connection<?>> findConnections(String providerId) {
		return providerIdConnectionMap.get(providerId);
	}

	public <A> List<Connection<A>> findConnections(Class<A> apiType) {
		// TODO Auto-generated method stub
		return null;
	}

	public MultiValueMap<String, Connection<?>> findConnectionsToUsers(MultiValueMap<String, String> providerUserIds) {
		// TODO Auto-generated method stub
		return null;
	}

	public Connection<?> getConnection(ConnectionKey connectionKey) {
		// TODO Auto-generated method stub
		return null;
	}

	public <A> Connection<A> getConnection(Class<A> apiType, String providerUserId) {
		// TODO Auto-generated method stub
		return null;
	}

	public <A> Connection<A> getPrimaryConnection(Class<A> apiType) {
		// TODO Auto-generated method stub
		return null;
	}

	public <A> Connection<A> findPrimaryConnection(Class<A> apiType) {
		// TODO Auto-generated method stub
		return null;
	}

	public void addConnection(Connection<?> connection) {
		providerIdConnectionMap.add(connection.getKey().getProviderId(), connection);
	}

	public void updateConnection(Connection<?> connection) {
		// TODO Auto-generated method stub
		
	}

	public void removeConnections(String providerId) {
		// TODO Auto-generated method stub
		
	}

	public void removeConnection(ConnectionKey connectionKey) {
		// TODO Auto-generated method stub
		
	}

}
