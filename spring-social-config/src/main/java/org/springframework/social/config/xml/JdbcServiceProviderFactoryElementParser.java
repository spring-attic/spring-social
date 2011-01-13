/*
 * Copyright 2011 the original author or authors.
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
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.security.encrypt.NoOpStringEncryptor;
import org.springframework.social.connect.jdbc.JdbcServiceProviderFactory;
import org.w3c.dom.Element;

public class JdbcServiceProviderFactoryElementParser implements BeanDefinitionParser {
	public BeanDefinition parse(Element element, ParserContext parserContext) {
		BeanDefinitionBuilder beanBuilder = BeanDefinitionBuilder
				.genericBeanDefinition(JdbcServiceProviderFactory.class);

		String jdbcTemplate = element.getAttribute("jdbc-template");
		beanBuilder.addConstructorArgReference(jdbcTemplate);

		String stringEncryptor = element.getAttribute("string-encryptor");
		if (stringEncryptor != null && !stringEncryptor.isEmpty()) {
			beanBuilder.addConstructorArgReference(stringEncryptor);
		} else {
			beanBuilder.addConstructorArgValue(NoOpStringEncryptor.getInstance());
		}

		AbstractBeanDefinition beanDefinition = beanBuilder.getBeanDefinition();
		parserContext.getRegistry().registerBeanDefinition("serviceProviderFactory", beanDefinition);

		return beanDefinition;
	}
}
