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
package org.springframework.social.config.support;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.aop.scope.ScopedProxyUtils;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.social.connect.jdbc.JdbcUsersConnectionRepository;

/**
 * Support class providing methods for configuring a singleton {@link JdbcUsersConnectionRepository} bean and a request-scoped JdbcConnectionRepository bean.
 * Used by JdbcConnectionRepositoryRegistrar (for EnableJdbcConnectionRepository) and JdbcConnectionRepositoryBeanDefinitionParser for XML configuration.
 * @author Craig Walls
 */
public abstract class JdbcConnectionRepositoryConfigSupport {

	private final static Log logger = LogFactory.getLog(JdbcConnectionRepositoryConfigSupport.class);

	public BeanDefinition registerJdbcConnectionRepositoryBeans(BeanDefinitionRegistry registry, String connectionRepositoryId, String usersConnectionRepositoryId, 
			String connectionFactoryLocatorRef, String dataSourceRef, String encryptorRef, String userIdSourceRef, String connectionSignUpRef) {
		registerUsersConnectionRepositoryBeanDefinition(registry, usersConnectionRepositoryId, connectionFactoryLocatorRef, dataSourceRef, encryptorRef, connectionSignUpRef);
		registerUserIdBeanDefinition(registry, userIdSourceRef);
		return registerConnectionRepository(registry, usersConnectionRepositoryId, connectionRepositoryId);		
	}
	
	
	private BeanDefinition registerUsersConnectionRepositoryBeanDefinition(BeanDefinitionRegistry registry, String usersConnectionRepositoryId, 
			String connectionFactoryLocatorRef, String dataSourceRef, String encryptorRef, String connectionSignUpRef) {
		if (logger.isDebugEnabled()) {
			logger.debug("Registering JdbcUsersConnectionRepository bean");
		}				
		BeanDefinitionBuilder usersConnectionRepositoryBeanBuilder = BeanDefinitionBuilder.genericBeanDefinition(JdbcUsersConnectionRepository.class)
				.addConstructorArgReference(dataSourceRef)
				.addConstructorArgReference(connectionFactoryLocatorRef)
				.addConstructorArgReference(encryptorRef);
		if (connectionSignUpRef != null && connectionSignUpRef.length() > 0) {
			usersConnectionRepositoryBeanBuilder.addPropertyReference("connectionSignUp", connectionSignUpRef);
		}
		BeanDefinition usersConnectionRepositoryBD = usersConnectionRepositoryBeanBuilder.getBeanDefinition();
		BeanDefinition scopedProxyBean = decorateWithScopedProxy(usersConnectionRepositoryId, usersConnectionRepositoryBD, registry);
		registry.registerBeanDefinition(usersConnectionRepositoryId, scopedProxyBean);
		return scopedProxyBean;
	}
	
	// TODO: Kinda hackish...pushes a request-scoped String containing the name retrieved from the UserIdSource into the context.
	private BeanDefinition registerUserIdBeanDefinition(BeanDefinitionRegistry registry, String userIdSourceRef) {
		BeanDefinition userIdStringDB = BeanDefinitionBuilder.genericBeanDefinition().getBeanDefinition();
		userIdStringDB.setFactoryBeanName(userIdSourceRef);
		userIdStringDB.setFactoryMethodName("getUserId");
		userIdStringDB.setScope("request");
		registry.registerBeanDefinition(USER_ID_STRING_ID, userIdStringDB);
		return userIdStringDB;
	}

	private BeanDefinition registerConnectionRepository(BeanDefinitionRegistry registry, String usersConnectionRepositoryId, String connectionRepositoryId) {
		if (logger.isDebugEnabled()) {
			logger.debug("Registering JdbcConnectionRepository bean");
		}		
		BeanDefinition connectionRepositoryBD = BeanDefinitionBuilder.genericBeanDefinition().addConstructorArgReference(USER_ID_STRING_ID).getBeanDefinition();
		connectionRepositoryBD.setFactoryBeanName(usersConnectionRepositoryId);
		connectionRepositoryBD.setFactoryMethodName(CREATE_CONNECTION_REPOSITORY_METHOD_NAME);
		connectionRepositoryBD.setScope("request");
		registry.registerBeanDefinition(connectionRepositoryId, decorateWithScopedProxy(connectionRepositoryId, connectionRepositoryBD, registry));
		return connectionRepositoryBD;
	}

	private BeanDefinition decorateWithScopedProxy(String beanName, BeanDefinition beanDefinition, BeanDefinitionRegistry registry) {
		BeanDefinitionHolder beanDefinitionHolder = new BeanDefinitionHolder(beanDefinition, beanName);
		return ScopedProxyUtils.createScopedProxy(beanDefinitionHolder, registry, false).getBeanDefinition();
	}

	private static final String USER_ID_STRING_ID = "__userIdString";

	private static final String CREATE_CONNECTION_REPOSITORY_METHOD_NAME = "createConnectionRepository";

}
