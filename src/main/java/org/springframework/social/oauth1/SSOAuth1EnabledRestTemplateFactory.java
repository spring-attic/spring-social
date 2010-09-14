package org.springframework.social.oauth1;

import org.springframework.security.oauth.consumer.OAuthConsumerSupport;
import org.springframework.security.oauth.consumer.ProtectedResourceDetailsService;
import org.springframework.social.oauth.OAuthClientRequestSigner;
import org.springframework.social.oauth.OAuthEnabledRestTemplateFactory;


public class SSOAuth1EnabledRestTemplateFactory extends OAuthEnabledRestTemplateFactory {
	private String providerId;
	public SSOAuth1EnabledRestTemplateFactory(String providerId) {
		this.providerId = providerId;
	}

	public OAuthClientRequestSigner getRequestSigner() {
		return new SSOAuth1ClientRequestSigner(applicationContext.getBean(OAuthConsumerSupport.class),
				applicationContext.getBean(ProtectedResourceDetailsService.class).loadProtectedResourceDetailsById(
						providerId), getAccessTokenServices());
	}
}
