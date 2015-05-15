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

/**
 * Strategy interface for registering connection factories.
 * Given to configuration in call to {@link SocialConfigurer#addConnectionFactories(ConnectionFactoryConfigurer, org.springframework.core.env.Environment)}.
 * There are currently two implementations.
 * The default implementation simply registers the given ConnectionFactory with a ConnectionFactoryRegistry.
 * If Spring Social's security module is available on the classpath, the implementation given will work with a SocialAuthenticationServiceRegistry and
 * automatically wrap any given ConnectionFactory with a SocialAuthenticationService.
 * @author Craig Walls
 */
public interface ConnectionFactoryConfigurer {
	
	/**
	 * Add a connection factory registry.
	 * If Spring Social's security module is available, the given connection factory will be wrapped as a SocialAuthenticationService.
	 * @param connectionFactory the ConnectionFactory to register
	 */
	void addConnectionFactory(ConnectionFactory<?> connectionFactory);

}
