package org.springframework.social.facebook.support.extractors;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.social.facebook.Comment;
import org.springframework.social.facebook.FeedEntry;
import org.springframework.social.facebook.Reference;

public class FeedEntryResponseExtractor extends AbstractResponseExtractor<FeedEntry> {

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
		List<Comment> comments = ResponseExtractors.COMMENT_EXTRACTOR.extractObjects(commentsList);
		if (comments != null) {
			builder.comments(comments);
		}
		return builder.build();
	}

}
