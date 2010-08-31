package org.springframework.social.account;

public interface PasswordEncoder {
	
	String encode(String rawPassword);
	
	boolean matches(String rawPassword, String encodedPassword);
	
}
