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

import java.util.Set;

/**
 * A ServiceLocator for {@link ServiceProviderConnectionFactory} instances.
 * Supports lookup by providerId and by serviceApiType.
 * @author Keith Donald
 * @see ServiceProviderConnectionFactory
 */
public interface ServiceProviderConnectionFactoryLocator {

	/**
	 * Lookup a ServiceProviderConnectionFactory by providerId; for example, "facebook".
	 * The returned factory can be used to create connections to the provider.
	 * Used to support connection creation in a dynamic manner across the set of registered providers.
	 */
	ServiceProviderConnectionFactory<?> getConnectionFactory(String providerId);

	/**
	 * Lookup a ServiceProviderConnectionFactory by serviceApiType; for example, FacebookApi.class.
	 * The returned factory can be used to create connections to the provider.
	 * Primarily used in support of connection restoration requested by application code.
	 * @see ServiceProviderConnectionRepository#findPrimaryConnectionToServiceApi(Class)
	 */
	<S> ServiceProviderConnectionFactory<S> getConnectionFactory(Class<S> serviceApiType);

	/**
	 * Returns the set of providerIds for which a {@link ServiceProviderConnectionFactory} is registered; for example, <code>{ "twitter", "facebook", "foursquare" }</code>
	 * Elements in this set can be passed to {@link #getConnectionFactory(String)} to fetch a specific factory instance.
	 */
	Set<String> registeredProviderIds();
	
}
