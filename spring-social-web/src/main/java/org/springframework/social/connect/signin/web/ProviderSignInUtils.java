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
package org.springframework.social.connect.signin.web;

import org.springframework.web.context.request.WebRequest;

/**
 * Helper methods that support provider sign-in scenarios.
 * @author Keith Donald
 */
public class ProviderSignInUtils {
	
	/**
	 * Connect the new user account to the provider account the user attempted to sign-in with.
	 * Should be called after signing-up a new user in the context of a provider sign-in attempt.
	 * In this context, the user did not yet have a local account but attempted to sign-in using one of his or her existing provider accounts.
	 * @param request the current web request, used to extract sign-in attempt information from the current user session
	 */
	public static void handleConnectPostSignUp(WebRequest request) {
		ProviderSignInAttempt signInAttempt = (ProviderSignInAttempt) request.getAttribute(ProviderSignInAttempt.SESSION_ATTRIBUTE, WebRequest.SCOPE_SESSION);
		if (signInAttempt != null) {
			signInAttempt.addConnection();
			request.removeAttribute(ProviderSignInAttempt.SESSION_ATTRIBUTE, WebRequest.SCOPE_SESSION);
		}		
	}
	
}
