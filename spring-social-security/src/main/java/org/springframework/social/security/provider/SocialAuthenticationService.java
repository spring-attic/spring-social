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
package org.springframework.social.security.provider;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionFactory;
import org.springframework.social.security.SocialAuthenticationRedirectException;
import org.springframework.social.security.SocialAuthenticationToken;

/**
 * Authentication for social {@link ConnectionFactory}
 * @param <S> The provider's API type.
 * @author Stefan Fussennegger
 */
public interface SocialAuthenticationService<S> {

	public enum ConnectionCardinality {
		/**
		 * only one connected providerUserId per userId and vice versa
		 */
		ONE_TO_ONE(false, false),

		/**
		 * many connected providerUserIds per userId, but only one userId per providerUserId
		 */
		ONE_TO_MANY(false, true),

		/**
		 * only one providerUserId per userId, but many userIds per providerUserId. 
		 * Authentication of users not possible
		 */
		MANY_TO_ONE(true, false),

		/**
		 * no restrictions. Authentication of users not possible
		 */
		MANY_TO_MANY(true, true);
		
		private final boolean multiUserId;
		private final boolean multiProviderUserId;

		private ConnectionCardinality(boolean multiUserId, boolean multiProviderUserId) {
			this.multiUserId = multiUserId;
			this.multiProviderUserId = multiProviderUserId;
		}

		/**
		 * allow many userIds per providerUserId. If true, authentication is not possible
		 * @return true if multiple local users are allowed per provider user ID
		 */
		public boolean isMultiUserId() {
			return multiUserId;
		}

		/**
		 * allow many providerUserIds per userId
		 * @return true if users are allowed multiple connections to a provider
		 */
		public boolean isMultiProviderUserId() {
			return multiProviderUserId;
		}

		public boolean isAuthenticatePossible() {
			return !isMultiUserId();
		}
	}

	/**
	 * @return {@link ConnectionCardinality} for connections to this provider
	 */
	ConnectionCardinality getConnectionCardinality();
	
	/**
	 * @return {@link ConnectionFactory} used for authentication
	 */
	ConnectionFactory<S> getConnectionFactory();

	/**
	 * extract {@link SocialAuthenticationToken} from request
	 * 
	 * @param request current {@link HttpServletRequest}
	 * @param response current {@link HttpServletResponse}
	 * 
	 * @return new unauthenticated token or null
	 * @throws SocialAuthenticationRedirectException if social auth requires a redirect, e.g. OAuth
	 */
	SocialAuthenticationToken getAuthToken(HttpServletRequest request, HttpServletResponse response) throws SocialAuthenticationRedirectException;

	/**
	 * 
	 * @param request current {@link HttpServletRequest}
	 * @param connection the connection from which to calculate the redirect URL
	 * @return null to use filter default
	 */
	String getConnectionAddedRedirectUrl(HttpServletRequest request, Connection<?> connection);

}
