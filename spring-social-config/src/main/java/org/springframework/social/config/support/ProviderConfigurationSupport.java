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
package org.springframework.social.config.support;

import java.util.List;
import java.util.Map;

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
import org.springframework.social.connect.support.ConnectionFactoryRegistry;
import org.springframework.social.security.provider.SocialAuthenticationService;
import org.springframework.util.ClassUtils;

public abstract class ProviderConfigurationSupport {

	private final static Log logger = LogFactory.getLog(ProviderConfigurationSupport.class);	

	public ProviderConfigurationSupport(Class<? extends ConnectionFactory<?>> connectionFactoryClass, Class<? extends ApiHelper<?>> apiHelperClass) {
		this.connectionFactoryClass = connectionFactoryClass;
		this.apiHelperClass = apiHelperClass;
		this.apiBindingType = GenericTypeResolver.resolveTypeArgument(connectionFactoryClass, ConnectionFactory.class);
		if (isSocialSecurityAvailable()) {
			this.authenticationServiceClass = getAuthenticationServiceClass();
		}
	}
	
	protected Class<? extends SocialAuthenticationService<?>> getAuthenticationServiceClass() {
		return null;
	}

	protected static boolean isSocialSecurityAvailable() {
		try {
			Class.forName("org.springframework.social.security.SocialAuthenticationServiceLocator");
			return true;
		} catch (ClassNotFoundException cnfe) {
			return false; 
		}
	}	

	/**
	 * Creates a BeanDefinition for a provider connection factory.
	 * Although most providers will not need to override this method, it does allow for overriding to address any provider-specific needs.
	 * @param appId The application's App ID
	 * @param appSecret The application's App Secret
	 * @param allAttributes All attributes available on the configuration element. Useful for provider-specific configuration.
	 * @return a BeanDefinition for the provider's connection factory bean.
	 */
	protected BeanDefinition getConnectionFactoryBeanDefinition(String appId, String appSecret, Map<String, Object> allAttributes) {
		return BeanDefinitionBuilder.genericBeanDefinition(connectionFactoryClass).addConstructorArgValue(appId).addConstructorArgValue(appSecret).getBeanDefinition();
	}
	
	protected BeanDefinition getAuthenticationServiceBeanDefinition(String appId, String appSecret, Map<String, Object> allAttributes) {
		return BeanDefinitionBuilder.genericBeanDefinition(authenticationServiceClass).addConstructorArgValue(appId).addConstructorArgValue(appSecret).getBeanDefinition();
	}
	
	protected BeanDefinition registerBeanDefinitions(BeanDefinitionRegistry registry, Map<String, Object> allAttributes) {
		if (isSocialSecurityAvailable() && authenticationServiceClass != null) {
			registerAuthenticationServiceBeanDefinitions(registry, allAttributes);						
		} else {
			registerConnectionFactoryBeanDefinitions(registry, allAttributes);			
		}
		
		return registerApiBindingBean(registry, apiHelperClass, apiBindingType);
	}
	
	protected abstract String getAppId(Map<String, Object> allAttributes);
	
	protected abstract String getAppSecret(Map<String, Object> allAttributes);
	
	private void registerConnectionFactoryBeanDefinitions(BeanDefinitionRegistry registry, Map<String, Object> allAttributes) {
		BeanDefinition connectionFactoryBD = getConnectionFactoryBeanDefinition(getAppId(allAttributes), getAppSecret(allAttributes), allAttributes);
		BeanDefinition connectionFactoryLocatorBD = registerConnectionFactoryLocatorBean(registry);
		registerConnectionFactoryBean(connectionFactoryLocatorBD, connectionFactoryBD, connectionFactoryClass);
	}

	@SuppressWarnings("unchecked")
	private void registerAuthenticationServiceBeanDefinitions(BeanDefinitionRegistry registry, Map<String, Object> allAttributes) {
		Class<? extends org.springframework.social.security.provider.SocialAuthenticationService<?>> socialAuthenticationServiceClass = (Class<? extends org.springframework.social.security.provider.SocialAuthenticationService<?>>) this.authenticationServiceClass;
		BeanDefinition authenticationServiceBD = getAuthenticationServiceBeanDefinition(getAppId(allAttributes), getAppSecret(allAttributes), allAttributes);
		BeanDefinition connectionFactoryLocatorBD = registerConnectionFactoryLocatorBean(registry);
		registerAuthenticationServiceBean(connectionFactoryLocatorBD, authenticationServiceBD, socialAuthenticationServiceClass);
	}
	
