package org.springframework.social.gowalla;

public class Checkin {
	private final String name;
	private final int count;

	public Checkin(String name, int count) {
		this.name = name;
		this.count = count;
	}

	public String getName() {
		return name;
	}

	public int getCount() {
		return count;
	}
}
