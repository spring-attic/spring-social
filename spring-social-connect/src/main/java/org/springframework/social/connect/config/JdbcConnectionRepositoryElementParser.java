package org.springframework.social.connect.config;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.security.encrypt.NoOpStringEncryptor;
import org.springframework.social.connect.jdbc.JdbcAccountConnectionRepository;
import org.w3c.dom.Element;

public class JdbcConnectionRepositoryElementParser implements BeanDefinitionParser {

	public BeanDefinition parse(Element element, ParserContext parserContext) {
		BeanDefinitionBuilder beanBuilder = BeanDefinitionBuilder
				.genericBeanDefinition(JdbcAccountConnectionRepository.class);

		String jdbcTemplate = element.getAttribute("jdbc-template");
		beanBuilder.addConstructorArgReference(jdbcTemplate);

		String stringEncryptor = element.getAttribute("string-encryptor");
		if (stringEncryptor != null && !stringEncryptor.isEmpty()) {
			beanBuilder.addConstructorArgReference(stringEncryptor);
		} else {
			beanBuilder.addConstructorArgValue(NoOpStringEncryptor.getInstance());
		}

		AbstractBeanDefinition beanDefinition = beanBuilder.getBeanDefinition();
		parserContext.getRegistry().registerBeanDefinition("accountConnectionRepository", beanDefinition);

		return beanDefinition;
	}
}
