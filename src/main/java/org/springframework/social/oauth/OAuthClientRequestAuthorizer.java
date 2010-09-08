package org.springframework.social.oauth;

import org.springframework.http.client.ClientHttpRequest;

public interface OAuthClientRequestAuthorizer {
	ClientHttpRequest authorize(ClientHttpRequest request);
}
