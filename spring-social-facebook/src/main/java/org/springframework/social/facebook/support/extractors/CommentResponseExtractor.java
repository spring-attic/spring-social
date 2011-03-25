package org.springframework.social.facebook.support.extractors;

import java.util.List;
import java.util.Map;

import org.springframework.social.facebook.Comment;
import org.springframework.social.facebook.Reference;

public class CommentResponseExtractor extends AbstractResponseExtractor<Comment> {

	public Comment extractObject(Map<String, Object> commentMap) {
		String id = (String) commentMap.get("id");
		String message = (String) commentMap.get("message");
		Map<String, String> fromMap = (Map<String, String>) commentMap.get("from");
		String fromId = fromMap.get("id");
		String fromName = fromMap.get("name");
		String createdTimeAsString = (String) commentMap.get("created_time");
		Map<String, Object> likesMap = (Map<String, Object>) commentMap.get("likes");
		List<Reference> likes = extractReferences(likesMap);
		return new Comment(id, new Reference(fromId, fromName), message, toDate(createdTimeAsString), likes);
	}

}
