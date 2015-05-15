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
package org.springframework.social.security;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

/**
 * @author Craig Walls
 */
public class SocialAuthenticationFailureHandler implements AuthenticationFailureHandler {

	private AuthenticationFailureHandler delegate;

	public SocialAuthenticationFailureHandler(AuthenticationFailureHandler delegate) {
		this.delegate = delegate;
	}
	
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
		if (failed instanceof SocialAuthenticationRedirectException) {
			response.sendRedirect(((SocialAuthenticationRedirectException) failed).getRedirectUrl()); 
			return;
		}
		delegate.onAuthenticationFailure(request, response, failed);
	}

}
