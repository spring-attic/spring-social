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

import java.util.ArrayList;
import java.util.List;

import org.springframework.aop.scope.ScopedProxyUtils;
import org.springframework.beans.PropertyValue;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.parsing.BeanComponentDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.social.connect.ConnectionFactory;
import org.springframework.social.connect.support.ConnectionFactoryRegistry;
import org.w3c.dom.Element;

public abstract class AbstractConnectionFactoryBeanDefinitionParser implements BeanDefinitionParser {

	private static final String CONNECTION_FACTORY_LOCATOR_ID = "connectionFactoryLocator";

	private static final String APP_ID = "app-id";

	private static final String APP_SECRET = "app-secret";

	private static final String CONNECTION_FACTORIES = "connectionFactories";

	public final BeanDefinition parse(Element element, ParserContext parserContext) {
		BeanDefinition connectionFactoryLocatorBD = getConnectionFactoryLocatorBeanDefinition(parserContext);
		
		// BIG TODO: Can't pass in property placeholders for app-id or app-secret because the connection factory itself is never
		//           defined via a BeanDefinition and thus, Spring has no opportunity to perform the replacement of the placeholders.
		//           Need to create the CF as a bean definition and then register it with the CFL.
		
		ConnectionFactory<?> cf = getConnectionFactory(element.getAttribute(APP_ID), element.getAttribute(APP_SECRET));
		addConnectionFactory(connectionFactoryLocatorBD, cf);
		return connectionFactoryLocatorBD;
	}

	protected abstract ConnectionFactory<?> getConnectionFactory(String appId, String appSecret);

	private void addConnectionFactory(BeanDefinition connectionFactoryLocatorBD, ConnectionFactory<?> connectionFactory) {
		PropertyValue connectionFactoriesPropertyValue = connectionFactoryLocatorBD.getPropertyValues().getPropertyValue(CONNECTION_FACTORIES);
		@SuppressWarnings("unchecked")
		List<ConnectionFactory<?>> connectionFactoriesList = connectionFactoriesPropertyValue != null ? 
				(List<ConnectionFactory<?>>) connectionFactoriesPropertyValue.getValue() : new ArrayList<ConnectionFactory<?>>();
		connectionFactoriesList.add(connectionFactory);
		connectionFactoryLocatorBD.getPropertyValues().addPropertyValue(CONNECTION_FACTORIES, connectionFactoriesList);
}

	private BeanDefinition getConnectionFactoryLocatorBeanDefinition(ParserContext parserContext) {
		if (!parserContext.getRegistry().containsBeanDefinition(CONNECTION_FACTORY_LOCATOR_ID)) {		
			BeanDefinition connFactoryLocatorBeanDef = BeanDefinitionBuilder.genericBeanDefinition(ConnectionFactoryRegistry.class).getBeanDefinition();
			parserContext.getRegistry().registerBeanDefinition(CONNECTION_FACTORY_LOCATOR_ID, decorateWithScopedProxy(CONNECTION_FACTORY_LOCATOR_ID, connFactoryLocatorBeanDef, parserContext));
		}		

		BeanDefinition connectionFactoryLocatorBD = parserContext.getRegistry().getBeanDefinition(CONNECTION_FACTORY_LOCATOR_ID + "_target");
		return connectionFactoryLocatorBD;
	}
	
	private BeanDefinition decorateWithScopedProxy(String beanName, BeanDefinition beanDefinition, ParserContext parserContext) {
		BeanDefinitionHolder bdHolder = new BeanDefinitionHolder(beanDefinition, beanName + "_target");
		BeanDefinitionHolder scopedProxyHolder = ScopedProxyUtils.createScopedProxy(bdHolder, parserContext.getRegistry(), false);
		parserContext.registerBeanComponent(new BeanComponentDefinition(bdHolder));
		return scopedProxyHolder.getBeanDefinition();
	}
}
