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
import org.springframework.social.connect.ServiceProviderParameters;

public abstract class AbstractServiceProviderElementParser implements BeanDefinitionParser {
	protected BeanDefinition registerServiceProviderBean(ParserContext parserContext, String name, String className,
			String displayName, String consumerKey, String consumerSecret, Long appId, String requestTokenUrl,
			String authorizeUrl, String accessTokenUrl, String connectionRepositoryBean) {
		BeanDefinitionBuilder providerBeanBuilder = BeanDefinitionBuilder.genericBeanDefinition(className);
		ServiceProviderParameters providerParameters = new ServiceProviderParameters(name, displayName, consumerKey,
				consumerSecret, appId, requestTokenUrl, authorizeUrl, accessTokenUrl);
		providerBeanBuilder.addConstructorArgValue(providerParameters);
		if (connectionRepositoryBean != null && !connectionRepositoryBean.isEmpty()) {
			providerBeanBuilder.addConstructorArgReference(connectionRepositoryBean);
		} else {
			providerBeanBuilder.setAutowireMode(AbstractBeanDefinition.AUTOWIRE_CONSTRUCTOR);
		}

		BeanDefinition providerBeanDefinition = providerBeanBuilder.getBeanDefinition();
		parserContext.getRegistry().registerBeanDefinition(name, providerBeanDefinition);
		return providerBeanDefinition;
	}
}
