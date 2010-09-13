package org.springframework.social.oauth1;

import org.springframework.security.oauth.consumer.OAuthConsumerSupport;
import org.springframework.security.oauth.consumer.ProtectedResourceDetailsService;
import org.springframework.social.oauth.OAuthEnabledRestTemplateFactory;
import org.springframework.social.oauth.OAuthClientRequestAuthorizer;


public class SSOAuth1EnabledRestTemplateFactory extends OAuthEnabledRestTemplateFactory {
	private String providerId;
	public SSOAuth1EnabledRestTemplateFactory(String providerId) {
		this.providerId = providerId;
	}

	public OAuthClientRequestAuthorizer getAuthorizer() {
		return new SSOAuth1ClientRequestAuthorizer(applicationContext.getBean(OAuthConsumerSupport.class), applicationContext.getBean(
				ProtectedResourceDetailsService.class).loadProtectedResourceDetailsById(providerId),
				applicationContext.getBean(SSOAuthAccessTokenServices.class));
	}
}
