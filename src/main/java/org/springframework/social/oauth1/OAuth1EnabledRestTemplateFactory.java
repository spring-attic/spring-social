package org.springframework.social.oauth1;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.social.oauth.OAuthClientRequestAuthorizer;
import org.springframework.social.oauth.OAuthEnabledRestTemplate;

public abstract class OAuth1EnabledRestTemplateFactory implements FactoryBean<OAuthEnabledRestTemplate>,
		ApplicationContextAware {

	protected ApplicationContext applicationContext;

	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

	public OAuthEnabledRestTemplate getObject() throws Exception {
		return new OAuthEnabledRestTemplate(getAuthorizer());
	}

	public Class<?> getObjectType() {
		return OAuthEnabledRestTemplate.class;
	}

	public boolean isSingleton() {
		return true;
	}

	protected abstract OAuthClientRequestAuthorizer getAuthorizer();
}
