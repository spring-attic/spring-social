/*
 * Copyright 2010 the original author or authors.
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

package org.springframework.social.security.provider;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionFactory;
import org.springframework.social.security.SocialAuthenticationRedirectException;
import org.springframework.social.security.SocialAuthenticationToken;

/**
 * Authentication for social {@link ConnectionFactory}
 * 
 * @param <S>
 *            The service hosted by the service provider.
 * 
 * @author stf@molindo.at
 */
public interface SocialAuthenticationService<S> {

	public enum AuthenticationMode {
		EXPLICIT, IMPLICIT;
	}

	/**
	 * @return supported {@link AuthenticationMode} or <code>null</code> for
	 *         both
	 */
	AuthenticationMode getAuthenticationMode();

	/**
	 * @return {@link ConnectionFactory} used for authentication
	 */
	ConnectionFactory<S> getConnectionFactory();

	/**
	 * extract {@link SocialAuthenticationToken} from request
	 * 
	 * @param authMode
	 *            requested {@link AuthenticationMode} (explicit or implicit)
	 * @param request
	 *            current {@link HttpServletRequest}
	 * @param response
	 *            current {@link HttpServletResponse}
	 * 
	 * @return new unauthenticated token or null
	 * @throws SocialAuthenticationRedirectException
	 *             if social auth requires a redirect, e.g. OAuth
	 * @see SocialAuthenticationToken#SocialAuthenticationToken(String, String,
	 *      Object)
	 */
	SocialAuthenticationToken getAuthToken(AuthenticationMode authMode, HttpServletRequest request,
			HttpServletResponse response) throws SocialAuthenticationRedirectException;

	String getConnectionAddedRedirectUrl(HttpServletRequest request, Connection<?> connection);
}
