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

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.parsing.BeanComponentDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.social.connect.jdbc.JdbcUsersConnectionRepository;
import org.w3c.dom.Element;

public class JdbcConnectionRepositoryBeanDefinitionParser implements BeanDefinitionParser {

	private static final String CREATE_CONNECTION_REPOSITORY = "createConnectionRepository";
	private static final String USERS_CONNECTION_REPOSITORY_ID = "usersConnectionRepository";
	private static final String CONNECTION_REPOSITORY_ID = "connectionRepository";
	private static final String USER_ID_STRING_ID = "_userIdString";
	
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
		parserContext.registerBeanComponent(new BeanComponentDefinition(usersConnectionRepositoryBD, USERS_CONNECTION_REPOSITORY_ID));
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
		BeanDefinition connectionRepositoryDB = BeanDefinitionBuilder.genericBeanDefinition().addConstructorArgValue(USER_ID_STRING_ID).getBeanDefinition();
		connectionRepositoryDB.setFactoryBeanName(USERS_CONNECTION_REPOSITORY_ID);
		connectionRepositoryDB.setFactoryMethodName(CREATE_CONNECTION_REPOSITORY);
		connectionRepositoryDB.setScope("request");
		// TODO: Set scoped proxy on this somehow
		parserContext.registerBeanComponent(new BeanComponentDefinition(connectionRepositoryDB, CONNECTION_REPOSITORY_ID));
		return connectionRepositoryDB;
	}

}
