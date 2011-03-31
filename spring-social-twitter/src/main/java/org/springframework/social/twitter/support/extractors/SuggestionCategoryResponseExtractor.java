package org.springframework.social.twitter.support.extractors;

import java.util.Map;

import org.springframework.social.twitter.types.SuggestionCategory;

public class SuggestionCategoryResponseExtractor extends AbstractResponseExtractor<SuggestionCategory> {

	public SuggestionCategory extractObject(Map<String, Object> categoryMap) {
		return new SuggestionCategory(
				String.valueOf(categoryMap.get("name")), 
				String.valueOf(categoryMap.get("slug")), 
				Integer.valueOf(String.valueOf(categoryMap.get("size"))));
	}
	
}
