package org.springframework.social.facebook.api;

import java.util.Date;
import java.util.List;

/**
 * Model class representing a thread that contain messages
 * 
 * @author leandro.soler
 * 
 */
public class FacebookThread {
	private String id;

	private String snippet;

	private Date updatedTime;

	private int messageCount;

	private int unreadCount;

	private List<String> tags;
	
	public FacebookThread(String id, 
			String snippet, Date updatedTime,
			int messageCount, int unreadCount, List<String> tags) {
		this.id = id;
		this.snippet = snippet;
		this.updatedTime = updatedTime;
		this.messageCount = messageCount;
		this.unreadCount = unreadCount;
		this.tags = tags;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getSnippet() {
		return snippet;
	}

	public void setSnippet(String snippet) {
		this.snippet = snippet;
	}

	public Date getUpdatedTime() {
		return updatedTime;
	}

	public void setUpdatedTime(Date updatedTime) {
		this.updatedTime = updatedTime;
	}

	public int getMessageCount() {
		return messageCount;
	}

	public void setMessageCount(int messageCount) {
		this.messageCount = messageCount;
	}

	public int getUnreadCount() {
		return unreadCount;
	}

	public void setUnreadCount(int unreadCount) {
		this.unreadCount = unreadCount;
	}

	public List<String> getTags() {
		return tags;
	}

	public void setTags(List<String> tags) {
		this.tags = tags;
	}

}
