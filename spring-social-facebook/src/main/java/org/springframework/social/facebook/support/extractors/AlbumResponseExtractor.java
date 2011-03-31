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

import org.springframework.social.facebook.types.Album;
import org.springframework.social.facebook.types.Reference;
import org.springframework.social.facebook.types.Album.Privacy;
import org.springframework.social.facebook.types.Album.Type;

public class AlbumResponseExtractor extends AbstractResponseExtractor<Album> {

	public Album extractObject(Map<String, Object> albumMap) {
		String id = (String) albumMap.get("id");
		Reference from = extractReferenceFromMap((Map<String, Object>) albumMap.get("from"));
		String name = (String) albumMap.get("name");
		Type type = Type.valueOf(((String) albumMap.get("type")).toUpperCase());
		String link = (String) albumMap.get("link");
		int count = (Integer) albumMap.get("count");
		Privacy privacy = Privacy.valueOf(((String) albumMap.get("privacy")).replace("-", "_").toUpperCase());
		Date createdTime = toDate((String) albumMap.get("created_time"));
		
		return new Album.Builder(id, from, name, type, link, count, privacy, createdTime)
			.description((String) albumMap.get("description"))
			.location((String) albumMap.get("location"))
			.updatedTime(toDate((String) albumMap.get("updated_time"))).build();
	}

}
