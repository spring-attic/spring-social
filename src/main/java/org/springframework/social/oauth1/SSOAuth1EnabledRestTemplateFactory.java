package org.springframework.social.oauth1;

import org.springframework.security.oauth.consumer.OAuthConsumerSupport;
import org.springframework.security.oauth.consumer.ProtectedResourceDetailsService;


public class SSOAuth1EnabledRestTemplateFactory extends OAuth1EnabledRestTemplateFactory {
	private String providerId;
	public SSOAuth1EnabledRestTemplateFactory(String providerId) {
		this.providerId = providerId;
	}

	public OAuth1Template getOAuth1Template() {
		return new SSOAuth1Template(applicationContext.getBean(OAuthConsumerSupport.class), applicationContext.getBean(
				ProtectedResourceDetailsService.class).loadProtectedResourceDetailsById(providerId),
				applicationContext.getBean(SSOAuthAccessTokenServices.class));
	}
}
