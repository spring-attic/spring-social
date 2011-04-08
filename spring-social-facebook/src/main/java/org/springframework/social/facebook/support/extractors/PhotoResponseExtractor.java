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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.social.facebook.types.Photo;
import org.springframework.social.facebook.types.Photo.Builder;
import org.springframework.social.facebook.types.Photo.Image;
import org.springframework.social.facebook.types.Reference;
import org.springframework.social.facebook.types.Tag;

public class PhotoResponseExtractor extends AbstractResponseExtractor<Photo> {
	
	private TagResponseExtractor tagExtractor;

	public PhotoResponseExtractor() {
		tagExtractor = new TagResponseExtractor();
	}
	
	@SuppressWarnings("unchecked")
	public Photo extractObject(Map<String, Object> photoMap) {
		String id = (String) photoMap.get("id");
		Reference from = extractReferenceFromMap((Map<String, Object>)photoMap.get("from"));
		String link = (String) photoMap.get("link");
		String icon = (String) photoMap.get("icon");
		Date createdTime = toDate((String) photoMap.get("created_time"));		
		List<Image> images = extractImages(photoMap);
		Builder builder = new Photo.Builder(id, from, link, icon, createdTime, images);
		builder.name((String) photoMap.get("name"));
		builder.updatedTime(toDate((String) photoMap.get("updated_time")));
		builder.tags(extractTags(photoMap));
		builder.position((Integer) photoMap.get("position"));
		return builder.build();
	}

	@SuppressWarnings("unchecked")
	private List<Tag> extractTags(Map<String, Object> photoMap) {				
		Map<String, Object> tagsMap = (Map<String, Object>) photoMap.get("tags");
		if(tagsMap == null) {
			return null;
		}
		
		List<Map<String, Object>> tagsList = (List<Map<String, Object>>) tagsMap.get("data");
		return tagExtractor.extractObjects(tagsList);
	}

	@SuppressWarnings("unchecked")
	private List<Image> extractImages(Map<String, Object> responseMap) {
		List<Map<String,Object>> imagesList = (List<Map<String,Object>>) responseMap.get("images");
		List<Image> images = new ArrayList<Image>(imagesList.size());
		for (Map<String, Object> imageMap : imagesList) {
			images.add(new Image((String) imageMap.get("source"), (Integer) imageMap.get("width"), (Integer) imageMap.get("height")));
		}
		return images;
	}
}
