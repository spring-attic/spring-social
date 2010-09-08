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

public class SSOAuth1Template implements OAuth1Template {
	private final OAuthConsumerSupport oauthSupport;
	private final SSOAuthAccessTokenServices tokenServices;
	private final ProtectedResourceDetails protectedResourceDetails;

	public SSOAuth1Template(OAuthConsumerSupport oauthSupport,
			ProtectedResourceDetails protectedResourceDetails, SSOAuthAccessTokenServices tokenServices) {
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

		OAuthConsumerToken accessToken = tokenServices.getToken(protectedResourceDetails.getId(),
				authentication.getPrincipal());
		return accessToken;
	}
}
