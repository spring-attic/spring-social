/*
 * Copyright 2013 the original author or authors.
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

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;
import org.springframework.social.UserIdSource;
import org.springframework.social.connect.UsersConnectionRepository;

/**
 * Configurer that adds {@link SocialAuthenticationFilter} to Spring Security's filter chain.
 * Used with Spring Security 3.2's Java-based configuration support, when overriding WebSecurityConfigurerAdapter#configure(HttpSecurity):
 * 
 * <pre>
 * protected void configure(HttpSecurity http) throws Exception {
 *   http.
 *     // HTTP security configuration details snipped
 *     .and()
 *        .apply(
 *            new SpringSocialHttpConfigurer(userIdSource(), usersConnectionRepository, authenticationServiceLocator)
 *         );
 * }
 * </pre>
 * 
 * @author Craig Walls
 */
public class SpringSocialConfigurer extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {

	private final UserIdSource userIdSource;
	
	private final UsersConnectionRepository usersConnectionRepository;
	
	private final SocialAuthenticationServiceLocator authServiceLocator;

	private SocialUserDetailsService socialUsersDetailsService;

	/**
	 * Constructs a SpringSocialHttpConfigurer.
	 * @param userIdSource A {@link UserIdSource}.
	 * @param usersConnectionRepository A {@link UsersConnectionRepository}.
	 * @param authServiceLocator A {@link SocialAuthenticationServiceLocator}.
	 */
	public SpringSocialConfigurer(
			UserIdSource userIdSource, 
			UsersConnectionRepository usersConnectionRepository, 
			SocialAuthenticationServiceLocator authServiceLocator,
			SocialUserDetailsService socialUsersDetailsService) {
		this.userIdSource = userIdSource;
		this.usersConnectionRepository = usersConnectionRepository;
		this.authServiceLocator = authServiceLocator;
		this.socialUsersDetailsService = socialUsersDetailsService;
	}
	
	@Override
	public void configure(HttpSecurity http) throws Exception {
		http.authenticationProvider(
				new SocialAuthenticationProvider(usersConnectionRepository, socialUsersDetailsService))
			.addFilterBefore(
				new SocialAuthenticationFilter(http.getSharedObject(AuthenticationManager.class), userIdSource, usersConnectionRepository, authServiceLocator), 
				AbstractPreAuthenticatedProcessingFilter.class);
	}
	
}
