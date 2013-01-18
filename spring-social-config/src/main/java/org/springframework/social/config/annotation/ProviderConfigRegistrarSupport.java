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
package org.springframework.social.config.annotation;

import java.lang.annotation.Annotation;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.GenericTypeResolver;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.social.config.support.ProviderConfigSupport;
import org.springframework.social.config.xml.ApiHelper;
import org.springframework.social.connect.ConnectionFactory;
import org.springframework.social.security.provider.SocialAuthenticationService;

/**
 * Abstract base class for building provider-specific implementations of {@link ImportBeanDefinitionRegistrar} for configuring a connection factory and an API binding bean.
 * @author Craig Walls
 */
public abstract class ProviderConfigRegistrarSupport implements ImportBeanDefinitionRegistrar {

	private final static Log logger = LogFactory.getLog(ProviderConfigRegistrarSupport.class);

	/**
	 * Constructs 
	 * @param connectionFactoryClass
	 * @param apiHelperClass
	 */
	public ProviderConfigRegistrarSupport(Class<? extends Annotation> providerConfigAnnotation, Class<? extends ConnectionFactory<?>> connectionFactoryClass, 
			String socialAuthenticationServiceClassName, Class<? extends ApiHelper<?>> apiHelperClass) {
		// TODO: Does the above signature create a hard dependency on social security???
		this.providerConfigAnnotation = providerConfigAnnotation;
		this.connectionFactoryClass = connectionFactoryClass;
		this.socialAuthenticationServiceClassName = socialAuthenticationServiceClassName;
		this.apiHelperClass = apiHelperClass;
		this.apiBindingType = GenericTypeResolver.resolveTypeArgument(connectionFactoryClass, ConnectionFactory.class);		
	}
	
	public void registerBeanDefinitions(AnnotationMetadata metadata, BeanDefinitionRegistry registry) {
		Map<String, Object> allAttributes = metadata.getAnnotationAttributes(providerConfigAnnotation.getName());
		
		if (isSocialSecurityAvailable() && socialAuthenticationServiceClassName != null) {
			registerAuthenticationServiceBeanDefinitions(registry, allAttributes);						
		} else {
			registerConnectionFactoryBeanDefinitions(registry, allAttributes);			
		}
		
		ProviderConfigSupport.registerApiBindingBean(registry, apiHelperClass, apiBindingType);		
	}

	private static boolean isSocialSecurityAvailable() {
		try {
			Class.forName("org.springframework.social.security.SocialAuthenticationServiceLocator");
			return true;
		} catch (ClassNotFoundException cnfe) {
			return false; 
		}
	}	
	private void registerConnectionFactoryBeanDefinitions(BeanDefinitionRegistry registry, Map<String, Object> allAttributes) {
		BeanDefinition connectionFactoryBD = getConnectionFactoryBeanDefinition((String) allAttributes.get("appId"), (String) allAttributes.get("appSecret"), allAttributes);
		BeanDefinition connectionFactoryLocatorBD = ProviderConfigSupport.registerConnectionFactoryLocatorBean(registry);
		ProviderConfigSupport.registerConnectionFactoryBean(connectionFactoryLocatorBD, connectionFactoryBD, connectionFactoryClass);
	}

	private void registerAuthenticationServiceBeanDefinitions(BeanDefinitionRegistry registry, Map<String, Object> allAttributes) {
		try {
			Class<? extends SocialAuthenticationService<?>> socialAuthenticationServiceClass = (Class<? extends SocialAuthenticationService<?>>) Class.forName(socialAuthenticationServiceClassName);
			BeanDefinition authenticationServiceBD = getAuthenticationServiceBeanDefinition((String) allAttributes.get("appId"), (String) allAttributes.get("appSecret"), allAttributes);
			BeanDefinition connectionFactoryLocatorBD = ProviderConfigSupport.registerConnectionFactoryLocatorBean(registry);
			ProviderConfigSupport.registerAuthenticationServiceBean(connectionFactoryLocatorBD, authenticationServiceBD, socialAuthenticationServiceClass);
		} catch (ClassNotFoundException cnfe) {
			logger.error("Unable to configure SocialAuthenticationService (" + socialAuthenticationServiceClassName + "); Class not found.");
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
		try {
			Class<?> socialAuthenticationServiceClass = Class.forName(socialAuthenticationServiceClassName);
			return BeanDefinitionBuilder.genericBeanDefinition(socialAuthenticationServiceClass).addConstructorArgValue(appId).addConstructorArgValue(appSecret).getBeanDefinition();
		} catch (ClassNotFoundException cnfe) {
			// Fall back on connection factory bean
			return getConnectionFactoryBeanDefinition(appId, appSecret, allAttributes);
		}
	}

	private final Class<? extends ConnectionFactory<?>> connectionFactoryClass;
	
	private String socialAuthenticationServiceClassName;

	private final Class<? extends ApiHelper<?>> apiHelperClass;
	
	private final Class<?> apiBindingType;

	private final Class<? extends Annotation> providerConfigAnnotation;

}
