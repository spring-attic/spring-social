package org.springframework.social.oauth1;

import org.springframework.security.oauth.consumer.token.OAuthConsumerToken;
import org.springframework.social.oauth.AccessTokenServices;

public interface SSOAuthAccessTokenServices extends AccessTokenServices<OAuthConsumerToken> {
}
