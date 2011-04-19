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

/**
 * Thrown by a {@link ServiceProviderConnectionRepository} when attempting to fetch a ServiceProviderConnection 
 * and no such connection exists with the provided key.
 * @author Keith Donald
 * @see ServiceProviderConnectionRepository#findConnection(ServiceProviderConnectionKey)
 */
@SuppressWarnings("serial")
public final class NoSuchServiceProviderConnectionException extends RuntimeException {
	
	private final ServiceProviderConnectionKey connectionKey;

	public NoSuchServiceProviderConnectionException(ServiceProviderConnectionKey connectionKey) {
		this.connectionKey = connectionKey;
	}

	/**
	 * The invalid key value.
	 */
	public ServiceProviderConnectionKey getConnectionKey() {
		return connectionKey;
	}
	
}
