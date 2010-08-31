package org.springframework.social.oauth;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth.consumer.OAuthConsumerSupport;
import org.springframework.security.oauth.consumer.ProtectedResourceDetails;
import org.springframework.security.oauth.consumer.ProtectedResourceDetailsService;
import org.springframework.security.oauth.consumer.token.OAuthConsumerToken;

public class OAuthSpringSecurityOAuthTemplate implements OAuthTemplate {
	private final OAuthConsumerSupport oauthSupport;
	private final OAuthConsumerTokenServices tokenServices;
	private final ProtectedResourceDetails protectedResourceDetails;
	private final String providerId;

	public OAuthSpringSecurityOAuthTemplate(String providerId, OAuthConsumerSupport oauthSupport,
			ProtectedResourceDetailsService resourceDetailsService, OAuthConsumerTokenServices tokenServices) {
		this.providerId = providerId;
		this.oauthSupport = oauthSupport;
		this.tokenServices = tokenServices;

		// TODO - Can the resource details be resolved/injected without
		// injecting the resource details service?
		this.protectedResourceDetails = resourceDetailsService.loadProtectedResourceDetailsById(providerId);
	}

	public String buildAuthorizationHeader(HttpMethod method, String url, Map<String, String> parameters)
			throws MalformedURLException {

		OAuthConsumerToken accessToken = resolveAccessToken(providerId);
		if (accessToken == null) {
			return null;
		}

		return oauthSupport.getAuthorizationHeader(protectedResourceDetails, accessToken, new URL(url),
				method.name(), parameters);
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
