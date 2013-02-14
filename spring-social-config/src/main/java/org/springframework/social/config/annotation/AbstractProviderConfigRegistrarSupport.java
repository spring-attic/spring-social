/*
 * Copyright 2013 the original author or authors.
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

import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.social.config.support.ProviderConfigurationSupport;
import org.springframework.social.config.xml.ApiHelper;
import org.springframework.social.connect.ConnectionFactory;

/**
 * Abstract base class for building provider-specific implementations of {@link ImportBeanDefinitionRegistrar} for configuring a connection factory and an API binding bean.
 * @author Craig Walls
 */
public abstract class AbstractProviderConfigRegistrarSupport extends ProviderConfigurationSupport implements ImportBeanDefinitionRegistrar {
	
	/**
	 * Constructs 
	 * @param connectionFactoryClass
	 * @param apiHelperClass
	 */
	public AbstractProviderConfigRegistrarSupport(Class<? extends Annotation> providerConfigAnnotation, Class<? extends ConnectionFactory<?>> connectionFactoryClass,
			Class<? extends ApiHelper<?>> apiHelperClass) {
		super(connectionFactoryClass, apiHelperClass);
		this.providerConfigAnnotation = providerConfigAnnotation;
	}
	
	public void registerBeanDefinitions(AnnotationMetadata metadata, BeanDefinitionRegistry registry) {
		Map<String, Object> allAttributes = metadata.getAnnotationAttributes(providerConfigAnnotation.getName());		
		registerBeanDefinitions(registry, allAttributes);		
	}

	@Override
	protected String getAppId(Map<String, Object> allAttributes) {
		return (String) allAttributes.get("appId");
	}
	
	@Override
	protected String getAppSecret(Map<String, Object> allAttributes) {
		return (String) allAttributes.get("appSecret");
	}
	
	private final Class<? extends Annotation> providerConfigAnnotation;

}
