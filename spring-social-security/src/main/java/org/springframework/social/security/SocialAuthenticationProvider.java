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

import java.util.Collection;
import java.util.List;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.social.ServiceProvider;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.UsersConnectionRepository;
import org.springframework.util.Assert;

/**
 * {@link AuthenticationProvider} for spring-social based {@link ServiceProvider}s
 * 
 * @author Stefan Fussennegger
 * @author Yuan Ji
 */
public class SocialAuthenticationProvider implements AuthenticationProvider {

	private UsersConnectionRepository usersConnectionRepository;
	
	private SocialUserDetailsService userDetailsService;

	public SocialAuthenticationProvider(UsersConnectionRepository usersConnectionRepository, SocialUserDetailsService userDetailsService) {
		this.usersConnectionRepository = usersConnectionRepository;
		this.userDetailsService = userDetailsService;
	}
	
	public boolean supports(Class<? extends Object> authentication) {
		return SocialAuthenticationToken.class.isAssignableFrom(authentication);
	}

	/**
	 * Authenticate user based on {@link SocialAuthenticationToken}
	 */
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		Assert.isInstanceOf(SocialAuthenticationToken.class, authentication, "unsupported authentication type");
		Assert.isTrue(!authentication.isAuthenticated(), "already authenticated");
		SocialAuthenticationToken authToken = (SocialAuthenticationToken) authentication;
		String providerId = authToken.getProviderId();
		Connection<?> connection = authToken.getConnection();

		String userId = toUserId(connection);
		if (userId == null) {
			throw new BadCredentialsException("Unknown access token");
		}

		UserDetails userDetails = userDetailsService.loadUserByUserId(userId);
		if (userDetails == null) {
			throw new UsernameNotFoundException("Unknown connected account id");
		}

		return new SocialAuthenticationToken(connection, userDetails, authToken.getProviderAccountData(), getAuthorities(providerId, userDetails));
	}

	protected String toUserId(Connection<?> connection) {
		List<String> userIds = usersConnectionRepository.findUserIdsWithConnection(connection);
		// only if a single userId is connected to this providerUserId
		return (userIds.size() == 1) ? userIds.iterator().next() : null;
	}

	/**
	 * Override to grant authorities based on {@link ServiceProvider} id and/or a user's account id
	 * @param providerId {@link ServiceProvider} id
	 * @param userDetails {@link UserDetails} as returned by {@link SocialUserDetailsService}
	 * @return extra authorities of this user not already returned by {@link UserDetails#getAuthorities()}
	 */
	protected Collection<? extends GrantedAuthority> getAuthorities(String providerId, UserDetails userDetails) {
		return userDetails.getAuthorities();
	}

}
