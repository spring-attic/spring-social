package org.springframework.social.facebook;

import java.util.Date;

public class Tag {
	private final String id;

	private final String name;
	
	private final Integer x;
	
	private final Integer y;
	
	private final Date createdTime;
	
	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public Integer getX() {
		return x;
	}

	public Integer getY() {
		return y;
	}

	public Date getCreatedTime() {
		return createdTime;
	}

	public Tag(String id, String name, Date createdTime) {
		this(id, name, null, null, createdTime);
	}

	public Tag(String id, String name, Integer x, Integer y, Date createdTime) {
		this.id = id;
		this.name = name;
		this.x = x;
		this.y = y;
		this.createdTime = createdTime;			
	}
}
