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
import org.springframework.social.connect.ConnectionFactoryLocator;
import org.springframework.social.connect.UsersConnectionRepository;
import org.springframework.social.connect.mem.InMemoryUsersConnectionRepository;

/**
 * Abstract implementation of {@link SocialConfigurer} with convenient default implementations of methods.
 * @author Craig Walls
 */
public abstract class SocialConfigurerAdapter implements SocialConfigurer {

	/**
	 * Default implementation of {@link #addConnectionFactories(ConnectionFactoryConfigurer, Environment)}.
	 * Implemented as a no-op, adding no connection factories.
	 */
	public void addConnectionFactories(ConnectionFactoryConfigurer connectionFactoryConfigurer, Environment environment) {
	}
	
	/**
	 * Default implementation of {@link #getUserIdSource()}.
	 * Returns null, indicating that this configuration class doesn't provide a UserIdSource (another configuration class must provide one, however).
	 * @return null
	 */
	public UserIdSource getUserIdSource() {
		return null;
	}
	
	/**
	 * Default implementation of {@link #getUsersConnectionRepository(ConnectionFactoryLocator)} that creates an in-memory repository.
	 */
	public UsersConnectionRepository getUsersConnectionRepository(ConnectionFactoryLocator connectionFactoryLocator) {
		return new InMemoryUsersConnectionRepository(connectionFactoryLocator);
	}

}
