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

import java.util.List;

import org.springframework.aop.scope.ScopedProxyUtils;
import org.springframework.beans.PropertyValue;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.ManagedList;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.social.connect.ConnectionFactory;
import org.springframework.social.connect.ConnectionFactoryLocator;
import org.springframework.social.connect.support.ConnectionFactoryRegistry;
import org.w3c.dom.Element;

/**
 * Abstract bean definition parser for declaring {@link ConnectionFactory}s in a Spring application context.
 * Automatically creates a {@link ConnectionFactoryLocator} bean if none exists and registers the {@link ConnectionFactory} bean with the {@link ConnectionFactoryLocator}.
 * @author Craig Walls
 */
abstract class AbstractConnectionFactoryBeanDefinitionParser implements BeanDefinitionParser {

	private Class<? extends ConnectionFactory<?>> connectionFactoryClass;

	/**
	 * Constructs a connection factory-creating {@link BeanDefinitionParser}.
	 * @param connectionFactoryClass The type of {@link ConnectionFactory} to create. Must have a two-argument constructor taking an application's ID and secret as Strings.
	 */
	protected AbstractConnectionFactoryBeanDefinitionParser(Class<? extends ConnectionFactory<?>> connectionFactoryClass) {
		this.connectionFactoryClass = connectionFactoryClass;		
	}
	
	public final BeanDefinition parse(Element element, ParserContext parserContext) {
		BeanDefinition connectionFactoryLocatorBD = getConnectionFactoryLocatorBeanDefinition(parserContext);
		addConnectionFactory(connectionFactoryLocatorBD, element.getAttribute(APP_ID), element.getAttribute(APP_SECRET));
		return connectionFactoryLocatorBD;
	}

	private BeanDefinition getConnectionFactoryBeanDefinition(String appId, String appSecret) {
		return BeanDefinitionBuilder.genericBeanDefinition(connectionFactoryClass).addConstructorArgValue(appId).addConstructorArgValue(appSecret).getBeanDefinition();
	}

	private void addConnectionFactory(BeanDefinition connectionFactoryLocatorBD, String appId, String appSecret) {
		PropertyValue connectionFactoriesPropertyValue = connectionFactoryLocatorBD.getPropertyValues().getPropertyValue(CONNECTION_FACTORIES);
		@SuppressWarnings("unchecked")
		List<BeanDefinition> connectionFactoriesList = connectionFactoriesPropertyValue != null ? 
				(List<BeanDefinition>) connectionFactoriesPropertyValue.getValue() : new ManagedList<BeanDefinition>();
		connectionFactoriesList.add(getConnectionFactoryBeanDefinition(appId, appSecret));		
		connectionFactoryLocatorBD.getPropertyValues().addPropertyValue(CONNECTION_FACTORIES, connectionFactoriesList);
	}

	private BeanDefinition getConnectionFactoryLocatorBeanDefinition(ParserContext parserContext) {
		if (!parserContext.getRegistry().containsBeanDefinition(CONNECTION_FACTORY_LOCATOR_ID)) {		
			BeanDefinitionHolder connFactoryLocatorBeanDefHolder = new BeanDefinitionHolder(BeanDefinitionBuilder.genericBeanDefinition(ConnectionFactoryRegistry.class).getBeanDefinition(), CONNECTION_FACTORY_LOCATOR_ID);			
			BeanDefinitionHolder scopedProxy = ScopedProxyUtils.createScopedProxy(connFactoryLocatorBeanDefHolder, parserContext.getRegistry(), false);			
			parserContext.getRegistry().registerBeanDefinition(scopedProxy.getBeanName(), scopedProxy.getBeanDefinition());
		}		
		BeanDefinition connectionFactoryLocatorBD = parserContext.getRegistry().getBeanDefinition(ScopedProxyUtils.getTargetBeanName(CONNECTION_FACTORY_LOCATOR_ID));
		return connectionFactoryLocatorBD;
	}

	private static final String CONNECTION_FACTORY_LOCATOR_ID = "connectionFactoryLocator";

	private static final String APP_ID = "app-id";

	private static final String APP_SECRET = "app-secret";

	private static final String CONNECTION_FACTORIES = "connectionFactories";

}
