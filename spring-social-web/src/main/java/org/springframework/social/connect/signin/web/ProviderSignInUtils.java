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

import org.springframework.social.connect.ServiceProviderUserProfile;
import org.springframework.web.context.request.WebRequest;

/**
 * Helper methods that support provider sign-in scenarios.
 * @author Keith Donald
 */
public class ProviderSignInUtils {
	
	/**
	 * Get the profile of the provider user the client attempted to sign-in with.
	 * This profile data can be used to pre-populate a local application registration/signup form.
	 * Returns null if no provider sign-in was attempted.
	 * @param request the current web request, used to extract sign-in attempt information from the current user session
	 */
	public static ServiceProviderUserProfile getUserProfile(WebRequest request) {
		ProviderSignInAttempt signInAttempt = getProviderSignInAttempt(request);
		return signInAttempt != null ? signInAttempt.getUserProfile() : null;
	}

	/**
	 * Connect the new user to the provider user the client attempted to sign-in with.
	 * Should be called after signing-up a new user in the context of a provider sign-in attempt.
	 * In this context, the user did not yet have a local account but attempted to sign-in using one of his or her existing provider accounts.
	 * Ensures provider sign-in attempt session context is cleaned up.
	 * Does nothing if no provider sign-in was attempted for the current user session (is safe to call in that case).
	 * @param request the current web request, used to extract sign-in attempt information from the current user session
	 */
	public static void handleConnectPostSignUp(WebRequest request) {
		ProviderSignInAttempt signInAttempt = getProviderSignInAttempt(request);
		if (signInAttempt != null) {
			signInAttempt.addConnection();
			request.removeAttribute(ProviderSignInAttempt.SESSION_ATTRIBUTE, WebRequest.SCOPE_SESSION);
		}		
	}

	// internal helpers
	
	private ProviderSignInUtils() {	
	}
	
	private static ProviderSignInAttempt getProviderSignInAttempt(WebRequest request) {
		return (ProviderSignInAttempt) request.getAttribute(ProviderSignInAttempt.SESSION_ATTRIBUTE, WebRequest.SCOPE_SESSION);
	}
	
}