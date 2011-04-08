package org.springframework.social.twitter.support.extractors;

import java.util.List;
import java.util.Map;

public class ListOfLongExtractor extends AbstractResponseExtractor<List<Long>> {

	private final String jsonPath;

	public ListOfLongExtractor(String jsonPath) {
		this.jsonPath = jsonPath;
	}
	
	@SuppressWarnings("unchecked")
	public List<Long> extractObject(Map<String, Object> responseMap) {
		return (List<Long>) responseMap.get(jsonPath);
	}
	
}
