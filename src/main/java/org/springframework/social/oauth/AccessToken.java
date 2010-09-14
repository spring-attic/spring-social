package org.springframework.social.oauth;

public class AccessToken {
	private final String value;
	private final String providerId;
	private final String secret;

	public AccessToken(String value, String providerId) {
		this(value, null, providerId);
	}

	public AccessToken(String value, String secret, String providerId) {
		this.value = value;
		this.providerId = providerId;
		this.secret = secret;
	}

	public String getValue() {
		return value;
	}

	public String getProviderId() {
		return providerId;
	}

	public String getSecret() {
		return secret;
	}
}
