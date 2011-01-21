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
package org.springframework.social.provider;

/**
 * A connection between a user account and a service provider.
 * @author Keith Donald
 * @param <S> the service API
 */
public interface ServiceProviderConnection<S> {

	/**
	 * The connection identifier.
	 */
	public Long getId();

	/**
	 * The Service API.
	 */
	public S getApi();
	
	/**
	 * Severs this connection.
	 * This object should no longer be used after calling this method.
	 */
	public void disconnect();
	
}