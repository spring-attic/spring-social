package org.springframework.social.config.xml;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.parsing.BeanComponentDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.social.connect.ConnectionFactory;
import org.springframework.social.connect.jdbc.JdbcUsersConnectionRepository;
import org.springframework.social.connect.support.ConnectionFactoryRegistry;
import org.springframework.social.facebook.connect.FacebookConnectionFactory;
import org.springframework.social.twitter.api.Twitter;
import org.springframework.social.twitter.connect.TwitterConnectionFactory;
import org.springframework.util.xml.DomUtils;
import org.w3c.dom.Element;

public class SocialConfigBeanDefinitionParser implements BeanDefinitionParser {

	private static final String CONNECTION_FACTORY_LOCATOR = "connectionFactoryLocator";

	private static final String TWITTER = "twitter";

	private static final String FACEBOOK = "facebook";

	public BeanDefinition parse(Element configElement, ParserContext parserContext) {
		BeanDefinition connFactoryLocatorBeanDef = BeanDefinitionBuilder.genericBeanDefinition(ConnectionFactoryRegistry.class).getBeanDefinition();
		BeanComponentDefinition connFactoryLocatorBeanCompDef = new BeanComponentDefinition(connFactoryLocatorBeanDef, CONNECTION_FACTORY_LOCATOR);		
		parserContext.registerBeanComponent(connFactoryLocatorBeanCompDef);
		
		
		registerJdbcConnectionRepositories(configElement, parserContext);
		
		// TODO: Need a connection repository registered before we can register API binding beans
		
		List<ConnectionFactory<?>> connectionFactories = new ArrayList<ConnectionFactory<?>>();
		Element twitterElement = DomUtils.getChildElementByTagName(configElement, TWITTER);
		if (twitterElement != null) {
			connectionFactories.add(new TwitterConnectionFactory(
					twitterElement.getAttribute("consumer-key"), 
					twitterElement.getAttribute("consumer-secret")));
			
			
			BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition()
					.setFactoryBean("connectionRepository", "findPrimaryConnection")
					.addConstructorArgValue(Twitter.class)
					.setScope("request");
			parserContext.registerBeanComponent(new BeanComponentDefinition(builder.getBeanDefinition(), "twitter"));
			
			
		}		
		
		
		
		registerFacebookConnectionFactory(configElement, connectionFactories);
		
		connFactoryLocatorBeanDef.getPropertyValues().add("connectionFactories", connectionFactories);
		
		return null;
	}

	private void registerJdbcConnectionRepositories(Element configElement, ParserContext parserContext) {
		Element repoElement = DomUtils.getChildElementByTagName(configElement, "jdbc-connection-repository");
		if (repoElement == null) return;
		
		String dataSourceRef = repoElement.getAttribute("data-source-ref");
		String encryptorRef = repoElement.getAttribute("encryptor-ref");
		String userIdSourceRef = repoElement.getAttribute("user-id-source-ref");
		
		BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(JdbcUsersConnectionRepository.class)
				.addConstructorArgReference(dataSourceRef)
				.addConstructorArgReference("connectionFactoryLocator")
				.addConstructorArgReference(encryptorRef)
				.setScope(BeanDefinition.SCOPE_SINGLETON);
		parserContext.registerBeanComponent(new BeanComponentDefinition(builder.getBeanDefinition(), "usersConnectionRepository"));
		
		BeanDefinitionBuilder userIdBuilder = BeanDefinitionBuilder.genericBeanDefinition()
				.setFactoryBean(userIdSourceRef, "getUserId");
		parserContext.registerBeanComponent(new BeanComponentDefinition(userIdBuilder.getBeanDefinition(), "_userId"));
		
		builder = BeanDefinitionBuilder.genericBeanDefinition()
				.setFactoryBean("usersConnectionRepository", "createConnectionRepository")
				.addConstructorArgReference("_userId");
		
		parserContext.registerBeanComponent(new BeanComponentDefinition(builder.getBeanDefinition(), "connectionRepository"));
		
		
//		new JdbcUsersConnectionRepository(dataSource, connectionFactoryLocator, textEncryptor)
	}

	private void registerTwitterConnectionFactory(Element configElement, List<ConnectionFactory<?>> connectionFactories) {
		Element twitterElement = DomUtils.getChildElementByTagName(configElement, TWITTER);
		if (twitterElement != null) {
			connectionFactories.add(new TwitterConnectionFactory(
					twitterElement.getAttribute("consumer-key"), 
					twitterElement.getAttribute("consumer-secret")));
		}
	}

	private void registerFacebookConnectionFactory(Element configElement, List<ConnectionFactory<?>> connectionFactories) {
		Element facebookElement = DomUtils.getChildElementByTagName(configElement, FACEBOOK);
		if (facebookElement != null) {
			connectionFactories.add(new FacebookConnectionFactory(
					facebookElement.getAttribute("consumer-key"), 
					facebookElement.getAttribute("consumer-secret")));
		}
	}

}
