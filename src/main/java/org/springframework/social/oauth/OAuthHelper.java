package org.springframework.social.oauth;

import java.net.MalformedURLException;
import java.util.Map;

import org.springframework.http.HttpMethod;
import org.springframework.security.oauth.consumer.token.OAuthConsumerToken;

public interface OAuthHelper {
	String buildAuthorizationHeader(HttpMethod method, String url, String providerId, Map<String, String> parameters)
			throws MalformedURLException;

	OAuthConsumerToken resolveAccessToken(String resourceId);
}
