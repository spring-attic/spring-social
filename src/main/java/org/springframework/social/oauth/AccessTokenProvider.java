package org.springframework.social.oauth;

public interface AccessTokenProvider<T> {
	T getAccessToken();
}
