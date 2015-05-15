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
package org.springframework.social.config.annotation;

import org.springframework.social.connect.ConnectionFactory;
import org.springframework.social.connect.support.ConnectionFactoryRegistry;
import org.springframework.social.connect.support.OAuth1ConnectionFactory;
import org.springframework.social.connect.support.OAuth2ConnectionFactory;
import org.springframework.social.security.SocialAuthenticationServiceRegistry;
import org.springframework.social.security.provider.OAuth1AuthenticationService;
import org.springframework.social.security.provider.OAuth2AuthenticationService;
import org.springframework.social.security.provider.SocialAuthenticationService;

/**
 * Spring Social Security-aware implementation fo ConnectionFactoryConfigurer.
 * Wraps given {@link ConnectionFactory}s as {@link SocialAuthenticationService}s and registers them with a {@link SocialAuthenticationServiceRegistry}.
 * @author Craig Walls
 */
class SecurityEnabledConnectionFactoryConfigurer implements ConnectionFactoryConfigurer {

	private SocialAuthenticationServiceRegistry registry;
	
	public SecurityEnabledConnectionFactoryConfigurer() {
		registry = new SocialAuthenticationServiceRegistry();
	}
	
	public void addConnectionFactory(ConnectionFactory<?> connectionFactory) {
		registry.addAuthenticationService(wrapAsSocialAuthenticationService(connectionFactory));
	}
	
	public ConnectionFactoryRegistry getConnectionFactoryLocator() {
		return registry;
	}

	
	private <A> SocialAuthenticationService<A> wrapAsSocialAuthenticationService(ConnectionFactory<A> cf) {
		if (cf instanceof OAuth1ConnectionFactory) {
			return new OAuth1AuthenticationService<A>((OAuth1ConnectionFactory<A>) cf);
		} else if (cf instanceof OAuth2ConnectionFactory) {
			final OAuth2AuthenticationService<A> authService = new OAuth2AuthenticationService<A>((OAuth2ConnectionFactory<A>) cf);
			authService.setDefaultScope(((OAuth2ConnectionFactory<A>) cf).getScope());
			return authService;
		}
		throw new IllegalArgumentException("The connection factory must be one of OAuth1ConnectionFactory or OAuth2ConnectionFactory");
	}
	
}
