package org.springframework.social.oauth1;

import java.net.URL;
import java.util.Map;

import org.springframework.http.HttpMethod;

public interface OAuth1Template {
	String buildAuthorizationHeader(HttpMethod method, URL url, Map<String, String> parameters);
}
