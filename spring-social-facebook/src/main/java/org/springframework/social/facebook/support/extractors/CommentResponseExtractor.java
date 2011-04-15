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

import java.util.List;
import java.util.Map;

import org.springframework.social.facebook.types.Comment;
import org.springframework.social.facebook.types.Reference;

public class CommentResponseExtractor extends AbstractResponseExtractor<Comment> {

	@SuppressWarnings("unchecked")
	public Comment extractObject(Map<String, Object> commentMap) {
		String id = (String) commentMap.get("id");
		String message = (String) commentMap.get("message");
		Map<String, String> fromMap = (Map<String, String>) commentMap.get("from");
		String fromId = fromMap.get("id");
		String fromName = fromMap.get("name");
		String createdTimeAsString = (String) commentMap.get("created_time");
		Object likesObject = commentMap.get("likes");
		if(likesObject instanceof Integer) {
			// comment likes are usually just a count
			Integer likesCount = (Integer) likesObject;
			return new Comment(id, new Reference(fromId, fromName), message, toDate(createdTimeAsString));
		} else {
			// but sometimes (as in the case of a checkin comment), the likes are a list of user references 
			Map<String, Object> likesMap = (Map<String, Object>) likesObject;
			List<Reference> likes = extractReferences(likesMap);
			return new Comment(id, new Reference(fromId, fromName), message, toDate(createdTimeAsString));
		}
	}

}
