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
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.parsing.BeanComponentDefinition;
import org.springframework.beans.factory.xml.ParserContext;

/**
 * Convenience utility class for creating a scoped proxy for a given bean definition.
 * @author Craig Walls
 */
class ScopedProxyUtils {
	
	/**
	 * Creates a scoped proxy for the given {@link BeanDefinition} in the {@link BeanDefinitionHolder} and adds it to the {@link ParserContext}'s registry.
	 * @param parserContext The parser context to register the scoped proxy in.
	 * @param beanDefinitionHolder A {@link BeanDefinitionHolder} carrying the {@link BeanDefinition} and name of the bean to proxy.
	 * @param proxyTargetClass 
	 * @return the BeanDefinition for the scoped proxy
	 */
	public static BeanDefinition decorateWithScopedProxy(ParserContext parserContext, BeanDefinitionHolder beanDefinitionHolder, boolean proxyTargetClass) {
		BeanDefinitionHolder scopedProxyHolder = org.springframework.aop.scope.ScopedProxyUtils.createScopedProxy(beanDefinitionHolder, parserContext.getRegistry(), proxyTargetClass);
		parserContext.registerBeanComponent(new BeanComponentDefinition(beanDefinitionHolder));
		return scopedProxyHolder.getBeanDefinition();
	}
	
}
