package org.springframework.social.twitter;

import java.util.List;

import org.springframework.social.twitter.support.extractors.ResponseExtractor;
import org.springframework.util.MultiValueMap;

// TODO : Needs a better name
public interface TwitterRequestApi {
	
	<T> T fetchObject(String path, ResponseExtractor<T> extractor, Object... params);
	
	<T> List<T> fetchObjects(String path, ResponseExtractor<T> extractor, Object... params);
	
	<T> List<T> fetchObjects(String path, String jsonPath, ResponseExtractor<T> extractor, Object... params);

	void publish(String path, MultiValueMap<String, Object> data, Object... params);
	
	<T> T publish(String path, MultiValueMap<String, Object> data, ResponseExtractor<T> extractor, Object... params);

	void delete(String path, Object... params);
}
