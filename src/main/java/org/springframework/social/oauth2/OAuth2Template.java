package org.springframework.social.oauth2;

public interface OAuth2Template<T> {
	T resolveAccessToken();
}
