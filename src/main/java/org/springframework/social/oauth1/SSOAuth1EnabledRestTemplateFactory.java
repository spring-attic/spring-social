package org.springframework.social.oauth1;

import org.springframework.security.oauth.consumer.OAuthConsumerSupport;
import org.springframework.security.oauth.consumer.ProtectedResourceDetailsService;
import org.springframework.social.oauth.OAuthClientRequestSigner;
import org.springframework.social.oauth.OAuthEnabledRestTemplate;
import org.springframework.social.oauth.OAuthEnabledRestTemplateFactory;
import org.springframework.social.twitter.TwitterErrorHandler;

/**
 * @deprecated This class is likely to soon go away and be replaced with a new
 *             API
 */
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

	protected OAuthEnabledRestTemplate createRestTemplate() {
		// TODO: If this class wasn't going away, then the following code would
		// not belong. But given its short life expectancy, I'm putting it here
		// for now so that I can go ahead and make TwitterTemplate depend on
		// RestOperations instead of RestTemplate.
		OAuthEnabledRestTemplate restTemplate = super.createRestTemplate();
		restTemplate.setErrorHandler(new TwitterErrorHandler());
		return restTemplate;
	}
}
