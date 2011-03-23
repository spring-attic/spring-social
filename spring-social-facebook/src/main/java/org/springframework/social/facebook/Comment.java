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
	private final Date createdDate;
	private final Reference from;
	private final List<Reference> likes;

	public Comment(String id, String message, Date createdDate, Reference from, List<Reference> likes) {
		this.id = id;
		this.message = message;
		this.createdDate = createdDate;
		this.from = from;
		this.likes = likes;
	}

	public String getId() {
		return id;
	}

	public String getMessage() {
		return message;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public Reference getFrom() {
		return from;
	}

	public List<Reference> getLikes() {
		return likes;
	}

}
