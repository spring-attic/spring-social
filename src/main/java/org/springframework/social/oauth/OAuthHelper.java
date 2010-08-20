package org.springframework.social.oauth;

import java.net.MalformedURLException;
import java.util.Map;

import org.springframework.http.HttpMethod;

public interface OAuthHelper {
	String buildAuthorizationHeader(AccessTokenProvider<?> accessTokenProvider, HttpMethod method, String url,
			String providerId, Map<String, String> parameters) throws MalformedURLException;
}
