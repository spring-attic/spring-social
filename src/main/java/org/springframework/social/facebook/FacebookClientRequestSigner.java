package org.springframework.social.facebook;

import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.social.oauth.AccessToken;
import org.springframework.social.oauth.AccessTokenServices;
import org.springframework.social.oauth2.OAuth2ParameterClientRequestSigner;

// TODO: There's nothing really Facebook-specific about this. Need to figure out where it should go
public class FacebookClientRequestSigner extends OAuth2ParameterClientRequestSigner {
	private final AccessTokenServices accessTokenServices;
	private String providerId;

	public FacebookClientRequestSigner(AccessTokenServices accessTokenServices, String providerId) {
		this.accessTokenServices = accessTokenServices;
		this.providerId = providerId;
		this.setParameterName("access_token");
	}

	protected AccessToken resolveAccessToken() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication == null) {
			throw new AuthenticationCredentialsNotFoundException("No credentials found");
		}

		return accessTokenServices.getToken(providerId, authentication.getPrincipal());
	}
}
