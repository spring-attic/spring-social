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

import org.springframework.social.connect.ConnectionFactory;
import org.springframework.web.context.request.WebRequest;


/**
 * Listens for service provider disconnection events.
 * Allows for custom logic to be executed before and after connections are deleted with a specific service provider.
 * @author Craig Walls
 * @param <S> The service API hosted by the intercepted service provider.
 */
public interface DisconnectInterceptor<S> {

	/**
	 * Called immediately before a connection is removed.
	 * @param connectionFactory the connection factory for the service provider
	 * @param request the web request
	 */
	void preDisconnect(ConnectionFactory<S> connectionFactory, WebRequest request);	
	
	/**
	 * Called immediately after a connection is removed.
	 * @param connectionFactory the connection factory for the service provider
	 * @param request the web request
	 */
	void postDisconnect(ConnectionFactory<S> connectionFactory, WebRequest request);
	
}
