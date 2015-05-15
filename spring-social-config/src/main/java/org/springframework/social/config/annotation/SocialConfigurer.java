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

import org.springframework.core.env.Environment;
import org.springframework.social.UserIdSource;
import org.springframework.social.connect.ConnectionFactory;
import org.springframework.social.connect.ConnectionFactoryLocator;
import org.springframework.social.connect.ConnectionRepository;
import org.springframework.social.connect.UsersConnectionRepository;

/**
 * Defines callback methods to customize Java-based configuration enabled by {@code @EnableWebMvc}.
 * {@code @EnableWebMvc}-annotated classes may implement this interface or extend {@link SocialConfigurerAdapter},
 * which provides some default configuration.
 * @author Craig Walls
 */
public interface SocialConfigurer {

	/**
	 * Callback method to allow configuration of {@link ConnectionFactory}s.
	 * @param connectionFactoryConfigurer A configurer for adding {@link ConnectionFactory} instances.
	 * @param environment The Spring environment, useful for fetching application credentials needed to create a {@link ConnectionFactory} instance.
	 */
	void addConnectionFactories(ConnectionFactoryConfigurer connectionFactoryConfigurer, Environment environment);
	
	/**
	 * Callback method to enable creation of a {@link UserIdSource} that uniquely identifies the current user.
	 * @return the {@link UserIdSource}.
	 */
	UserIdSource getUserIdSource();
	
	/**
	 * Callback method to create an instance of {@link UsersConnectionRepository}. 
	 * Will be used to create a request-scoped instance of {@link ConnectionRepository} for the current user.
	 * @param connectionFactoryLocator A {@link ConnectionFactoryLocator} to be used by the {@link UsersConnectionRepository}.
	 * @return An instance of {@link UsersConnectionRepository}.
	 */
	UsersConnectionRepository getUsersConnectionRepository(ConnectionFactoryLocator connectionFactoryLocator);

}
