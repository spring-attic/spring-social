package org.springframework.social.oauth1;

import java.net.URL;
import java.util.Map;

import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth.consumer.OAuthConsumerSupport;
import org.springframework.security.oauth.consumer.ProtectedResourceDetails;
import org.springframework.security.oauth.consumer.token.OAuthConsumerToken;
import org.springframework.social.oauth.AccessToken;
import org.springframework.social.oauth.AccessTokenServices;

public class SSOAuth1ClientRequestSigner extends OAuth1ClientRequestSigner {
	private final OAuthConsumerSupport oauthSupport;
	private final AccessTokenServices tokenServices;
	private final ProtectedResourceDetails protectedResourceDetails;

	public SSOAuth1ClientRequestSigner(OAuthConsumerSupport oauthSupport,
			ProtectedResourceDetails protectedResourceDetails, AccessTokenServices tokenServices) {
		this.oauthSupport = oauthSupport;
		this.tokenServices = tokenServices;
		this.protectedResourceDetails = protectedResourceDetails;
	}

	public String buildAuthorizationHeader(HttpMethod method, URL url, Map<String, String> parameters) {

		OAuthConsumerToken accessToken = resolveAccessToken();
		if (accessToken == null) {
			return null;
		}

		return oauthSupport.getAuthorizationHeader(protectedResourceDetails, accessToken, url, method.name(),
				parameters);
	}

	private OAuthConsumerToken resolveAccessToken() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication == null) {
			throw new AuthenticationCredentialsNotFoundException("No credentials found");
		}

		AccessToken accessToken = tokenServices.getToken(protectedResourceDetails.getId(),
				authentication.getPrincipal());

		if (accessToken == null) {
			return null;
		}

		OAuthConsumerToken consumerToken = new OAuthConsumerToken();
		consumerToken.setAccessToken(true);
		consumerToken.setValue(accessToken.getValue());
		consumerToken.setSecret(accessToken.getSecret());
		consumerToken.setResourceId(accessToken.getProviderId());

		return consumerToken;
	}
}
