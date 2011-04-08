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

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.social.facebook.types.Reference;
import org.springframework.social.facebook.types.Tag;
import org.springframework.social.facebook.types.Video;

public class VideoResponseExtractor extends AbstractResponseExtractor<Video> {

	private TagResponseExtractor tagExtractor;

	public VideoResponseExtractor() {
		tagExtractor = new TagResponseExtractor();
	}
	
	@SuppressWarnings("unchecked")
	public Video extractObject(Map<String, Object> videoMap) {
		String id = (String) videoMap.get("id");
		Reference from = extractReferenceFromMap((Map<String, Object>)videoMap.get("from"));
		String picture = (String) videoMap.get("picture");
		String embedHtml = (String) videoMap.get("embed_html");
		String icon = (String) videoMap.get("icon");
		String source = (String) videoMap.get("source");
		Date createdTime = toDate((String) videoMap.get("created_time"));
		Date updatedTime = toDate((String) videoMap.get("updated_time"));
		Video.Builder builder = new Video.Builder(id, from, picture, embedHtml, icon, source, createdTime, updatedTime)
			.name((String) videoMap.get("name"))
			.description((String) videoMap.get("description"))
			.tags(extractTags(videoMap));
		return builder.build();
	}

	@SuppressWarnings("unchecked")
	private List<Tag> extractTags(Map<String, Object> videoMap) {				
		Map<String, Object> tagsMap = (Map<String, Object>) videoMap.get("tags");
		if(tagsMap == null) {
			return null;
		}
		
		List<Map<String, Object>> tagsList = (List<Map<String, Object>>) tagsMap.get("data");
		return tagExtractor.extractObjects(tagsList);
	}

}
