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

class ServiceProviderElementParser implements BeanDefinitionParser {
	
	public BeanDefinition parse(Element element, ParserContext parserContext) {
		String beanId = element.getAttribute("id");
		String providerClassName = element.getAttribute("class");
		String clientKey = element.getAttribute("app-key");
		String clientSecret = element.getAttribute("app-secret");
		String connectionRepository = element.getAttribute("connection-repository");

		BeanDefinitionBuilder providerBeanBuilder = BeanDefinitionBuilder.genericBeanDefinition(providerClassName);
		providerBeanBuilder.addConstructorArgValue(clientKey);
		providerBeanBuilder.addConstructorArgValue(clientSecret);
		providerBeanBuilder.addConstructorArgReference(connectionRepository);

		BeanDefinition providerBeanDefinition = providerBeanBuilder.getBeanDefinition();
		if(beanId == null || beanId.isEmpty()) {
			beanId = parserContext.getReaderContext().generateBeanName(providerBeanDefinition);
		}
		parserContext.getRegistry().registerBeanDefinition(beanId, providerBeanDefinition);
		return providerBeanDefinition;
	}

}
