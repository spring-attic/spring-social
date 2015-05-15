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
package org.springframework.social.config.support;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.social.connect.mem.InMemoryUsersConnectionRepository;

/**
 * Support class providing methods for configuring a singleton {@link InMemoryUsersConnectionRepository} bean and a request-scoped InMemoryConnectionRepository bean.
 * Used by InMemoryConnectionRepositoryBeanDefinitionParser for XML configuration.
 * @author Craig Walls
 */
public abstract class InMemoryConnectionRepositoryConfigSupport extends AbstractConnectionRepositoryConfigSupport {

	private final static Log logger = LogFactory.getLog(InMemoryConnectionRepositoryConfigSupport.class);

	public BeanDefinition registerInMemoryConnectionRepositoryBeans(BeanDefinitionRegistry registry, String connectionRepositoryId, String usersConnectionRepositoryId, 
			String connectionFactoryLocatorRef, String userIdSourceRef, String connectionSignUpRef) {
		registerUsersConnectionRepositoryBeanDefinition(registry, usersConnectionRepositoryId, connectionFactoryLocatorRef, connectionSignUpRef);
		return registerConnectionRepository(registry, usersConnectionRepositoryId, connectionRepositoryId, userIdSourceRef);		
	}
	
	
	private BeanDefinition registerUsersConnectionRepositoryBeanDefinition(BeanDefinitionRegistry registry, String usersConnectionRepositoryId, 
			String connectionFactoryLocatorRef, String connectionSignUpRef) {
		if (logger.isDebugEnabled()) {
			logger.debug("Registering InMemoryUsersConnectionRepository bean");
		}				
		BeanDefinitionBuilder usersConnectionRepositoryBeanBuilder = BeanDefinitionBuilder.genericBeanDefinition(InMemoryUsersConnectionRepository.class)
				.addConstructorArgReference(connectionFactoryLocatorRef);
		if (connectionSignUpRef != null && connectionSignUpRef.length() > 0) {
			usersConnectionRepositoryBeanBuilder.addPropertyReference("connectionSignUp", connectionSignUpRef);
		}
		BeanDefinition usersConnectionRepositoryBD = usersConnectionRepositoryBeanBuilder.getBeanDefinition();
		BeanDefinition scopedProxyBean = decorateWithScopedProxy(usersConnectionRepositoryId, usersConnectionRepositoryBD, registry);
		registry.registerBeanDefinition(usersConnectionRepositoryId, scopedProxyBean);
		return scopedProxyBean;
	}

}
