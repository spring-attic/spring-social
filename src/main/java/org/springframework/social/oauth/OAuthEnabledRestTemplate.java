package org.springframework.social.oauth;

import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

public class OAuthEnabledRestTemplate extends RestTemplate {
	public OAuthEnabledRestTemplate(ClientHttpRequestFactory requestFactory) {
		this.setRequestFactory(requestFactory);
	}
}
