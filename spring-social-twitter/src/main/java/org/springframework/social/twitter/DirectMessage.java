package org.springframework.social.twitter;

import java.util.Date;

public class DirectMessage {
	private long id;
	private String text;
	private long senderId;
	private String senderScreenName;
	private long recipientId;
	private String recipientScreenName;
	private Date createdAt;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public long getSenderId() {
		return senderId;
	}

	public void setSenderId(long senderId) {
		this.senderId = senderId;
	}

	public String getSenderScreenName() {
		return senderScreenName;
	}

	public void setSenderScreenName(String senderScreenName) {
		this.senderScreenName = senderScreenName;
	}

	public long getRecipientId() {
		return recipientId;
	}

	public void setRecipientId(long recipientId) {
		this.recipientId = recipientId;
	}

	public String getRecipientScreenName() {
		return recipientScreenName;
	}

	public void setRecipientScreenName(String recipientScreenName) {
		this.recipientScreenName = recipientScreenName;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

}
