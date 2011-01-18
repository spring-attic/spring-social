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
package org.springframework.social.provider.jdbc;

import java.util.Map;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.social.provider.ServiceProvider;
import org.springframework.social.provider.ServiceProviderFactory;

public class ContextServiceProviderFactory implements ServiceProviderFactory, BeanFactoryPostProcessor {

	@SuppressWarnings("rawtypes")
	private Map<String, ServiceProvider> serviceProviders;

	public ServiceProvider<?> getServiceProvider(String name) {
		return serviceProviders.get(name);
	}

	@SuppressWarnings("unchecked")
	public <S> ServiceProvider<S> getServiceProvider(String name, Class<S> serviceType) {
		return (ServiceProvider<S>) serviceProviders.get(name);
	}

	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
		serviceProviders = beanFactory.getBeansOfType(ServiceProvider.class);
	}

}
