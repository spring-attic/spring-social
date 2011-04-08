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
package org.springframework.social.twitter;

import java.util.List;

import org.springframework.social.twitter.support.extractors.ResponseExtractor;
import org.springframework.util.MultiValueMap;

// TODO: Consider a finding a better name for this interface

/**
 * Low level interface defining generic operations for interacting with Twitter's REST API.
 */
public interface LowLevelTwitterApi {
	
	<T> T fetchObject(String path, ResponseExtractor<T> extractor, Object... params);
	
	<T> List<T> fetchObjects(String path, ResponseExtractor<T> extractor, Object... params);
	
	<T> List<T> fetchObjects(String path, String jsonPath, ResponseExtractor<T> extractor, Object... params);

	void publish(String path, MultiValueMap<String, Object> data, Object... params);
	
	<T> T publish(String path, MultiValueMap<String, Object> data, ResponseExtractor<T> extractor, Object... params);

	void delete(String path, Object... params);
}
