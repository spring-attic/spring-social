package org.springframework.social.oauth;

// TODO: Could this be merged back into AccessTokenServices? Or OAuthTemplate?
public interface AccessTokenResolver<T> {
	T resolveAccessToken();
}
