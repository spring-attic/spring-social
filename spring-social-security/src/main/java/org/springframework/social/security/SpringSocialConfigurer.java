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
	
	private String postLoginUrl;
	
	private String postFailureUrl;

	private String signupUrl;

	private String connectionAddedRedirectUrl;

	private String defaultFailureUrl;

	private boolean alwaysUsePostLoginUrl = false;

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
		if (rememberMe != null) {
			filter.setRememberMeServices(rememberMe);
		}
		
		if (postLoginUrl != null) {
			filter.setPostLoginUrl(postLoginUrl);
			filter.setAlwaysUsePostLoginUrl(alwaysUsePostLoginUrl);
		}
		
		if (postFailureUrl != null) {
			filter.setPostFailureUrl(postFailureUrl);
		}

		if (signupUrl != null) {
			filter.setSignupUrl(signupUrl);
		}

		if (connectionAddedRedirectUrl != null) {
			filter.setConnectionAddedRedirectUrl(connectionAddedRedirectUrl);
		}

		if (defaultFailureUrl != null) {
			filter.setDefaultFailureUrl(defaultFailureUrl);
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
	 * @param userIdSource the UserIdSource to use when authenticating
	 * @return this SpringSocialConfigurer for chained configuration
	 */
	public SpringSocialConfigurer userIdSource(UserIdSource userIdSource) {
		this.userIdSource = userIdSource;
		return this;
	}
	
	/**
	 * Sets the URL to land on after a successful login.
	 * @param postLoginUrl the URL to redirect to after a successful login
     * @return this SpringSocialConfigurer for chained configuration
	 */
	public SpringSocialConfigurer postLoginUrl(String postLoginUrl) {
		this.postLoginUrl = postLoginUrl;
		return this;
	}
	
	/**
	 * If true, always redirect to postLoginUrl, even if a pre-signin target is in the request cache.
	 * @param alwaysUsePostLoginUrl if true, always redirect to the postLoginUrl
     * @return this SpringSocialConfigurer for chained configuration
	 */
	public SpringSocialConfigurer alwaysUsePostLoginUrl(boolean alwaysUsePostLoginUrl) {
		this.alwaysUsePostLoginUrl = alwaysUsePostLoginUrl;
		return this;
	}
	
	/**
	 * Sets the URL to land on after a failed login.
	 * @param postFailureUrl the URL to redirect to after a failed login
     * @return this SpringSocialConfigurer for chained configuration
	 */
	public SpringSocialConfigurer postFailureUrl(String postFailureUrl) {
		this.postFailureUrl = postFailureUrl;
		return this;
	}

	/**
	 * Sets the URL to land on after an authentication failure so that the user can register with the application.
	 * @param signupUrl the URL to redirect to after an authentication failure
	 * @return this SpringSocialConfigurer for chained configuration
	 */
	public SpringSocialConfigurer signupUrl(String signupUrl) {
		this.signupUrl = signupUrl;
		return this;
	}

	/**
	 * Sets the URL to land on after an a connection was added.
	 * @param connectionAddedRedirectUrl the URL to redirect after a connection was added
	 * @return this SpringSocialConfigurer for chained configuration
	 */
	public SpringSocialConfigurer connectionAddedRedirectUrl(String connectionAddedRedirectUrl) {
		this.connectionAddedRedirectUrl = connectionAddedRedirectUrl;
		return this;
	}

	/**
	 * Sets the URL to redirect to if authentication fails or if authorization is denied by the user.
	 * @param defaultFailureUrl the URL to redirect to after an authentication fail or authorization deny
	 * @return this SpringSocialConfigurer for chained configuration
	 */
	public SpringSocialConfigurer defaultFailureUrl(String defaultFailureUrl) {
		this.defaultFailureUrl = defaultFailureUrl;
		return this;
	}
	
}
