package org.springframework.social.facebook.support.extractors;

import java.util.Date;
import java.util.Map;

import org.springframework.social.facebook.Tag;

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
