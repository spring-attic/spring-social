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

import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.authentication.RememberMeServices;
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
 *        .apply(new SpringSocialHttpConfigurer());
 * }
 * </pre>
 * 
 * @author Craig Walls
 */
public class SpringSocialConfigurer extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {

	private UserIdSource userIdSource;

	/**
	 * Constructs a SpringSocialHttpConfigurer.
	 * Requires that {@link UsersConnectionRepository}, {@link SocialAuthenticationServiceLocator}, and
	 * {@link SocialUserDetailsService} beans be available in the application context.
	 */
	public SpringSocialConfigurer() {
	}
	
	@Override
	public void configure(HttpSecurity http) throws Exception {		
		ApplicationContext applicationContext = http.getSharedObject(ApplicationContext.class);
		UsersConnectionRepository usersConnectionRepository = getDependency(applicationContext, UsersConnectionRepository.class);
		SocialAuthenticationServiceLocator authServiceLocator = getDependency(applicationContext, SocialAuthenticationServiceLocator.class);
		SocialUserDetailsService socialUsersDetailsService = getDependency(applicationContext, SocialUserDetailsService.class);
		
		SocialAuthenticationFilter filter = new SocialAuthenticationFilter(
				http.getSharedObject(AuthenticationManager.class), 
				userIdSource != null ? userIdSource : new AuthenticationNameUserIdSource(), 
				usersConnectionRepository, 
				authServiceLocator);

		RememberMeServices rememberMe = http.getSharedObject(RememberMeServices.class);
		if(rememberMe != null) {
			filter.setRememberMeServices(rememberMe);
		}
		http.authenticationProvider(
				new SocialAuthenticationProvider(usersConnectionRepository, socialUsersDetailsService))
			.addFilterBefore(postProcess(filter), AbstractPreAuthenticatedProcessingFilter.class);
	}

	private <T> T getDependency(ApplicationContext applicationContext, Class<T> dependencyType) {
		try {
			T dependency = applicationContext.getBean(dependencyType);
			return dependency;
		} catch (NoSuchBeanDefinitionException e) {
			throw new IllegalStateException("SpringSocialConfigurer depends on " + dependencyType.getName() +". No single bean of that type found in application context.", e);
		}
	}
	
	/**
	 * Sets the {@link UserIdSource} to use for authentication. Defaults to {@link AuthenticationNameUserIdSource}.
	 */
	public SpringSocialConfigurer userIdSource(UserIdSource userIdSource) {
		this.userIdSource = userIdSource;
		return this;
	}
	
}
