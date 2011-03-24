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
import org.springframework.social.facebook.EducationEntry;
import org.springframework.social.facebook.FacebookProfile;
import org.springframework.social.facebook.FeedEntry;
import org.springframework.social.facebook.Reference;
import org.springframework.social.facebook.UserLike;
import org.springframework.social.facebook.WorkEntry;

class FacebookResponseExtractors {
	private FacebookResponseExtractors() {
	}

	public static FacebookProfile extractUserProfileFromMap(Map<String, Object> profileMap) {
		long id = Long.valueOf(String.valueOf(profileMap.get("id")));
		String username = String.valueOf(profileMap.get("username"));
		String name = String.valueOf(profileMap.get("name"));
		String firstName = String.valueOf(profileMap.get("first_name"));
		String lastName = String.valueOf(profileMap.get("last_name"));
		String gender = String.valueOf(profileMap.get("gender"));
		String locale = String.valueOf(profileMap.get("locale"));
		return new FacebookProfile.Builder(id, username, name, firstName, lastName, gender, locale)
				.email((String) profileMap.get("email"))
				.link((String) profileMap.get("link"))
				.thirdPartyId((String) profileMap.get("third_party_id"))
				.timezone((Integer) profileMap.get("timezone"))
				.updatedTime(toDate((String) profileMap.get("updated_time")))
				.verified((Boolean) profileMap.get("verified"))
				.about((String) profileMap.get("about"))
				.bio((String) profileMap.get("bio"))
				.birthday((String) profileMap.get("birthday"))
				.location(extractReferenceFromMap((Map<String, String>) profileMap.get("location")))
				.hometown(extractReferenceFromMap((Map<String, String>) profileMap.get("hometown")))
				.interestedIn((List<String>) profileMap.get("interested_in"))
				.religion((String) profileMap.get("religion"))
				.political((String) profileMap.get("political"))
				.quotes((String) profileMap.get("quotes"))
				.relationshipStatus((String) profileMap.get("relationship_status"))
				.significantOther(extractReferenceFromMap((Map<String, String>) profileMap.get("significant_other")))
				.website((String) profileMap.get("website"))
				.work(extractWorkHistoryFromListOfMaps((List<Map<String, Object>>) profileMap.get("work")))
				.education(extractEducationHistoryFromListOfMaps((List<Map<String, Object>>) profileMap.get("education")))
				.build();
	}

	public static FeedEntry extractFeedEntryFromResponseMap(Map<String, Object> feedEntryMap) {
		String id = (String) feedEntryMap.get("id");
		Map<String, String> fromMap = (Map<String, String>) feedEntryMap.get("from");
		Reference from = new Reference(fromMap.get("id"), fromMap.get("name"));
		String message = (String) feedEntryMap.get("message");
		Date createdTime = toDate((String) feedEntryMap.get("created_time"));
		Date updatedTime = toDate((String) feedEntryMap.get("updated_time"));
		FeedEntry.Builder builder = new FeedEntry.Builder(id, from, message, createdTime, updatedTime);
		List<Reference> likes = extractLikes((Map<String, Object>) feedEntryMap.get("likes"));
		if (likes != null) {
			builder.likes(likes);
		}
		List<Comment> comments = extractCommentsFromResponseList((Map<String, Object>) feedEntryMap.get("comments"));
		if (comments != null) {
			builder.comments(comments);
		}
		return builder.build();
	}

	public static List<Comment> extractCommentsFromResponseList(Map<String, Object> commentsMap) {
		if (commentsMap == null) {
			return null;
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
		return new Comment(id, new Reference(fromId, fromName), message, toDate(createdTimeAsString), likes);
	}

	public static Reference extractReferenceFromMap(Map<String, String> referenceMap) {
		if (referenceMap == null) {
			return null;
		}
		return new Reference(referenceMap.get("id"), referenceMap.get("name"));
	}

	public static List<Reference> extractLikes(Map<String, Object> likesMap) {
		if (likesMap == null) {
			return null;
		}

		List<Map<String, String>> likeEntries = (List<Map<String, String>>) likesMap.get("data");
		List<Reference> likes = new ArrayList<Reference>(likeEntries.size());
		for (Map<String, String> likeEntry : likeEntries) {
			likes.add(new Reference(likeEntry.get("id"), likeEntry.get("name")));
		}
		return Collections.unmodifiableList(likes);
	}

	private static List<WorkEntry> extractWorkHistoryFromListOfMaps(List<Map<String, Object>> workHistoryList) {
		if (workHistoryList == null) {
			return null;
		}

		List<WorkEntry> work = new ArrayList<WorkEntry>(workHistoryList.size());
		for (Map<String, Object> workEntryMap : workHistoryList) {
			work.add(new WorkEntry(extractReferenceFromMap((Map<String, String>) workEntryMap.get("employer")),
					(String) workEntryMap.get("start_date"), (String) workEntryMap.get("end_date")));
		}
		return work;
	}
	
	private static List<EducationEntry> extractEducationHistoryFromListOfMaps(List<Map<String, Object>> educationList) {
		if(educationList == null) {
			return null;
		}
		
		List<EducationEntry> education = new ArrayList<EducationEntry>(educationList.size());
		for (Map<String, Object> educationEntryMap : educationList) {
			education.add(new EducationEntry(
					extractReferenceFromMap((Map<String, String>) educationEntryMap.get("school")), 
					extractReferenceFromMap((Map<String, String>) educationEntryMap.get("year")), 
					(String) educationEntryMap.get("type")));
		}
		return education;
	}


	private static final DateFormat FB_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.ENGLISH);

	private static Date toDate(String dateString) {
		if (dateString == null) {
			return null;
		}

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
