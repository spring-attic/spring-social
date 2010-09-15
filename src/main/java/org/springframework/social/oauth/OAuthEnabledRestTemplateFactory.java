package org.springframework.social.oauth;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;

/**
 * @deprecated This class is likely to soon go away and be replaced with a new
 *             API
 */
public abstract class OAuthEnabledRestTemplateFactory implements FactoryBean<RestOperations>,
		ApplicationContextAware {

	protected ApplicationContext applicationContext;

	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

	public RestOperations getObject() throws Exception {
		return createRestTemplate();
	}

	protected RestOperations createRestTemplate() {
		return new RestTemplate(new OAuthSigningClientHttpRequestFactory(getRequestSigner()));
	}

	public Class<?> getObjectType() {
		return RestOperations.class;
	}

	public boolean isSingleton() {
		return true;
	}

	protected AccessTokenServices getAccessTokenServices() {
		return applicationContext.getBean(AccessTokenServices.class);
	}

	protected abstract OAuthClientRequestSigner getRequestSigner();
}
