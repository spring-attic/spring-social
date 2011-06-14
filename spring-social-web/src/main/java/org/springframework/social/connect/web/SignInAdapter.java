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
package org.springframework.social.connect.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.social.connect.Connection;

/**
 * Adapter that bridges between a {@link ProviderSignInController} and a application-specific user sign-in service.
 * Invoked at the end of a provider sign-in attempt to sign-in the local user account associated with the provider user account.
 * @author Craig Walls
 */
public interface SignInAdapter {

	/**
	 * Complete a provider sign-in attempt by signing in the local user account with the specified id.
	 * Called if this SignInAdapter supports users.
	 * @param userId the local user id
	 * @param connection the connection
	 * @param request a reference to the current servlet request
	 * @param response a reference to the current servlet response
	 */
	void signIn(String userId, Connection<?> connection, HttpServletRequest request, HttpServletResponse response);

}