package org.springframework.social.facebook;

import org.springframework.social.oauth2.OAuth2ParameterClientRequestSigner;

public class FacebookRequestSigner extends OAuth2ParameterClientRequestSigner {
	private final String accessToken;

	public FacebookRequestSigner(String accessToken) {
		this.accessToken = accessToken;
		this.setParameterName("access_token");
	}

	protected String resolveAccessToken() {
		return accessToken;
	}

}
