package org.springframework.social.gowalla;

import java.io.Serializable;

@SuppressWarnings("serial")
public class GowallaProfile implements Serializable {
	private final String id;
	private final String firstName;
	private final String lastName;
	private final String hometown;
	private final int pinsCount;
	private final int stampsCount;

	public GowallaProfile(String id, String firstName, String lastName, String hometown, int pinsCount, int stampsCount) {
		this.id = id;
		this.firstName = firstName;
		this.lastName = lastName;
		this.hometown = hometown;
		this.pinsCount = pinsCount;
		this.stampsCount = stampsCount;
	}

	public String getFirstName() {
		return firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public String getHometown() {
		return hometown;
	}

	public int getPinsCount() {
		return pinsCount;
	}

	public int getStampsCount() {
		return stampsCount;
	}

	public String getId() {
		return id;
	}
}
