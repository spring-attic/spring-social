package org.springframework.social.oauth2;

import org.springframework.http.client.ClientHttpRequestInterceptor;

public enum TokenStrategy {

	AUTHORIZATION_HEADER {
		public ClientHttpRequestInterceptor interceptor(String accessToken, OAuth2Version oauth2Version) {
			return new OAuth2RequestInterceptor(accessToken, oauth2Version);
		}
	},
	ACCESS_TOKEN_PARAMETER {
		public ClientHttpRequestInterceptor interceptor(String accessToken, OAuth2Version oauth2Version) {
			return new OAuth2TokenParameterRequestInterceptor(accessToken);
		}
	},
	OAUTH_TOKEN_PARAMETER {
		public ClientHttpRequestInterceptor interceptor(String accessToken, OAuth2Version oauth2Version) {
			return new OAuth2TokenParameterRequestInterceptor(accessToken, "oauth_token");
		}
	};
	
	public abstract ClientHttpRequestInterceptor interceptor(String accessToken, OAuth2Version oauth2Version);

}
