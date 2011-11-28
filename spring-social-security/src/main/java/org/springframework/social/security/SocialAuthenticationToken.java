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

package org.springframework.social.security;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.social.ServiceProvider;
import org.springframework.social.connect.ConnectionData;

/**
 * auth token for social authentication, e.g. Twitter or Facebook
 * 
 * 
 * @author stf@molindo.at
 */
@SuppressWarnings("serial")
public class SocialAuthenticationToken extends AbstractAuthenticationToken {

	private final String providerId;

	private final Serializable principle;

	private final Map<String, String> providerAccountData;

	/**
	 * new unauthenticated token
	 * 
	 * @param connection
	 *            connection data
	 * @param providerAccountData
	 *            optional extra account data
	 */
	public SocialAuthenticationToken(final ConnectionData connection,
			final Map<String, String> providerAccountData) {
		super(null);

		if (connection == null) {
			throw new NullPointerException("connection");
		}
		if (connection.getProviderId() == null) {
			throw new NullPointerException("connection.providerId");
		}
		if (connection.getExpireTime() != null && connection.getExpireTime() < System.currentTimeMillis()) {
			throw new IllegalArgumentException("connection.expireTime < currentTime");
		}
		
		this.providerId = connection.getProviderId();
		this.principle = connection;
		if (providerAccountData != null) {
			this.providerAccountData = Collections.unmodifiableMap(new HashMap<String, String>(providerAccountData));
		} else {
			this.providerAccountData = Collections.emptyMap();
		}
		super.setAuthenticated(false);
	}

	/**
	 * new authenticated token using authorities from provided {@link UserDetails}
	 * 
	 * @param providerId
	 *            {@link ServiceProvider} id
	 * @param details
	 *            user details, typically as returned by
	 *            {@link SocialUserDetailsService}
	 * @param providerAccountData
	 *            optional extra account data
	 */
	public SocialAuthenticationToken(final String providerId, final UserDetails details,
			final Map<String, String> providerAccountData) {
		this(providerId, details, providerAccountData, details.getAuthorities());
	}
	
	/**
	 * new authenticated token
	 * 
	 * @param providerId
	 *            {@link ServiceProvider} id
	 * @param details
	 *            user details, typically as returned by
	 *            {@link SocialUserDetailsService}
	 * @param providerAccountData
	 *            optional extra account data
	 * @param authorities
	 *            any {@link GrantedAuthority}s for this user
	 */
	public SocialAuthenticationToken(final String providerId, final UserDetails details,
			final Map<String, String> providerAccountData, final Collection<? extends GrantedAuthority> authorities) {
		super(authorities);
		if (providerId == null) {
			throw new NullPointerException("providerId");
		}
		if (details == null) {
			throw new NullPointerException("details");
		}
		this.providerId = providerId;
		this.principle = details;
		if (providerAccountData != null) {
			this.providerAccountData = Collections.unmodifiableMap(new HashMap<String, String>(providerAccountData));
		} else {
			this.providerAccountData = Collections.emptyMap();
		}
		super.setAuthenticated(true);
	}

	/**
	 * @return {@link ServiceProvider} id
	 */
	public String getProviderId() {
		return providerId;
	}

	/**
	 * @return always null
	 */
	public Object getCredentials() {
		return null;
	}

	/**
	 * @return {@link ConnectionData} if not authenticated, {@link UserDetails}
	 *         otherwise
	 */
	public Serializable getPrincipal() {
		return principle;
	}

	/**
	 * @return unmodifiable map, never null
	 */
	public Map<String, String> getProviderAccountData() {
		return providerAccountData;
	}

	/**
	 * @throws IllegalArgumentException
	 *             when trying to authenticate a previously unauthenticated
	 *             token
	 */
	@Override
	public void setAuthenticated(final boolean isAuthenticated) throws IllegalArgumentException {
		if (!isAuthenticated) {
			super.setAuthenticated(false);
		} else if (!super.isAuthenticated()) {
			throw new IllegalArgumentException(
					"Cannot set this token to trusted - use constructor which takes a GrantedAuthority list instead");
		}
	}

}
