package org.springframework.social.showcase;

public interface UserRepository {
	public ShowcaseUser findUserByUsername(String username);
}
