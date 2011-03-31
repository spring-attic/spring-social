package org.springframework.social.facebook.support.extractors;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.social.facebook.Photo;
import org.springframework.social.facebook.Photo.Builder;
import org.springframework.social.facebook.Photo.Image;
import org.springframework.social.facebook.Reference;
import org.springframework.social.facebook.Tag;

public class PhotoResponseExtractor extends AbstractResponseExtractor<Photo> {
	public Photo extractObject(Map<String, Object> photoMap) {
		String id = (String) photoMap.get("id");
		Reference from = ResponseExtractors.REFERENCE_EXTRACTOR.extractObject((Map<String, Object>)photoMap.get("from"));
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

	private List<Tag> extractTags(Map<String, Object> photoMap) {				
		Map<String, Object> tagsMap = (Map<String, Object>) photoMap.get("tags");
		if(tagsMap == null) {
			return null;
		}
		
		List<Map<String, Object>> tagsList = (List<Map<String, Object>>) tagsMap.get("data");
		return ResponseExtractors.TAG_EXTRACTOR.extractObjects(tagsList);
	}

	private List<Image> extractImages(Map<String, Object> responseMap) {
		List<Map<String,Object>> imagesList = (List<Map<String,Object>>) responseMap.get("images");
		List<Image> images = new ArrayList<Image>(imagesList.size());
		for (Map<String, Object> imageMap : imagesList) {
			images.add(new Image((String) imageMap.get("source"), (Integer) imageMap.get("width"), (Integer) imageMap.get("height")));
		}
		return images;
	}
}
