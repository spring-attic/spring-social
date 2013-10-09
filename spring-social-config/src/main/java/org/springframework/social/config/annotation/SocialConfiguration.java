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
package org.springframework.social.config.annotation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.core.env.Environment;
import org.springframework.social.UserIdSource;
import org.springframework.social.connect.ConnectionFactoryLocator;
import org.springframework.social.connect.ConnectionRepository;
import org.springframework.social.connect.UsersConnectionRepository;
import org.springframework.util.Assert;

/**
 * Configuration class imported by {@link EnableSocial}.
 * @author Craig Walls
 */
@Configuration
public class SocialConfiguration {

	private static boolean securityEnabled = isSocialSecurityAvailable();
	
	@Autowired
	private Environment environment;
	
	private SocialConfigurer socialConfigurer;

	@Autowired
	public void setSocialConfigurer(SocialConfigurer socialConfigurer) {
		Assert.notNull(socialConfigurer, "One configuration class must implement SocialConfigurer (or subclass SocialConfigurerAdapter)");
		this.socialConfigurer = socialConfigurer;
	}

	@Bean
	public ConnectionFactoryLocator connectionFactoryLocator() {
		if (securityEnabled) {
			SecurityEnabledConnectionFactoryConfigurer cfConfig = new SecurityEnabledConnectionFactoryConfigurer();
			socialConfigurer.addConnectionFactories(cfConfig, environment);
			return cfConfig.getConnectionFactoryLocator();
		} else {
			DefaultConnectionFactoryConfigurer cfConfig = new DefaultConnectionFactoryConfigurer();
			socialConfigurer.addConnectionFactories(cfConfig, environment);
			return cfConfig.getConnectionFactoryLocator();
		}
	}
	
	@Bean
	public UserIdSource userIdSource() {
		return socialConfigurer.getUserIdSource();
	}
	
	@Bean
	public UsersConnectionRepository usersConnectionRepository() {
		return socialConfigurer.getUsersConnectionRepository(connectionFactoryLocator());
	}

	@Bean
	@Scope(value="request", proxyMode=ScopedProxyMode.INTERFACES)
	public ConnectionRepository connectionRepository() {
		return usersConnectionRepository().createConnectionRepository(userIdSource().getUserId());
	}

	private static boolean isSocialSecurityAvailable() {
		try {
			Class.forName("org.springframework.social.security.SocialAuthenticationServiceLocator");
			return true;
		} catch (ClassNotFoundException cnfe) {
			return false; 
		}
	}

}
