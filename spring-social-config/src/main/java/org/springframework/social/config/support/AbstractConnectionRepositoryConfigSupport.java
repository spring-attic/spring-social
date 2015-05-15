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
import org.springframework.aop.scope.ScopedProxyUtils;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;

public abstract class AbstractConnectionRepositoryConfigSupport {

	private final static Log logger = LogFactory.getLog(AbstractConnectionRepositoryConfigSupport.class);

	protected BeanDefinition registerConnectionRepository(BeanDefinitionRegistry registry, String usersConnectionRepositoryId, String connectionRepositoryId, String userIdSourceRef) {
		if (logger.isDebugEnabled()) {
			logger.debug("Registering ConnectionRepository bean");
		}		
		// TODO: Hackish use of SpEL to reference userIdSource
		BeanDefinition connectionRepositoryBD = BeanDefinitionBuilder.genericBeanDefinition().addConstructorArgValue("#{" + userIdSourceRef + ".userId}").getBeanDefinition();
		connectionRepositoryBD.setFactoryBeanName(usersConnectionRepositoryId);
		connectionRepositoryBD.setFactoryMethodName(CREATE_CONNECTION_REPOSITORY_METHOD_NAME);
		connectionRepositoryBD.setScope("request");
		registry.registerBeanDefinition(connectionRepositoryId, decorateWithScopedProxy(connectionRepositoryId, connectionRepositoryBD, registry));
		return connectionRepositoryBD;
	}

	protected BeanDefinition decorateWithScopedProxy(String beanName, BeanDefinition beanDefinition, BeanDefinitionRegistry registry) {
		BeanDefinitionHolder beanDefinitionHolder = new BeanDefinitionHolder(beanDefinition, beanName);
		return ScopedProxyUtils.createScopedProxy(beanDefinitionHolder, registry, false).getBeanDefinition();
	}

	private static final String CREATE_CONNECTION_REPOSITORY_METHOD_NAME = "createConnectionRepository";
}
