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

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.core.GenericTypeResolver;
import org.springframework.social.config.support.ProviderConfigSupport;
import org.springframework.social.connect.ConnectionFactory;
import org.springframework.social.connect.ConnectionFactoryLocator;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/**
 * Abstract bean definition parser for configuring provider-specific beans in a Spring application context.
 * Automatically creates a {@link ConnectionFactoryLocator} bean if none exists and registers the {@link ConnectionFactory} bean with the {@link ConnectionFactoryLocator}.
 * Also creates a request-scoped API binding bean retrieved from the connection repository.
 * @author Craig Walls
 */
public abstract class AbstractProviderConfigBeanDefinitionParser implements BeanDefinitionParser {

	public final BeanDefinition parse(Element element, ParserContext parserContext) {
		BeanDefinitionRegistry registry = parserContext.getRegistry();
		Map<String, String> allAttributes = toMap(element.getAttributes());
		BeanDefinition connectionFactoryBD = getConnectionFactoryBeanDefinition(allAttributes.get(APP_ID), allAttributes.get(APP_SECRET), allAttributes);
		BeanDefinition connectionFactoryLocatorBD = ProviderConfigSupport.registerConnectionFactoryLocatorBean(registry);
		ProviderConfigSupport.registerConnectionFactoryBean(connectionFactoryLocatorBD, connectionFactoryBD, connectionFactoryClass);
		return ProviderConfigSupport.registerApiBindingBean(registry, apiHelperClass, apiBindingType);
	}

	/**
	 * Constructs a connection factory-creating {@link BeanDefinitionParser}.
	 * @param connectionFactoryClass The type of {@link ConnectionFactory} to create. Must have a two-argument constructor taking an application's ID and secret as Strings.
	 */
	protected AbstractProviderConfigBeanDefinitionParser(Class<? extends ConnectionFactory<?>> connectionFactoryClass, Class<? extends ApiHelper<?>> apiHelperClass) {
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
	protected BeanDefinition getConnectionFactoryBeanDefinition(String appId, String appSecret, Map<String, String> allAttributes) {
		return BeanDefinitionBuilder.genericBeanDefinition(connectionFactoryClass).addConstructorArgValue(appId).addConstructorArgValue(appSecret).getBeanDefinition();
	}
	

	private Map<String, String> toMap(NamedNodeMap attributes) {
		Map<String, String> map = new HashMap<String, String>(attributes.getLength());
		for (int i=0; i < attributes.getLength(); i++) {
			Node attribute = attributes.item(i);
			map.put(attribute.getNodeName(),  attribute.getNodeValue());
		}
		return map;
	}

	private final Class<? extends ConnectionFactory<?>> connectionFactoryClass;
	
	private final Class<?> apiBindingType;

	private final Class<? extends ApiHelper<?>> apiHelperClass;

	private static final String APP_ID = "app-id";

	private static final String APP_SECRET = "app-secret";

}
