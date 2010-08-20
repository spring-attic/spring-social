package org.springframework.social.oauth;

public class SimpleAccessTokenProvider<T> implements AccessTokenProvider<T> {
	private T accessToken;

	public SimpleAccessTokenProvider(T accessToken) {
		this.accessToken = accessToken;
	}

	public T getAccessToken() {
		return this.accessToken;
	}
}
