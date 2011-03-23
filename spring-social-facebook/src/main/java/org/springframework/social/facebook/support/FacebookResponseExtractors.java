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
package org.springframework.social.facebook.support;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.springframework.social.facebook.Comment;
import org.springframework.social.facebook.FeedEntry;
import org.springframework.social.facebook.Reference;
import org.springframework.social.facebook.UserLike;

class FacebookResponseExtractors {
	private FacebookResponseExtractors() {
	}

	public static FeedEntry extractFeedEntryFromResponseMap(Map<String, Object> feedEntryMap) {
		String id = (String) feedEntryMap.get("id");
		Map<String, String> fromMap = (Map<String, String>) feedEntryMap.get("from");
		String fromId = fromMap.get("id");
		String fromName = fromMap.get("name");
		String message = (String) feedEntryMap.get("message");
		String createdTimeAsString = (String) feedEntryMap.get("created_time");
		String updatedTimeAsString = (String) feedEntryMap.get("updated_time");
		Map<String, Object> likesMap = (Map<String, Object>) feedEntryMap.get("likes");
		Map<String, Object> commentsMap = (Map<String, Object>) feedEntryMap.get("comments");
		FeedEntry feedEntry = new FeedEntry(id, new Reference(fromId, fromName), message, toDate(createdTimeAsString),
				toDate(updatedTimeAsString), extractLikes(likesMap), extractCommentsFromResponseList(commentsMap));
		return feedEntry;
	}

	public static List<Comment> extractCommentsFromResponseList(Map<String, Object> commentsMap) {
		if (commentsMap == null) {
			return Collections.emptyList();
		}

		List<Map<String, Object>> commentEntries = (List<Map<String, Object>>) commentsMap.get("data");
		List<Comment> comments = new ArrayList<Comment>(commentEntries.size());
		for (Map<String, Object> commentEntry : commentEntries) {
			Comment comment = extractCommentFromResponseMap(commentEntry);
			comments.add(comment);
		}
		return Collections.unmodifiableList(comments);
	}

	public static Comment extractCommentFromResponseMap(Map<String, Object> commentEntry) {
		String id = (String) commentEntry.get("id");
		String message = (String) commentEntry.get("message");
		Map<String, String> fromMap = (Map<String, String>) commentEntry.get("from");
		String fromId = fromMap.get("id");
		String fromName = fromMap.get("name");
		String createdTimeAsString = (String) commentEntry.get("created_time");
		Map<String, Object> likesMap = (Map<String, Object>) commentEntry.get("likes");
		List<Reference> likes = extractLikes(likesMap);
		Comment comment = new Comment(id, message, toDate(createdTimeAsString), new Reference(fromId, fromName), likes);
		return comment;
	}

	public static List<Reference> extractLikes(Map<String, Object> likesMap) {
		if (likesMap == null) {
			return Collections.emptyList();
		}

		List<Map<String, String>> likeEntries = (List<Map<String, String>>) likesMap.get("data");
		List<Reference> likes = new ArrayList<Reference>(likeEntries.size());
		for (Map<String, String> likeEntry : likeEntries) {
			likes.add(new Reference(likeEntry.get("id"), likeEntry.get("name")));
		}
		return Collections.unmodifiableList(likes);
	}

	private static final DateFormat FB_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.ENGLISH);

	private static Date toDate(String dateString) {
		try {
			return FB_DATE_FORMAT.parse(dateString);
		} catch (ParseException e) {
			return null;
		}
	}

	public static List<UserLike> extractUserLikes(Map<String, Object> likesMap) {
		if (likesMap == null) {
			return Collections.emptyList();
		}

		List<Map<String, String>> likeEntries = (List<Map<String, String>>) likesMap.get("data");
		List<UserLike> likes = new ArrayList<UserLike>(likeEntries.size());
		for (Map<String, String> likeEntry : likeEntries) {
			likes.add(new UserLike(likeEntry.get("id"), likeEntry.get("name"), likeEntry.get("category"),
					toDate(likeEntry.get("created_time"))));
		}
		return Collections.unmodifiableList(likes);
	}
}
