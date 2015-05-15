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
package org.springframework.social.connect.web;

import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionFactory;
import org.springframework.util.MultiValueMap;
import org.springframework.web.context.request.WebRequest;

/**
 * Listens for service provider connection events.
 * Allows for custom logic to be executed before and after connections are established with a specific service provider.
 * @author Keith Donald
 * @author Craig Walls
 * @param <S> The service API hosted by the intercepted service provider.
 */
public interface ConnectInterceptor<S> {
	
	/**
	 * Called during connection initiation, immediately before user authorization.
	 * May be used to store custom connection attributes in the session before redirecting the user to the provider's site or to contribute parameters to the authorization URL.
	 * @param connectionFactory the connection factory in play for this connection
	 * @param parameters parameters being sent to the provider during authorization
	 * @param request the request
	 */
	void preConnect(ConnectionFactory<S> connectionFactory, MultiValueMap<String, String> parameters, WebRequest request);

	/**
	 * Called immediately after the connection is established.
	 * Used to invoke the service API on behalf of the user upon connecting.
	 * @param connection the connection that was just established
	 * @param request the request
	 */
	void postConnect(Connection<S> connection, WebRequest request);
	
}
