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
package org.springframework.social.facebook.support.extractors;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.social.facebook.FeedEntry;
import org.springframework.social.facebook.types.Comment;
import org.springframework.social.facebook.types.Reference;

public class FeedEntryResponseExtractor extends AbstractResponseExtractor<FeedEntry> {

	private CommentResponseExtractor commentExtractor;

	public FeedEntryResponseExtractor() {
		commentExtractor = new CommentResponseExtractor();
	}
	
	public FeedEntry extractObject(Map<String, Object> feedEntryMap) {
		String id = (String) feedEntryMap.get("id");
		Map<String, String> fromMap = (Map<String, String>) feedEntryMap.get("from");
		Reference from = new Reference(fromMap.get("id"), fromMap.get("name"));
		String message = (String) feedEntryMap.get("message");
		Date createdTime = toDate((String) feedEntryMap.get("created_time"));
		Date updatedTime = toDate((String) feedEntryMap.get("updated_time"));
		FeedEntry.Builder builder = new FeedEntry.Builder(id, from, message, createdTime, updatedTime)
			.link((String) feedEntryMap.get("link"))
			.picture((String) feedEntryMap.get("picture"))
			.subject((String) feedEntryMap.get("subject"))
			.name((String) feedEntryMap.get("name"))
			.description((String) feedEntryMap.get("description"))
			.icon((String) feedEntryMap.get("icon"));
		
		List<Reference> likes = extractReferences((Map<String, Object>) feedEntryMap.get("likes"));
		if (likes != null) {
			builder.likes(likes);
		}
		Map<String, Object> commentsMap = (Map<String, Object>) feedEntryMap.get("comments");
		List<Map<String, Object>> commentsList = (List<Map<String, Object>>) (commentsMap != null ? commentsMap
				.get("data") : Collections.emptyList());
		List<Comment> comments = commentExtractor.extractObjects(commentsList);
		if (comments != null) {
			builder.comments(comments);
		}
		return builder.build();
	}

}
