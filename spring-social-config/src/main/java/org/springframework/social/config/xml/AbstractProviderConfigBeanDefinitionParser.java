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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.aop.scope.ScopedProxyUtils;
import org.springframework.beans.PropertyValue;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.ManagedList;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.core.GenericTypeResolver;
import org.springframework.social.connect.ConnectionFactory;
import org.springframework.social.connect.ConnectionFactoryLocator;
import org.springframework.social.connect.support.ConnectionFactoryRegistry;
import org.springframework.util.ClassUtils;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;

/**
 * Abstract bean definition parser for configuring provider-specific beans in a Spring application context.
 * Automatically creates a {@link ConnectionFactoryLocator} bean if none exists and registers the {@link ConnectionFactory} bean with the {@link ConnectionFactoryLocator}.
 * Also creates a request-scoped API binding bean retrieved from the connection repository.
 * @author Craig Walls
 */
public abstract class AbstractProviderConfigBeanDefinitionParser implements BeanDefinitionParser {

	public final BeanDefinition parse(Element element, ParserContext parserContext) {
		BeanDefinition connectionFactoryLocatorBD = getConnectionFactoryLocatorBeanDefinition(parserContext);
		addConnectionFactory(connectionFactoryLocatorBD, element.getAttribute(APP_ID), element.getAttribute(APP_SECRET), element.getAttributes());
		BeanDefinition addApiBindingBean = addApiBindingBean(parserContext);
		return addApiBindingBean;
	}

	/**
	 * Constructs a connection factory-creating {@link BeanDefinitionParser}.
	 * @param connectionFactoryClass The type of {@link ConnectionFactory} to create. Must have a two-argument constructor taking an application's ID and secret as Strings.
	 */
	protected AbstractProviderConfigBeanDefinitionParser(Class<? extends ConnectionFactory<?>> connectionFactoryClass, Class<?> apiHelperClass) {
		this.connectionFactoryClass = connectionFactoryClass;
		this.apiHelperClass = apiHelperClass;
		this.apiBindingType = GenericTypeResolver.resolveTypeArgument(connectionFactoryClass, ConnectionFactory.class);
	}
	
	/**
	 * Creates a BeanDefinition for a provider connection factory.
	 * Although most providers will not need to override this method, it does allow for overriding to address any provider-specific needs.
	 * @param appId The application's App ID
	 * @param appSecret The application's App Secret
	 * @param allAttributes All attributes available on the configuration element. Useful for provider-specific configuration.
	 * @return a BeanDefinition for the provider's connection factory bean.
	 */
	protected BeanDefinition getConnectionFactoryBeanDefinition(String appId, String appSecret, NamedNodeMap allAttributes) {
		return BeanDefinitionBuilder.genericBeanDefinition(connectionFactoryClass).addConstructorArgValue(appId).addConstructorArgValue(appSecret).getBeanDefinition();
	}

	private void addConnectionFactory(BeanDefinition connectionFactoryLocatorBD, String appId, String appSecret, NamedNodeMap allAttributes) {
		if (logger.isDebugEnabled()) {
			logger.debug("Registering ConnectionFactory for " + ClassUtils.getShortName(apiBindingType));
		}		
		PropertyValue connectionFactoriesPropertyValue = connectionFactoryLocatorBD.getPropertyValues().getPropertyValue(CONNECTION_FACTORIES);
		@SuppressWarnings("unchecked")
		List<BeanDefinition> connectionFactoriesList = connectionFactoriesPropertyValue != null ? 
				(List<BeanDefinition>) connectionFactoriesPropertyValue.getValue() : new ManagedList<BeanDefinition>();
		connectionFactoriesList.add(getConnectionFactoryBeanDefinition(appId, appSecret, allAttributes));		
		connectionFactoryLocatorBD.getPropertyValues().addPropertyValue(CONNECTION_FACTORIES, connectionFactoriesList);
	}

	private BeanDefinition getConnectionFactoryLocatorBeanDefinition(ParserContext parserContext) {
		if (logger.isDebugEnabled()) {
			logger.debug("Registering ConnectionFactoryLocator bean");
		}		
		if (!parserContext.getRegistry().containsBeanDefinition(CONNECTION_FACTORY_LOCATOR_ID)) {		
			BeanDefinitionHolder connFactoryLocatorBeanDefHolder = new BeanDefinitionHolder(BeanDefinitionBuilder.genericBeanDefinition(ConnectionFactoryRegistry.class).getBeanDefinition(), CONNECTION_FACTORY_LOCATOR_ID);			
			BeanDefinitionHolder scopedProxy = ScopedProxyUtils.createScopedProxy(connFactoryLocatorBeanDefHolder, parserContext.getRegistry(), false);			
			parserContext.getRegistry().registerBeanDefinition(scopedProxy.getBeanName(), scopedProxy.getBeanDefinition());
		}
		BeanDefinition connectionFactoryLocatorBD = parserContext.getRegistry().getBeanDefinition(ScopedProxyUtils.getTargetBeanName(CONNECTION_FACTORY_LOCATOR_ID));
		return connectionFactoryLocatorBD;
	}
	
	private BeanDefinition addApiBindingBean(ParserContext parserContext) {
		if (logger.isDebugEnabled()) {
			logger.debug("Registering API Helper bean for " + ClassUtils.getShortName(apiBindingType));
		}		
		String helperId = "__" + ClassUtils.getShortNameAsProperty(apiBindingType) + "ApiHelper";
		BeanDefinition helperBD = BeanDefinitionBuilder.genericBeanDefinition(apiHelperClass).addConstructorArgReference("usersConnectionRepository").addConstructorArgReference("userIdSource").getBeanDefinition();
		parserContext.getRegistry().registerBeanDefinition(helperId, helperBD);
		
		if (logger.isDebugEnabled()) {
			logger.debug("Creating API Binding bean for " + ClassUtils.getShortName(apiBindingType));
		}		
		BeanDefinition bindingBD = BeanDefinitionBuilder.genericBeanDefinition().getBeanDefinition();
		bindingBD.setFactoryBeanName(helperId);
		bindingBD.setFactoryMethodName("getApi");
		bindingBD.setScope("request");
		BeanDefinitionHolder scopedProxyBDH = ScopedProxyUtils.createScopedProxy(new BeanDefinitionHolder(bindingBD, ClassUtils.getShortNameAsProperty(apiBindingType)), parserContext.getRegistry(), false);
		parserContext.getRegistry().registerBeanDefinition(scopedProxyBDH.getBeanName(), scopedProxyBDH.getBeanDefinition());
		return scopedProxyBDH.getBeanDefinition();
	}
	

	private final Class<? extends ConnectionFactory<?>> connectionFactoryClass;
	
	private final Class<?> apiBindingType;

	private final Class<?> apiHelperClass;

	private final static Log logger = LogFactory.getLog(AbstractProviderConfigBeanDefinitionParser.class);

	private static final String CONNECTION_FACTORY_LOCATOR_ID = "connectionFactoryLocator";

	private static final String APP_ID = "app-id";

	private static final String APP_SECRET = "app-secret";

	private static final String CONNECTION_FACTORIES = "connectionFactories";

}
