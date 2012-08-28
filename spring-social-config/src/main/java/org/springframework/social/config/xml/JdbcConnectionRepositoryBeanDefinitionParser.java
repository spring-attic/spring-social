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

	public BeanDefinition parse(Element element, ParserContext parserContext) {		
		String connectionFactoryLocatorRef = element.getAttribute("connection-factory-locator-ref");
		String dataSourceRef = element.getAttribute("data-source-ref");
		String encryptorRef = element.getAttribute("encryptor-ref");
		String userIdSourceRef = element.getAttribute("user-id-source-ref");
		
		BeanDefinition usersConnectionRepositoryBD = registerUsersConnectionRepositoryBeanDefinition(parserContext, connectionFactoryLocatorRef, dataSourceRef, encryptorRef);
		registerUserIdBeanDefinition(parserContext, userIdSourceRef);
		BeanDefinition connectionRepositoryDB = registerConnectionRepository(parserContext, usersConnectionRepositoryBD);

		return connectionRepositoryDB;
	}

	private BeanDefinition registerUsersConnectionRepositoryBeanDefinition(ParserContext parserContext, String connectionFactoryLocatorRef, String dataSourceRef, String encryptorRef) {
		BeanDefinition usersConnectionRepositoryBD = BeanDefinitionBuilder.genericBeanDefinition(JdbcUsersConnectionRepository.class)
				.addConstructorArgReference(dataSourceRef)
				.addConstructorArgReference(connectionFactoryLocatorRef)
				.addConstructorArgReference(encryptorRef)
				.getBeanDefinition();
		parserContext.getRegistry().registerBeanDefinition(USERS_CONNECTION_REPOSITORY_ID, decorateWithScopedProxy(USERS_CONNECTION_REPOSITORY_ID, usersConnectionRepositoryBD, parserContext));
		return usersConnectionRepositoryBD;
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
	
	private BeanDefinition registerConnectionRepository(ParserContext parserContext, BeanDefinition usersConnectionRepositoryBD) {
		BeanDefinition connectionRepositoryBD = BeanDefinitionBuilder.genericBeanDefinition().addConstructorArgValue(USER_ID_STRING_ID).getBeanDefinition();
		connectionRepositoryBD.setFactoryBeanName(USERS_CONNECTION_REPOSITORY_ID);
		connectionRepositoryBD.setFactoryMethodName(CREATE_CONNECTION_REPOSITORY);
		connectionRepositoryBD.setScope("request");
		parserContext.getRegistry().registerBeanDefinition(CONNECTION_REPOSITORY_ID, decorateWithScopedProxy(CONNECTION_REPOSITORY_ID, connectionRepositoryBD, parserContext));
		return connectionRepositoryBD;
	}

	private BeanDefinition decorateWithScopedProxy(String beanName, BeanDefinition beanDefinition, ParserContext parserContext) {
		BeanDefinitionHolder bdHolder = new BeanDefinitionHolder(beanDefinition, beanName + "_target");
		BeanDefinitionHolder scopedProxyHolder = ScopedProxyUtils.createScopedProxy(bdHolder, parserContext.getRegistry(), false);
		parserContext.registerBeanComponent(new BeanComponentDefinition(bdHolder));
		return scopedProxyHolder.getBeanDefinition();
	}

	private static final String CREATE_CONNECTION_REPOSITORY = "createConnectionRepository";

	private static final String USERS_CONNECTION_REPOSITORY_ID = "usersConnectionRepository";
	
	private static final String CONNECTION_REPOSITORY_ID = "connectionRepository";
	
	private static final String USER_ID_STRING_ID = "_userIdString";
	
}
