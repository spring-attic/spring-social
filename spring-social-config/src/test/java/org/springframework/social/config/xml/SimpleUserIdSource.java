package org.springframework.social.config.xml;

public class SimpleUserIdSource implements UserIdSource {

	public String getUserId() {
		return "bob";
	}
	
}
