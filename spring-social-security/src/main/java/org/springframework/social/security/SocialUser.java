package org.springframework.social.security;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

/**
 * simple utility class that uses {@link User#getUsername()} as {@link SocialUserDetails#getUserId()}
 * for SocialUserDetails
 *
 * @author stf@molindo.at
 */
public class SocialUser extends User implements SocialUserDetails {

	private static final long serialVersionUID = 1L;

	public SocialUser(String username, String password, boolean enabled, boolean accountNonExpired,
			boolean credentialsNonExpired, boolean accountNonLocked, Collection<? extends GrantedAuthority> authorities) {
		super(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
	}

	public SocialUser(String username, String password, Collection<? extends GrantedAuthority> authorities) {
		super(username, password, authorities);
	}

	public String getUserId() {
		return getUsername();
	}
}
