package org.springframework.social.facebook;

import org.springframework.social.oauth.OAuthClientRequestSigner;
import org.springframework.social.oauth.OAuthEnabledRestTemplateFactory;

public class FacebookOAuthEnabledRestTemplateFactory extends OAuthEnabledRestTemplateFactory {
	protected OAuthClientRequestSigner getRequestSigner() {
		return new FacebookClientRequestSigner(getAccessTokenServices(), "Facebook");
	}
}
