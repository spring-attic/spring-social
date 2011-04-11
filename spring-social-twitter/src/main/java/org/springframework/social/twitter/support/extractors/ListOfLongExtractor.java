package org.springframework.social.twitter.support.extractors;

import java.util.List;
import java.util.Map;

import org.springframework.social.twitter.support.CollectionUtils;

public class ListOfLongExtractor extends AbstractResponseExtractor<List<Long>> {

	private final String jsonPath;

	public ListOfLongExtractor(String jsonPath) {
		this.jsonPath = jsonPath;
	}
	
	@SuppressWarnings("unchecked")
	public List<Long> extractObject(Map<String, Object> responseMap) {
		List<Number> list = (List<Number>) responseMap.get(jsonPath);
		return CollectionUtils.asLongList(list);
	}
	
}
