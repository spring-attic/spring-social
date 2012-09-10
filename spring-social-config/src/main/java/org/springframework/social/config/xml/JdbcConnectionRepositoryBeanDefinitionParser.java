/*
 * Copyright 2012 the original author or authors.
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
package org.springframework.social.config.xml;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.aop.scope.ScopedProxyUtils;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.parsing.BeanComponentDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.social.connect.ConnectionRepository;
import org.springframework.social.connect.UsersConnectionRepository;
import org.springframework.social.connect.jdbc.JdbcUsersConnectionRepository;
import org.w3c.dom.Element;

/**
 * {@link BeanDefinitionParser} for creating {@link UsersConnectionRepository} (specifically, {@link JdbcUsersConnectionRepository}) and {@link ConnectionRepository} beans that use JDBC as the persistence mechanism.
 * @author Craig Walls
 */
class JdbcConnectionRepositoryBeanDefinitionParser implements BeanDefinitionParser {

	private final static Log logger = LogFactory.getLog(JdbcConnectionRepositoryBeanDefinitionParser.class);

	public BeanDefinition parse(Element element, ParserContext parserContext) {		
		String connectionRepositoryId = element.getAttribute("connection-repository-id");
		String usersConnectionRepositoryId = element.getAttribute("users-connection-repository-id");
		String connectionFactoryLocatorRef = element.getAttribute("connection-factory-locator-ref");
		String dataSourceRef = element.getAttribute("data-source-ref");
		String encryptorRef = element.getAttribute("encryptor-ref");
		String userIdSourceRef = element.getAttribute("user-id-source-ref");
		
		registerUsersConnectionRepositoryBeanDefinition(parserContext, usersConnectionRepositoryId, connectionFactoryLocatorRef, dataSourceRef, encryptorRef);
		registerUserIdBeanDefinition(parserContext, userIdSourceRef);
		return registerConnectionRepository(parserContext, usersConnectionRepositoryId, connectionRepositoryId);
	}

	private void registerUsersConnectionRepositoryBeanDefinition(ParserContext parserContext, String usersConnectionRepositoryId, String connectionFactoryLocatorRef, String dataSourceRef, String encryptorRef) {
		if (logger.isDebugEnabled()) {
			logger.debug("Registering JdbcUsersConnectionRepository bean");
		}		
		BeanDefinition usersConnectionRepositoryBD = BeanDefinitionBuilder.genericBeanDefinition(JdbcUsersConnectionRepository.class)
				.addConstructorArgReference(dataSourceRef)
				.addConstructorArgReference(connectionFactoryLocatorRef)
				.addConstructorArgReference(encryptorRef)
				.getBeanDefinition();
		parserContext.getRegistry().registerBeanDefinition(usersConnectionRepositoryId, decorateWithScopedProxy(usersConnectionRepositoryId, usersConnectionRepositoryBD, parserContext));
	}
	
	// TODO: Kinda hackish...pushes a request-scoped String containing the name retrieved from the UserIdSource into the context.
	private BeanDefinition registerUserIdBeanDefinition(ParserContext parserContext, String userIdSourceRef) {
		BeanDefinition userIdStringDB = BeanDefinitionBuilder.genericBeanDefinition().getBeanDefinition();
		userIdStringDB.setFactoryBeanName(userIdSourceRef);
		userIdStringDB.setFactoryMethodName("getUserId");
		userIdStringDB.setScope("request");
		parserContext.registerBeanComponent(new BeanComponentDefinition(userIdStringDB, USER_ID_STRING_ID));
		return userIdStringDB;
	}
	
	private BeanDefinition registerConnectionRepository(ParserContext parserContext, String usersConnectionRepositoryId, String connectionRepositoryId) {
		if (logger.isDebugEnabled()) {
			logger.debug("Registering JdbcConnectionRepository bean");
		}		
		BeanDefinition connectionRepositoryBD = BeanDefinitionBuilder.genericBeanDefinition().addConstructorArgReference(USER_ID_STRING_ID).getBeanDefinition();
		connectionRepositoryBD.setFactoryBeanName(usersConnectionRepositoryId);
		connectionRepositoryBD.setFactoryMethodName(CREATE_CONNECTION_REPOSITORY_METHOD_NAME);
		connectionRepositoryBD.setScope("request");
		parserContext.getRegistry().registerBeanDefinition(connectionRepositoryId, decorateWithScopedProxy(connectionRepositoryId, connectionRepositoryBD, parserContext));
		return connectionRepositoryBD;
	}

	private BeanDefinition decorateWithScopedProxy(String beanName, BeanDefinition beanDefinition, ParserContext parserContext) {
		BeanDefinitionHolder beanDefinitionHolder = new BeanDefinitionHolder(beanDefinition, beanName);
		return ScopedProxyUtils.createScopedProxy(beanDefinitionHolder, parserContext.getRegistry(), false).getBeanDefinition();
	}

	private static final String CREATE_CONNECTION_REPOSITORY_METHOD_NAME = "createConnectionRepository";

	private static final String USER_ID_STRING_ID = "__userIdString";
	
}
