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
package org.springframework.social.config.xml;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.NamespaceHandler;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Base {@link NamespaceHandler} for Spring Social provider modules to create configuration namespaces. 
 * Requires, at minimum, that the provider-specific namespace provider an AbstractProviderConfigBeanDefinition for parsing "config" elements.
 * @author Craig Walls
 */
public abstract class AbstractProviderConfigNamespaceHandler implements NamespaceHandler {

	public final void init() {
		loadParsers();
	}

	public final BeanDefinition parse(Element element, ParserContext parserContext) {
		String name = parserContext.getDelegate().getLocalName(element);
        BeanDefinitionParser parser = parsers.get(name);
        if (parser == null) {
            loadParsers();
        }
        
        if(parser == null) {
        	reportUnsupportedNodeType(name, parserContext, element);
        	return null;
        }
        
        return parser.parse(element, parserContext);
	}

	public BeanDefinitionHolder decorate(Node node, BeanDefinitionHolder beanDefinitionHolder, ParserContext parserContext) {
		return beanDefinitionHolder;
	}
	
	/**
	 * Implemented by provider namespaces to provide an instance of the bean definition parser that will parse the "config" element.
	 * This is the only configuration element required by a provider namespace.
	 * If a provider namespace offers additional elements, then their bean definition parsers may be registered by overriding the loadParsers() method.
	 * @return an instance of AbstractProviderConfigBeanDefinitionParser to register against the "config" element in the namespace.
	 */
	protected abstract AbstractProviderConfigBeanDefinitionParser getProviderConfigBeanDefinitionParser();
	
	/**
	 * Hook method to allow provider-specific implementation to register bean definition parsers for their namespace.
	 * An overriding method will simply add one or more bean definition parsers to the given map where the key is the name of the element the parser handles.
	 * Overriding this method is optional and there is no need to override it just to add the provider-specific implementation of AbstractProviderConfigBeanDefinitionParser.
	 * That bean definition parser will always be registered, whether or not this method is overridden.
	 * @param parsers a Map of parsers to be applied when parsing the namespace.
	 */
	protected void loadParsers(Map<String, BeanDefinitionParser> parsers) {}
	
    private void reportUnsupportedNodeType(String name, ParserContext parserContext, Node node) {
        parserContext.getReaderContext().fatal("Twitter namespace does not support " +
                (node instanceof Element ? "element" : "attribute") + " [" + name + "]", node);
    }

	private void loadParsers() {
		parsers.put("config", getProviderConfigBeanDefinitionParser());
		loadParsers(parsers);
	}

    private final Map<String, BeanDefinitionParser> parsers = new HashMap<String, BeanDefinitionParser>();

}
