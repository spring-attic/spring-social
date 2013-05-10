package org.springframework.social.config.annotation;

import java.util.Map;

import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.social.config.support.InMemoryConnectionRepositoryConfigSupport;

/**
 * {@link ImportBeanDefinitionRegistrar} to enable {@link EnableInMemoryConnectionRepository} annotation.
 * @author Craig Walls
 */
class InMemoryConnectionRepositoryRegistrar extends InMemoryConnectionRepositoryConfigSupport implements ImportBeanDefinitionRegistrar {

	public void registerBeanDefinitions(AnnotationMetadata annotationMetadata, BeanDefinitionRegistry registry) {
		Map<String, Object> annotationAttributes = annotationMetadata.getAnnotationAttributes(EnableInMemoryConnectionRepository.class.getName());
		if (annotationAttributes == null) {
			return;
		}
		AnnotationAttributes attributes = new AnnotationAttributes(annotationAttributes);
		String connectionRepositoryId = attributes.getString("connectionRepositoryId");
		String usersConnectionRepositoryId = attributes.getString("usersConnectionRepositoryId");
		String connectionFactoryLocatorRef = attributes.getString("connectionFactoryLocatorRef");
		String userIdSourceRef = attributes.getString("userIdSourceRef");
		String connectionSignUpRef = attributes.getString("connectionSignUpRef");
		registerInMemoryConnectionRepositoryBeans(registry, connectionRepositoryId, usersConnectionRepositoryId, connectionFactoryLocatorRef, userIdSourceRef, connectionSignUpRef);
	}

}