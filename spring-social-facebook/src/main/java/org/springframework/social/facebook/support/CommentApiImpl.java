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

import java.util.List;

import org.springframework.social.facebook.CommentApi;
import org.springframework.social.facebook.support.extractors.CommentResponseExtractor;
import org.springframework.social.facebook.support.extractors.ReferenceResponseExtractor;
import org.springframework.social.facebook.types.Comment;
import org.springframework.social.facebook.types.Reference;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

public class CommentApiImpl extends AbstractFacebookApi implements CommentApi {
	private final CommentResponseExtractor commentsExtractor;

	private final ReferenceResponseExtractor referenceExtractor;

	public CommentApiImpl(RestTemplate restTemplate) {
		super(restTemplate);
		commentsExtractor = new CommentResponseExtractor();
		referenceExtractor = new ReferenceResponseExtractor();
	}

	public List<Comment> getComments(String objectId) {
		return getObjectConnection(objectId, "comments", commentsExtractor);
	}

	public Comment getComment(String commentId) {
		return getObject(commentId, commentsExtractor);
	}

	public String addComment(String objectId, String message) {
		MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
		map.set("message", message);
		return (String) publish(objectId, "comments", map).get("id");
	}

	public void deleteComment(String objectId) {
		delete(objectId);
	}

	public List<Reference> getLikes(String objectId) {
		return getObjectConnection(objectId, "likes", referenceExtractor);
	}

	public void like(String objectId) {
		MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
		post(objectId, "likes", map);
	}

	public void unlike(String objectId) {
		delete(objectId, "likes");
	}

}
