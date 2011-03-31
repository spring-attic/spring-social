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

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.social.facebook.types.Checkin;
import org.springframework.social.facebook.types.Location;
import org.springframework.social.facebook.types.Reference;

public class CheckinResponseExtractor extends AbstractResponseExtractor<Checkin> {

	private CommentResponseExtractor commentExtractor;

	public CheckinResponseExtractor() {
		commentExtractor = new CommentResponseExtractor();
	}
	
	public Checkin extractObject(Map<String, Object> checkinMap) {
		String id = (String) checkinMap.get("id");
		Location place = extractLocationFromMap((Map<String, Object>) checkinMap.get("place"));
		Reference from = extractReferenceFromMap((Map<String, Object>) checkinMap.get("from"));
		Reference application = extractReferenceFromMap((Map<String, Object>) checkinMap.get("application"));
		Date createdTime = toDate((String) checkinMap.get("created_time"));
		Map<String, Object> commentsMap = (Map<String, Object>) checkinMap.get("comments");
		List<Map<String, Object>> commentsList = (List<Map<String, Object>>) (commentsMap != null ? 
				commentsMap.get("data") : Collections.emptyList());
		return new Checkin.Builder(id, place, from, application, createdTime)
			.message((String) checkinMap.get("message"))
			.comments(commentExtractor.extractObjects(commentsList))
			.likes(extractReferences((Map<String, Object>) checkinMap.get("likes")))
			.tags(extractReferences((Map<String, Object>) checkinMap.get("tags"))).build();
	}

	private Location extractLocationFromMap(Map<String, Object> map) {
		String id = (String) map.get("id");
		String name = (String) map.get("name");
		Map<String, Object> locationDetailsMap = (Map<String, Object>) map.get("location");
		double latitude = (Double) locationDetailsMap.get("latitude");
		double longitude = (Double) locationDetailsMap.get("longitude");
		return new Location.Builder(id, name, latitude, longitude)
			.street((String) locationDetailsMap.get("street"))
			.city((String) locationDetailsMap.get("city"))
			.state((String) locationDetailsMap.get("state"))
			.country((String) locationDetailsMap.get("country"))
			.zip((String) locationDetailsMap.get("zip")).build();
	}
}
