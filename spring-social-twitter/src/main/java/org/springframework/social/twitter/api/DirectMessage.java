/*
 * Copyright 2011 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.social.twitter.api;

import java.util.Date;

/**
 * Represents a direct message.
 * @author Craig Walls
 */
public class DirectMessage {
	private final long id;
	private final String text;
	private final long senderId;
	private final String senderScreenName;
	private final long recipientId;
	private final String recipientScreenName;
	private final Date createdAt;

	public DirectMessage(long id, String text, long senderId, String senderScreenName, long recipientId, String recipientScreenName, Date createdAt) {
		this.id = id;
		this.text = text;
		this.senderId = senderId;
		this.senderScreenName = senderScreenName;
		this.recipientId = recipientId;
		this.recipientScreenName = recipientScreenName;
		this.createdAt = createdAt;
	}
	
	public long getId() {
		return id;
	}

	public String getText() {
		return text;
	}

	public long getSenderId() {
		return senderId;
	}

	public String getSenderScreenName() {
		return senderScreenName;
	}

	public long getRecipientId() {
		return recipientId;
	}

	public String getRecipientScreenName() {
		return recipientScreenName;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

}
