package org.springframework.social.facebook.support.extractors;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.social.facebook.Reference;
import org.springframework.social.facebook.Tag;
import org.springframework.social.facebook.Video;

public class VideoResponseExtractor extends AbstractResponseExtractor<Video> {

	public Video extractObject(Map<String, Object> videoMap) {
		String id = (String) videoMap.get("id");
		Reference from = ResponseExtractors.REFERENCE_EXTRACTOR.extractObject((Map<String, Object>)videoMap.get("from"));
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

	private List<Tag> extractTags(Map<String, Object> videoMap) {				
		Map<String, Object> tagsMap = (Map<String, Object>) videoMap.get("tags");
		if(tagsMap == null) {
			return null;
		}
		
		List<Map<String, Object>> tagsList = (List<Map<String, Object>>) tagsMap.get("data");
		return ResponseExtractors.TAG_EXTRACTOR.extractObjects(tagsList);
	}

}
