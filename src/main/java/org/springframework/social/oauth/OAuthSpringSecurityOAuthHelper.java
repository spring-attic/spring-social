package org.springframework.social.oauth;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

import org.springframework.http.HttpMethod;
import org.springframework.security.oauth.consumer.OAuthConsumerSupport;
import org.springframework.security.oauth.consumer.ProtectedResourceDetails;
import org.springframework.security.oauth.consumer.ProtectedResourceDetailsService;
import org.springframework.security.oauth.consumer.token.OAuthConsumerToken;

public class OAuthSpringSecurityOAuthHelper implements OAuthHelper {
	private final OAuthConsumerSupport oauthSupport;
	private final ProtectedResourceDetailsService resourceDetailsService;

	public OAuthSpringSecurityOAuthHelper(OAuthConsumerSupport oauthSupport,
	        ProtectedResourceDetailsService resourceDetailsService) {
		this.oauthSupport = oauthSupport;
		this.resourceDetailsService = resourceDetailsService;
	}

	public String buildAuthorizationHeader(AccessTokenProvider<?> tokenProvider, HttpMethod method,
			String url, String providerId, Map<String, String> parameters) throws MalformedURLException {

		Object accessToken = tokenProvider.getAccessToken();
		if (!(accessToken instanceof OAuthConsumerToken)) {
			throw new IllegalArgumentException("The AccessTokenProvider must provide an OAuthConsumerToken.");
		}

		ProtectedResourceDetails details = resourceDetailsService.loadProtectedResourceDetailsById(providerId);
		String authorizationHeader = oauthSupport.getAuthorizationHeader(details, (OAuthConsumerToken) accessToken,
				new URL(url), method.name(), parameters);
		return authorizationHeader;
	}
}
