package org.springframework.social.oauth;

import org.springframework.http.client.ClientHttpRequest;

public interface OAuthClientRequestDecorator {
	ClientHttpRequest decorate(ClientHttpRequest request) throws AuthorizationException;
}
