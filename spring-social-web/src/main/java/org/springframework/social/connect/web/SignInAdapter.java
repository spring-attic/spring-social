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
import org.springframework.web.context.request.NativeWebRequest;

/**
 * Adapter that bridges between a {@link ProviderSignInController} and a application-specific user sign-in service.
 * Invoked at the end of a provider sign-in attempt to sign-in the local user account associated with the provider user account.
 * @author Craig Walls
 */
public interface SignInAdapter {

	/**
	 * Complete a provider sign-in attempt by signing in the local user account with the specified id.
	 * @param userId the local user id
	 * @param connection the connection
	 * @param request a reference to the current web request; is a "native" web request instance providing access to the native
	 * request and response objects, such as a HttpServletRequest and HttpServletResponse, if needed
	 * @return the URL that ProviderSignInController should redirect to after sign in. May be null, indicating that ProviderSignInController
	 * should redirect to its postSignInUrl.
	 */
	String signIn(String userId, Connection<?> connection, NativeWebRequest request);

}
