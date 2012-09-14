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

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.GenericTypeResolver;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.social.config.support.ProviderConfigSupport;
import org.springframework.social.config.xml.ApiHelper;
import org.springframework.social.connect.ConnectionFactory;

/**
 * Abstract base class for building provider-specific implementations of {@link ImportBeanDefinitionRegistrar} for configuring a connection factory and an API binding bean.
 * @author Craig Walls
 */
public abstract class ProviderConfigRegistrarSupport implements ImportBeanDefinitionRegistrar {

	/**
	 * Constructs 
	 * @param connectionFactoryClass
	 * @param apiHelperClass
	 */
	public ProviderConfigRegistrarSupport(Class<? extends Annotation> providerConfigAnnotation, Class<? extends ConnectionFactory<?>> connectionFactoryClass, Class<? extends ApiHelper<?>> apiHelperClass) {
		this.providerConfigAnnotation = providerConfigAnnotation;
		this.connectionFactoryClass = connectionFactoryClass;
		this.apiHelperClass = apiHelperClass;
		this.apiBindingType = GenericTypeResolver.resolveTypeArgument(connectionFactoryClass, ConnectionFactory.class);		
	}
	
	public void registerBeanDefinitions(AnnotationMetadata metadata, BeanDefinitionRegistry registry) {
		Map<String, Object> allAttributes = metadata.getAnnotationAttributes(providerConfigAnnotation.getName());
		BeanDefinition connectionFactoryBD = getConnectionFactoryBeanDefinition((String) allAttributes.get("appId"), (String) allAttributes.get("appSecret"), allAttributes);
		BeanDefinition connectionFactoryLocatorBD = ProviderConfigSupport.registerConnectionFactoryLocatorBean(registry);
		ProviderConfigSupport.registerConnectionFactoryBean(connectionFactoryLocatorBD, connectionFactoryBD, connectionFactoryClass);
		ProviderConfigSupport.registerApiBindingBean(registry, apiHelperClass, apiBindingType);		
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

	private final Class<? extends ConnectionFactory<?>> connectionFactoryClass;
	
	private final Class<? extends ApiHelper<?>> apiHelperClass;
	
	private final Class<?> apiBindingType;

	private final Class<? extends Annotation> providerConfigAnnotation;

}
