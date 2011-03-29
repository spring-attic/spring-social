package org.springframework.social.connect.support;

import java.io.Serializable;

public class ServiceProviderConnectionMemento implements Serializable {
	
	private Serializable accountId;
	
	private String providerId;
	
	private Integer id;
	
	private String providerAccountId;
	
	private String profileName;
	
	private String profileUrl;
	
	private String profilePictureUrl;
	
	private boolean allowSignIn;
	
	private String accessToken;
	
	private String accessSecret;
	
	private String refreshToken;

	public ServiceProviderConnectionMemento(Serializable accountId, String providerId, Integer id, String providerAccountId,
			String profileName, String profileUrl, String profilePictureUrl,
			boolean allowSignin,
			String accessToken, String accessSecret, String refreshToken) {		
	}

	public Serializable getAccountId() {
		return accountId;
	}

	public String getProviderId() {
		return providerId;
	}

	public Integer getId() {
		return id;
	}

	public String getProviderAccountId() {
		return providerAccountId;
	}

	public String getProfileName() {
		return profileName;
	}

	public String getProfileUrl() {
		return profileUrl;
	}

	public String getProfilePictureUrl() {
		return profilePictureUrl;
	}

	public boolean isAllowSignIn() {
		return allowSignIn;
	}

	public String getAccessToken() {
		return accessToken;
	}

	public String getAccessSecret() {
		return accessSecret;
	}

	public String getRefreshToken() {
		return refreshToken;
	}
	
}
