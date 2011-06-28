package org.springframework.social.security;

import org.springframework.security.core.userdetails.UserDetails;

public interface SocialUserDetails extends UserDetails {

	/**
	 * might be same as {@link #getUsername()} if users are identified by username
	 * 
	 * @return user's id used to assign connections
	 */
	String getUserId();
	
}
