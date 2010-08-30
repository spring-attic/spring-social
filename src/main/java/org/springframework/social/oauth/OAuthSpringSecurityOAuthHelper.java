package org.springframework.social.oauth;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

import org.springframework.http.HttpMethod;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth.consumer.OAuthConsumerSupport;
import org.springframework.security.oauth.consumer.ProtectedResourceDetails;
import org.springframework.security.oauth.consumer.ProtectedResourceDetailsService;
import org.springframework.security.oauth.consumer.token.OAuthConsumerToken;
import org.springframework.social.account.Account;

public class OAuthSpringSecurityOAuthHelper implements OAuthHelper {
	private final OAuthConsumerSupport oauthSupport;
	private final ProtectedResourceDetailsService resourceDetailsService;
	private final OAuthConsumerTokenServices tokenServices;

	public OAuthSpringSecurityOAuthHelper(OAuthConsumerSupport oauthSupport,
			ProtectedResourceDetailsService resourceDetailsService, OAuthConsumerTokenServices tokenServices) {
		this.oauthSupport = oauthSupport;
		this.resourceDetailsService = resourceDetailsService;
		this.tokenServices = tokenServices;
	}

	public String buildAuthorizationHeader(HttpMethod method, String url, String providerId,
			Map<String, String> parameters) throws MalformedURLException {

		OAuthConsumerToken accessToken = resolveAccessToken(providerId);
		ProtectedResourceDetails details = resourceDetailsService.loadProtectedResourceDetailsById(providerId);
		String authorizationHeader = oauthSupport.getAuthorizationHeader(details, (OAuthConsumerToken) accessToken,
				new URL(url), method.name(), parameters);
		return authorizationHeader;
	}

	public OAuthConsumerToken resolveAccessToken(String resourceId) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		System.out.println(authentication.getClass());
		Account account = (Account) authentication.getPrincipal();
		return tokenServices.getToken(resourceId, account.getId());
	}
}
