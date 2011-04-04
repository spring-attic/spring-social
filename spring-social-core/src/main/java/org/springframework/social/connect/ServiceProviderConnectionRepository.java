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

import java.util.List;
import java.util.Map;

public interface ServiceProviderConnectionRepository {

	List<ServiceProviderConnection<?>> findConnections();

	List<ServiceProviderConnection<?>> findConnectionsToProvider(String providerId);

	List<ServiceProviderConnection<?>> findConnectionsForUsers(Map<String, List<String>> providerUsers);
	
	ServiceProviderConnection<?> findConnectionByKey(ServiceProviderConnectionKey connectionKey);

	<S> ServiceProviderConnection<S> findConnectionByServiceApi(Class<S> serviceApiType);

	<S> ServiceProviderConnection<S> findConnectionByServiceApiForUser(Class<S> serviceApiType, String providerUserId);

	<S> ServiceProviderConnection<S> saveConnection(ServiceProviderConnection<S> connection);

	void removeConnectionsToProvider(String providerId);

	void removeConnectionWithKey(ServiceProviderConnectionKey connectionKey);
	
}