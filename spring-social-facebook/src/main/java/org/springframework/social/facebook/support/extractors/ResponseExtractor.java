package org.springframework.social.facebook.support.extractors;

import java.util.List;
import java.util.Map;

public interface ResponseExtractor<T> {

	T extractObject(Map<String, Object> responseMap);

	List<T> extractObjects(List<Map<String, Object>> responseList);

}
