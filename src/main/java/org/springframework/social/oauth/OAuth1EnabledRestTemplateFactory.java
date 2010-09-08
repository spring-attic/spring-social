package org.springframework.social.oauth;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public abstract class OAuth1EnabledRestTemplateFactory implements FactoryBean<OAuthEnabledRestTemplate>,
		ApplicationContextAware {

	protected ApplicationContext applicationContext;

	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

	public OAuthEnabledRestTemplate getObject() throws Exception {
		return new OAuthEnabledRestTemplate(new OAuth1ClientRequestAuthorizer(getOAuth1Template()));
	}

	public Class<?> getObjectType() {
		return OAuthEnabledRestTemplate.class;
	}

	public boolean isSingleton() {
		return true;
	}

	protected abstract OAuth1Template getOAuth1Template();
}
