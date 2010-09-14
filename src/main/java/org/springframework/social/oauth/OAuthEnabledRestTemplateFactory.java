package org.springframework.social.oauth;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public abstract class OAuthEnabledRestTemplateFactory implements FactoryBean<OAuthEnabledRestTemplate>,
		ApplicationContextAware {

	protected ApplicationContext applicationContext;

	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

	public OAuthEnabledRestTemplate getObject() throws Exception {
		return createRestTemplate();
	}

	protected OAuthEnabledRestTemplate createRestTemplate() {
		return new OAuthEnabledRestTemplate(new OAuthEnabledClientHttpRequestFactory(getRequestSigner()));
	}

	public Class<?> getObjectType() {
		return OAuthEnabledRestTemplate.class;
	}

	public boolean isSingleton() {
		return true;
	}

	protected AccessTokenServices getAccessTokenServices() {
		return applicationContext.getBean(AccessTokenServices.class);
	}

	protected abstract OAuthClientRequestSigner getRequestSigner();
}
