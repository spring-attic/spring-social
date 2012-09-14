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
package org.springframework.social.config.support;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.aop.scope.ScopedProxyUtils;
import org.springframework.beans.PropertyValue;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.ManagedList;
import org.springframework.core.GenericTypeResolver;
import org.springframework.social.config.xml.ApiHelper;
import org.springframework.social.connect.ConnectionFactory;
import org.springframework.social.connect.ConnectionFactoryLocator;
import org.springframework.social.connect.ConnectionRepository;
import org.springframework.social.connect.support.ConnectionFactoryRegistry;
import org.springframework.util.ClassUtils;

/**
 * Support class providing methods for configuring a {@link ConnectionFactory} (and a {@link ConnectionFactoryLocator} if one is not yet registered).
 * Also registers a request-scoped API binding retrieved from a {@link ConnectionRepository} bean.
 * @author Craig Walls
 */
public class ProviderConfigSupport {
	
	private final static Log logger = LogFactory.getLog(ProviderConfigSupport.class);

	public static BeanDefinition registerConnectionFactoryLocatorBean(BeanDefinitionRegistry registry) {
		if (logger.isDebugEnabled()) {
			logger.debug("Registering ConnectionFactoryLocator bean");
		}		
		if (!registry.containsBeanDefinition(CONNECTION_FACTORY_LOCATOR_ID)) {		
			BeanDefinitionHolder connFactoryLocatorBeanDefHolder = new BeanDefinitionHolder(BeanDefinitionBuilder.genericBeanDefinition(ConnectionFactoryRegistry.class).getBeanDefinition(), CONNECTION_FACTORY_LOCATOR_ID);			
			BeanDefinitionHolder scopedProxy = ScopedProxyUtils.createScopedProxy(connFactoryLocatorBeanDefHolder, registry, false);			
			registry.registerBeanDefinition(scopedProxy.getBeanName(), scopedProxy.getBeanDefinition());
		}
		BeanDefinition connectionFactoryLocatorBD = registry.getBeanDefinition(ScopedProxyUtils.getTargetBeanName(CONNECTION_FACTORY_LOCATOR_ID));
		return connectionFactoryLocatorBD;
	}


	public static BeanDefinition registerConnectionFactoryBean(BeanDefinition connectionFactoryLocatorBD, BeanDefinition connectionFactoryBD, Class<? extends ConnectionFactory<?>> connectionFactoryClass) {
		if (logger.isDebugEnabled()) {
			logger.debug("Registering ConnectionFactory for " + ClassUtils.getShortName(getApiBindingType(connectionFactoryClass)));
		}		
		PropertyValue connectionFactoriesPropertyValue = connectionFactoryLocatorBD.getPropertyValues().getPropertyValue(CONNECTION_FACTORIES);
		@SuppressWarnings("unchecked")
		List<BeanDefinition> connectionFactoriesList = connectionFactoriesPropertyValue != null ? 
				(List<BeanDefinition>) connectionFactoriesPropertyValue.getValue() : new ManagedList<BeanDefinition>();
		connectionFactoriesList.add(connectionFactoryBD);		
		connectionFactoryLocatorBD.getPropertyValues().addPropertyValue(CONNECTION_FACTORIES, connectionFactoriesList);
		return connectionFactoryBD;
	}
	
	public static BeanDefinition registerApiBindingBean(BeanDefinitionRegistry registry, Class<? extends ApiHelper<?>> apiHelperClass, Class<?> apiBindingType) {
		if (logger.isDebugEnabled()) {
			logger.debug("Registering API Helper bean for " + ClassUtils.getShortName(apiBindingType));
		}		
		String helperId = "__" + ClassUtils.getShortNameAsProperty(apiBindingType) + "ApiHelper";
		// TODO: Make the bean IDs here configurable.
		BeanDefinition helperBD = BeanDefinitionBuilder.genericBeanDefinition(apiHelperClass).addConstructorArgReference("usersConnectionRepository").addConstructorArgReference("userIdSource").getBeanDefinition();
		registry.registerBeanDefinition(helperId, helperBD);
		
		if (logger.isDebugEnabled()) {
			logger.debug("Creating API Binding bean for " + ClassUtils.getShortName(apiBindingType));
		}		
		BeanDefinition bindingBD = BeanDefinitionBuilder.genericBeanDefinition().getBeanDefinition();
		bindingBD.setFactoryBeanName(helperId);
		bindingBD.setFactoryMethodName("getApi");
		bindingBD.setScope("request");
		BeanDefinitionHolder scopedProxyBDH = ScopedProxyUtils.createScopedProxy(new BeanDefinitionHolder(bindingBD, ClassUtils.getShortNameAsProperty(apiBindingType)), registry, false);
		registry.registerBeanDefinition(scopedProxyBDH.getBeanName(), scopedProxyBDH.getBeanDefinition());
		return scopedProxyBDH.getBeanDefinition();
	}



	private static Class<?> getApiBindingType(Class<? extends ConnectionFactory<?>> connectionFactoryClass) {
		return GenericTypeResolver.resolveTypeArgument(connectionFactoryClass, ConnectionFactory.class);
	}

	private static final String CONNECTION_FACTORY_LOCATOR_ID = "connectionFactoryLocator";

	private static final String CONNECTION_FACTORIES = "connectionFactories";

}



























