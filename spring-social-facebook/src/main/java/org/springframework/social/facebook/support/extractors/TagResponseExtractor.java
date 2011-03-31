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
import java.util.Map;

import org.springframework.social.facebook.types.Tag;

public class TagResponseExtractor extends AbstractResponseExtractor<Tag> {

	public Tag extractObject(Map<String, Object> tagMap) {
		String id = (String) tagMap.get("id");
		String name = (String) tagMap.get("name");
		Object xObject = tagMap.get("x");
		Integer x = xObject != null ? ((Number) xObject).intValue() : null;
		Object yObject = tagMap.get("y");
		Integer y = yObject != null ? ((Number) yObject).intValue() : null;
		Date createdTime = toDate((String) tagMap.get("created_time"));		
		return new Tag(id, name, x, y, createdTime);
	}
	
}