	private BeanDefinition registerConnectionFactoryLocatorBean(BeanDefinitionRegistry registry) {
		Class<?> connectionFactoryRegistryClass = isSocialSecurityAvailable() ? org.springframework.social.security.SocialAuthenticationServiceRegistry.class : ConnectionFactoryRegistry.class;		
		if (!registry.containsBeanDefinition(CONNECTION_FACTORY_LOCATOR_ID)) {		
			if (logger.isDebugEnabled()) {
				logger.debug("Registering ConnectionFactoryLocator bean (" + connectionFactoryRegistryClass.getName() + ")");
			}
			BeanDefinitionHolder connFactoryLocatorBeanDefHolder = new BeanDefinitionHolder(BeanDefinitionBuilder.genericBeanDefinition(connectionFactoryRegistryClass).getBeanDefinition(), CONNECTION_FACTORY_LOCATOR_ID);			
			BeanDefinitionHolder scopedProxy = ScopedProxyUtils.createScopedProxy(connFactoryLocatorBeanDefHolder, registry, false);			
			registry.registerBeanDefinition(scopedProxy.getBeanName(), scopedProxy.getBeanDefinition());
		}
		BeanDefinition connectionFactoryLocatorBD = registry.getBeanDefinition(ScopedProxyUtils.getTargetBeanName(CONNECTION_FACTORY_LOCATOR_ID));
		return connectionFactoryLocatorBD;
	}

	private BeanDefinition registerConnectionFactoryBean(BeanDefinition connectionFactoryLocatorBD, BeanDefinition connectionFactoryBD, Class<? extends ConnectionFactory<?>> connectionFactoryClass) {
		if (logger.isDebugEnabled()) {
			logger.debug("Registering ConnectionFactory: " + connectionFactoryClass.getName());
		}		
		PropertyValue connectionFactoriesPropertyValue = connectionFactoryLocatorBD.getPropertyValues().getPropertyValue(CONNECTION_FACTORIES);
		@SuppressWarnings("unchecked")
		List<BeanDefinition> connectionFactoriesList = connectionFactoriesPropertyValue != null ? 
				(List<BeanDefinition>) connectionFactoriesPropertyValue.getValue() : new ManagedList<BeanDefinition>();
		connectionFactoriesList.add(connectionFactoryBD);		
		connectionFactoryLocatorBD.getPropertyValues().addPropertyValue(CONNECTION_FACTORIES, connectionFactoriesList);
		return connectionFactoryBD;
	}
	
	private BeanDefinition registerAuthenticationServiceBean(BeanDefinition authenticationServiceLocatorBD, BeanDefinition authenticationServiceBD, Class<? extends org.springframework.social.security.provider.SocialAuthenticationService<?>> socialAuthenticationServiceClass) {
		if (logger.isDebugEnabled()) {
			logger.debug("Registering SocialAuthenticationService: " + socialAuthenticationServiceClass.getName());
		}
		PropertyValue authenticationServicesPropertyValue = authenticationServiceLocatorBD.getPropertyValues().getPropertyValue(AUTHENTICATION_SERVICES);
		@SuppressWarnings("unchecked")
		List<BeanDefinition> authenticationServicesList = authenticationServicesPropertyValue != null ? 
				(List<BeanDefinition>) authenticationServicesPropertyValue.getValue() : new ManagedList<BeanDefinition>();
		authenticationServicesList.add(authenticationServiceBD);
		authenticationServiceLocatorBD.getPropertyValues().addPropertyValue(AUTHENTICATION_SERVICES, authenticationServicesList);
		return authenticationServiceBD;
	}
	
	private BeanDefinition registerApiBindingBean(BeanDefinitionRegistry registry, Class<? extends ApiHelper<?>> apiHelperClass, Class<?> apiBindingType) {
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

	protected final Class<? extends ConnectionFactory<?>> connectionFactoryClass;
	
	protected final Class<? extends ApiHelper<?>> apiHelperClass;
	
	protected final Class<?> apiBindingType;

	protected Class<?> authenticationServiceClass;

	private static final String CONNECTION_FACTORY_LOCATOR_ID = "connectionFactoryLocator";

	private static final String CONNECTION_FACTORIES = "connectionFactories";

	private static final String AUTHENTICATION_SERVICES = "authenticationServices";

}
