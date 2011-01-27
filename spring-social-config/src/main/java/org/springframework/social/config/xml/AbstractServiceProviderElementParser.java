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
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

public abstract class AbstractServiceProviderElementParser implements BeanDefinitionParser {
	private static final String CONNECTION_REPOSITORY_ATTRIBUTE = "connection-repository";
	private static final String DEFAULT_CONNECTION_REPOSITORY_BEAN_NAME = "connectionRepository";


	public BeanDefinition parse(Element element, ParserContext parserContext) {
		String name = element.getAttribute("id");
		if (name == null || name.isEmpty()) {
			name = getDefaultProviderName();
		}

		return registerServiceProviderBean(element, parserContext, name);
	}

	protected BeanDefinition registerServiceProviderBean(Element element, ParserContext parserContext, String name) {

		String consumerKey = element.getAttribute("consumer-key");
		String consumerSecret = element.getAttribute("consumer-secret");
		String connectionRepositoryBean = getConnectionRepositoryName(element);

		BeanDefinitionBuilder providerBeanBuilder = BeanDefinitionBuilder
				.genericBeanDefinition(getImplementationClassName());
		providerBeanBuilder.addConstructorArgValue(consumerKey);
		providerBeanBuilder.addConstructorArgValue(consumerSecret);
		providerBeanBuilder.addConstructorArgReference(connectionRepositoryBean);

		BeanDefinition providerBeanDefinition = providerBeanBuilder.getBeanDefinition();
		parserContext.getRegistry().registerBeanDefinition(name, providerBeanDefinition);
		return providerBeanDefinition;
	}

	String getConnectionRepositoryName(Element element) {
		return (element.hasAttribute(CONNECTION_REPOSITORY_ATTRIBUTE) ? element
				.getAttribute(CONNECTION_REPOSITORY_ATTRIBUTE) : DEFAULT_CONNECTION_REPOSITORY_BEAN_NAME);
	}

	abstract protected String getImplementationClassName();

	abstract protected String getDefaultProviderName();
}
