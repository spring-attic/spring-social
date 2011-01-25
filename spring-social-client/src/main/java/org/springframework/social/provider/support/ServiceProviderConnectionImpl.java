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
package org.springframework.social.provider.support;

import java.io.Serializable;

import org.springframework.social.provider.ServiceProviderConnection;

/**
 * Generic ServiceProviderConnection implementation used by {@link AbstractServiceProvider}.
 * @author Keith Donald
 * @param <S> the service API
 */
class ServiceProviderConnectionImpl<S> implements ServiceProviderConnection<S> {

	private final Long id;
	
	private final S api;

	private final Serializable accountId;
	
	private final String providerId;

	private final ConnectionRepository connectionRepository;
	
	private boolean disconnected;
	
	public ServiceProviderConnectionImpl(Long id, S api, Serializable accountId, String providerId, ConnectionRepository connectionRepository) {
		this.id = id;
		this.api = api;
		this.connectionRepository = connectionRepository;
		this.accountId = accountId;
		this.providerId = providerId;
	}
	
	public S getApi() {
		if (disconnected) {
			throw new IllegalStateException("Unable to get Api: this Connection is disconnected");
		}
		return api;
	}

	public void disconnect() {
		if (disconnected) {
			throw new IllegalStateException("This connection is already disconnected");
		}		
		connectionRepository.removeConnection(accountId, providerId, id);
		disconnected = true;
	}
	
	public boolean equals(Object o) {
		if (!(o instanceof ServiceProviderConnectionImpl)) {
			return false;
		}
		ServiceProviderConnectionImpl<?> other = (ServiceProviderConnectionImpl<?>) o;
		return id.equals(other.id);
	}
	
	public int hashCode() {
		return id.hashCode();
	}

}
