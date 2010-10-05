package org.springframework.social.core;

public interface SocialProviderOperations {
	String getProfileId();

	String getProfileUrl();

	void setStatus(String status);
}
