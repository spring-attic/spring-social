package org.springframework.social.core;

public interface SocialOperations {
	String getProfileId();

	String getProfileUrl();

	void setStatus(String status);
}
