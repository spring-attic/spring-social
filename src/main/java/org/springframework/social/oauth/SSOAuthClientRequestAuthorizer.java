package org.springframework.social.oauth;

import java.net.URL;
import java.util.Map;

import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth.consumer.OAuthConsumerSupport;
import org.springframework.security.oauth.consumer.ProtectedResourceDetails;
import org.springframework.security.oauth.consumer.token.OAuthConsumerToken;

public class SSOAuthClientRequestAuthorizer extends OAuth1ClientRequestAuthorizer {

	private final ProtectedResourceDetails protectedResourceDetails;
	private final OAuthConsumerSupport oauthSupport;
	private final SSOAuthAccessTokenServices tokenServices;

	public SSOAuthClientRequestAuthorizer(OAuthConsumerSupport oauthSupport,
			ProtectedResourceDetails protectedResourceDetails, SSOAuthAccessTokenServices tokenServices) {
		this.oauthSupport = oauthSupport;
		this.protectedResourceDetails = protectedResourceDetails;
		this.tokenServices = tokenServices;
	}

	public String buildAuthorizationHeader(HttpMethod method, URL url, Map<String, String> parameters) {

		OAuthConsumerToken accessToken = resolveAccessToken(protectedResourceDetails.getId());
		if (accessToken == null) {
			return null;
		}

		return oauthSupport.getAuthorizationHeader(protectedResourceDetails, accessToken, url, method.name(),
				parameters);
	}

	private OAuthConsumerToken resolveAccessToken(String providerId) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication == null) {
			throw new AuthenticationCredentialsNotFoundException("No credentials found");
		}

		OAuthConsumerToken accessToken = tokenServices.getToken(providerId, authentication.getPrincipal());
		return accessToken;
	}
}
