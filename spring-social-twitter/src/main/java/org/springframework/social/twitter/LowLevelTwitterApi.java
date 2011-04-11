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
import java.util.Map;

import org.springframework.social.twitter.support.extractors.ResponseExtractor;
import org.springframework.util.MultiValueMap;

// TODO: Consider a finding a better name for this interface

/**
 * Low level interface defining generic operations for interacting with Twitter's REST API.
 * Operations in this interface work with paths relative to the Twitter REST API's base path of https://api.twitter.com/1/.
 */
public interface LowLevelTwitterApi {
	/**
	 * Fetches a single object from the given path.
	 * @param path the relative path to the resource.  
	 * @param extractor an extractor used to extract the object into a specific type.
	 * @return an Java object representing the requested Twitter resource.
	 */
	<T> T fetchObject(String path, ResponseExtractor<T> extractor);

	/**
	 * Fetches a single object from the given path.
	 * @param path the relative path to the resource.  
	 * @param extractor an extractor used to extract the object into a specific type.
	 * @param queryParams parameters to fill in the template placeholders, if any.
	 * @return an Java object representing the requested Twitter resource.
	 */
	<T> T fetchObject(String path, ResponseExtractor<T> extractor, Map<String, String> queryParams);
	
	/**
	 * Fetches a list of objects from the given path.
	 * @param path the relative path to the resource. 
	 * @param extractor an extractor used to extract the response into a specific type.
	 * @return a list of Java objects representing the requested Twitter resource.
	 */
	<T> List<T> fetchObjects(String path, ResponseExtractor<T> extractor);
	
	/**
	 * Fetches a list of objects from the given path.
	 * @param path the relative path to the resource. 
	 * @param extractor an extractor used to extract the response into a specific type.
	 * @param params parameters to fill in the template placeholders, if any.
	 * @return a list of Java objects representing the requested Twitter resource.
	 */
	<T> List<T> fetchObjects(String path, ResponseExtractor<T> extractor, Map<String, String> params);

	/**
	 * Fetches a list of objects from the given path.
	 * @param path the relative path to the resource. 
	 * @param jsonProperty the property in the JSON response that contains the list of objects. 
	 * @param extractor an extractor used to extract the response into a specific type.
	 * @return a list of Java objects representing the requested Twitter resource.
	 */
	<T> List<T> fetchObjects(String path, String jsonProperty, ResponseExtractor<T> extractor);

	<T> T fetchObject(String path, Class<T> type);

	<T> T fetchObject(String path, Class<T> type, Map<String, String> params);

	/**
	 * Fetches an image an array of bytes from the given path.
	 * @param path the relative path to the resource. 
	 * @return an array of bytes containing the requested image.
	 */
	byte[] fetchImage(String path);
	
	/**
	 * Publishes data to the Twitter REST API. Does not return any results.
	 * @param path the relative path to the resource. 
	 * @param data the data to be posted.
	 */
	void publish(String path, MultiValueMap<String, Object> data);
	
	/**
	 * Publishes data to the Twitter REST API. Does not return any results.
	 * @param path the relative path to the resource. 
	 * @param data the data to be posted.
	 * @param extractor an extractor used to extract the response into a specific type.
	 * @return a Java object representing the response after publishing.
	 */
	<T> T publish(String path, MultiValueMap<String, Object> data, ResponseExtractor<T> extractor);

	/**
	 * Publishes data to the Twitter REST API. Does not return any results.
	 * @param path the relative path to the resource. 
	 * @param data the data to be posted.
	 * @param extractor an extractor used to extract the response into a specific type.
	 * @param params parameters to fill in the template placeholders, if any.
	 * @return a Java object representing the response after publishing.
	 */
	<T> T publish(String path, MultiValueMap<String, Object> data, ResponseExtractor<T> extractor, Map<String, String> params);

	/**
	 * Deletes a resource.
	 * @param path the relative path to the resource. 
	 */
	void delete(String path);

	/**
	 * Deletes a resource.
	 * @param path the relative path to the resource. 
	 * @param queryParams parameters to fill in the template placeholders, if any.
	 */
	void delete(String path, Map<String, String> queryParams);
	
}
