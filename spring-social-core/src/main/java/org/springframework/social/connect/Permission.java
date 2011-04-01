package org.springframework.social.connect;

import java.io.Serializable;

@SuppressWarnings("serial")
public class Permission implements Serializable {
	
	private String name;
	
	public Permission(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	public boolean equals(Object o) {
		if (!(o instanceof Permission)) {
			return false;
		}
		Permission other = (Permission) o;
		return name.equals(other.name);
	}
	
	public int hashCode() {
		return name.hashCode();
	}
}
