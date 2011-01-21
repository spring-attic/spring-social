/*
 * Copyright 2010 the original author or authors.
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
package org.springframework.social.provider.support;

import java.io.Serializable;
import java.util.List;

import org.springframework.social.provider.AuthorizationProtocol;
import org.springframework.social.provider.ServiceProvider;
import org.springframework.social.provider.ServiceProviderConnection;

/**
 * General-purpose base class for ServiceProvider implementations.
 * @author Keith Donald
 * @param <S> The service API hosted by this service provider.
 */
public abstract class AbstractServiceProvider<S> implements ServiceProvider<S> {

	private final String id;
	
	private final String displayName;
	
	private final ConnectionRepository connectionRepository;
	
	public AbstractServiceProvider(String id, String displayName, ConnectionRepository connectionRepository) {
		this.id = id;
		this.displayName = displayName;
		this.connectionRepository = connectionRepository;
	}
	
	public String getDisplayName() {
		return displayName;
	}

	public abstract AuthorizationProtocol getAuthorizationProtocol();

	public boolean isConnected(Serializable accountId) {
		return connectionRepository.isConnected(accountId, id);
	}

	public List<ServiceProviderConnection<S>> getConnections(Serializable accountId) {
		return null;
	}
		
}