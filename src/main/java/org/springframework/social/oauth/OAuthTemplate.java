package org.springframework.social.oauth;

import java.net.MalformedURLException;
import java.util.Map;

import org.springframework.http.HttpMethod;

public interface OAuthTemplate {
	String buildAuthorizationHeader(HttpMethod method, String url, Map<String, String> parameters)
			throws MalformedURLException;
}
