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

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.social.ServiceProvider;
import org.springframework.social.connect.ConnectionData;
import org.springframework.social.connect.UsersConnectionRepository;
import org.springframework.util.Assert;

/**
 * {@link AuthenticationProvider} for spring-social based
 * {@link ServiceProvider}s
 * 
 * @author stf@molindo.at
 */
public class SocialAuthenticationProvider implements AuthenticationProvider {

	private UsersConnectionRepository usersConnectionRepository;
	private SocialUserDetailsService userDetailsService;

	/**
	 * supports {@link SocialAuthenticationToken} only
	 */
	public final boolean supports(final Class<? extends Object> authentication) {
		return SocialAuthenticationToken.class.isAssignableFrom(authentication);
	}

	/**
	 * authenticate user based on {@link SocialAuthenticationToken}
	 */
	public final Authentication authenticate(final Authentication authentication) throws AuthenticationException {
		Assert.isInstanceOf(SocialAuthenticationToken.class, authentication, "unsupported authentication type");
		Assert.isTrue(!authentication.isAuthenticated(), "already authenticated");
		Assert.isInstanceOf(ConnectionData.class, authentication.getPrincipal(), "unsupported principal type");

		final SocialAuthenticationToken authToken = (SocialAuthenticationToken) authentication;

		String providerId = authToken.getProviderId();
		ConnectionData principal = (ConnectionData) authToken.getPrincipal();

		String userId = toUserId(principal);

		if (userId == null) {
			throw new BadCredentialsException("unknown access token");
		}

		final UserDetails userDetails = userDetailsService.loadUserByUserId(userId);
		if (userDetails == null) {
			throw new UsernameNotFoundException("unknown connected account id");
		}

		return new SocialAuthenticationToken(providerId, userDetails, authToken.getProviderAccountData(),
				getAuthorities(providerId, userDetails));
	}

	protected String toUserId(ConnectionData data) {
		String providerId = data.getProviderId();
		HashSet<String> providerUserIds = new HashSet<String>();
		providerUserIds.add(data.getProviderUserId());

		Set<String> userIds = usersConnectionRepository.findUserIdsConnectedTo(providerId, providerUserIds);

		// only if a single userId is connected to this providerUserId
		return (userIds.size() == 1) ? userIds.iterator().next() : null;
	}

	/**
	 * override to grant authorities based on {@link ServiceProvider} id and/or
	 * a user's account id
	 * 
	 * @param providerId
	 *            {@link ServiceProvider} id
	 * @param userDetails
	 *            {@link UserDetails} as returned by
	 *            {@link SocialUserDetailsService}
	 * @return extra authorities of this user not already returned by
	 *         {@link UserDetails#getAuthorities()}
	 */
	protected Collection<? extends GrantedAuthority> getAuthorities(final String providerId,
			final UserDetails userDetails) {
		return userDetails.getAuthorities();
	}

	public UsersConnectionRepository getUsersConnectionRepository() {
		return usersConnectionRepository;
	}

	public void setUsersConnectionRepository(UsersConnectionRepository usersConnectionRepository) {
		this.usersConnectionRepository = usersConnectionRepository;
	}

	public SocialUserDetailsService getUserDetailsService() {
		return userDetailsService;
	}

	public void setUserDetailsService(final SocialUserDetailsService userDetailsService) {
		this.userDetailsService = userDetailsService;
	}

}
