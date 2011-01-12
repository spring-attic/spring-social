package org.springframework.social.connect.config;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.social.connect.jdbc.ContextServiceProviderFactory;
import org.w3c.dom.Element;

public class ContextServiceProviderFactoryElementParser implements BeanDefinitionParser {
	public BeanDefinition parse(Element element, ParserContext parserContext) {
		BeanDefinitionBuilder beanBuilder = BeanDefinitionBuilder
				.genericBeanDefinition(ContextServiceProviderFactory.class);
		AbstractBeanDefinition beanDefinition = beanBuilder.getBeanDefinition();
		parserContext.getRegistry().registerBeanDefinition("serviceProviderFactory", beanDefinition);
		return beanDefinition;
	}
}
