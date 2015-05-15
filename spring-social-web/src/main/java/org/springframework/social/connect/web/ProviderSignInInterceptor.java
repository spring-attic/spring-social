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
 * Listens for service provider sign in events.
 * Allows for custom logic to be executed before and after sign in is performed via a specific service provider.
 * Note that this interceptor only handles events during {@link ProviderSignInController}'s flow.
 * It does not handle events that take place in the case where {@link ProviderSignInController} redirects to the application-defined signup process.
 * In that case, interceptors are not necessary and any post-signup logic can be performed using the connection carried in the {@link ProviderSignInAttempt}.
 * @author Craig Walls
 * @param <S> The service API hosted by the intercepted service provider.
 */
public interface ProviderSignInInterceptor<S> {
	
	/**
	 * Called during sign in initiation, immediately before user authorization.
	 * May be used to store custom connection attributes in the session before redirecting the user to the provider's site or to contribute parameters to the authorization URL.
	 * @param connectionFactory The connection factory
	 * @param parameters the parameters to be sent to the provider during authentication
	 * @param request The web request
	 */
	void preSignIn(ConnectionFactory<S> connectionFactory, MultiValueMap<String, String> parameters, WebRequest request);

	/**
	 * Called immediately after the sign in is complete.
	 * Used to invoke the service API on behalf of the user upon signing in.
	 * @param connection the connection that was created in the course of provider sign-in
	 * @param request the request
	 */
	void postSignIn(Connection<S> connection, WebRequest request);
	
}
