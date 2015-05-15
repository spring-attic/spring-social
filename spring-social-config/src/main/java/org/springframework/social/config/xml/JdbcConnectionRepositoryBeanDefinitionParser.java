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
package org.springframework.social.config.xml;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.social.config.support.JdbcConnectionRepositoryConfigSupport;
import org.springframework.social.connect.ConnectionRepository;
import org.springframework.social.connect.UsersConnectionRepository;
import org.springframework.social.connect.jdbc.JdbcUsersConnectionRepository;
import org.w3c.dom.Element;

/**
 * {@link BeanDefinitionParser} for creating {@link UsersConnectionRepository} (specifically, {@link JdbcUsersConnectionRepository}) and {@link ConnectionRepository} beans that use JDBC as the persistence mechanism.
 * @author Craig Walls
 */
class JdbcConnectionRepositoryBeanDefinitionParser extends JdbcConnectionRepositoryConfigSupport implements BeanDefinitionParser {

	public BeanDefinition parse(Element element, ParserContext parserContext) {		
		String connectionRepositoryId = element.getAttribute("connection-repository-id");
		String usersConnectionRepositoryId = element.getAttribute("users-connection-repository-id");
		String connectionFactoryLocatorRef = element.getAttribute("connection-factory-locator-ref");
		String dataSourceRef = element.getAttribute("data-source-ref");
		String encryptorRef = element.getAttribute("encryptor-ref");
		String userIdSourceRef = element.getAttribute("user-id-source-ref");
		String connectionSignUpRef = element.getAttribute("connection-signup-ref");
		return registerJdbcConnectionRepositoryBeans(parserContext.getRegistry(), connectionRepositoryId, usersConnectionRepositoryId, connectionFactoryLocatorRef, dataSourceRef, encryptorRef, userIdSourceRef, connectionSignUpRef);
	}
	
}
