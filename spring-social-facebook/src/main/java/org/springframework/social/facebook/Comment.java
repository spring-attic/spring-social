package org.springframework.social.facebook;

import java.util.Date;
import java.util.List;

/**
 * Represents a comment.
 * @author Craig Walls
 */
public class Comment {
	private final String id;
	private final String message;
	private final Date createdTime;
	private final Reference from;
	private final List<Reference> likes;

	public Comment(String id, Reference from, String message, Date createdTime, List<Reference> likes) {
		this.id = id;
		this.from = from;
		this.message = message;
		this.createdTime = createdTime;
		this.likes = likes;
	}

	public String getId() {
		return id;
	}

	public String getMessage() {
		return message;
	}

	public Date getCreatedTime() {
		return createdTime;
	}

	public Reference getFrom() {
		return from;
	}

	public List<Reference> getLikes() {
		return likes;
	}

}
