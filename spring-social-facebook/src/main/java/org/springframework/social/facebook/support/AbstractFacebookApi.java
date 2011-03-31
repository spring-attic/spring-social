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
package org.springframework.social.facebook.support;

import java.util.List;
import java.util.Map;

import org.springframework.social.facebook.support.extractors.ReferenceResponseExtractor;
import org.springframework.social.facebook.support.extractors.ResponseExtractor;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

public abstract class AbstractFacebookApi {

	private final RestTemplate restTemplate;
	
	protected final ReferenceResponseExtractor referenceExtractor;

	AbstractFacebookApi(RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
		this.referenceExtractor = new ReferenceResponseExtractor();
	}

	protected <T> T getObject(String objectId, ResponseExtractor<T> extractor) {
		return extractor.extractObject( (Map<String, Object>) restTemplate.getForObject(OBJECT_URL, Map.class, objectId));
	}

	protected <T> List<T> getObjectConnection(String objectId, String connectionType, ResponseExtractor<T> extractor) {
		Map<String, Object> response = restTemplate.getForObject(CONNECTION_URL, Map.class, objectId, connectionType);
		return extractor.extractObjects((List<Map<String, Object>>) response.get("data"));
	}

	protected Map<String, Object> publish(String objectId, String connectionType, MultiValueMap<String, String> data) {
		MultiValueMap<String, String> requestData = new LinkedMultiValueMap<String, String>(data);
		return restTemplate.postForObject(CONNECTION_URL, requestData, Map.class, objectId, connectionType);
	}

	protected void post(String objectId, String connectionType, MultiValueMap<String, String> data) {
		MultiValueMap<String, String> requestData = new LinkedMultiValueMap<String, String>(data);
		restTemplate.postForObject(CONNECTION_URL, requestData, String.class, objectId, connectionType);
	}

	protected void delete(String objectId) {
		LinkedMultiValueMap<String, String> deleteRequest = new LinkedMultiValueMap<String, String>();
		deleteRequest.set("method", "delete");
		restTemplate.postForObject(OBJECT_URL, deleteRequest, String.class, objectId);
	}
	
	protected void delete(String objectId, String connectionType) {
		LinkedMultiValueMap<String, String> deleteRequest = new LinkedMultiValueMap<String, String>();
		deleteRequest.set("method", "delete");
		restTemplate.postForObject(CONNECTION_URL, deleteRequest, String.class, objectId, connectionType);
	}
	
	protected static final String OBJECT_URL = "https://graph.facebook.com/{objectId}";
	protected static final String CONNECTION_URL = OBJECT_URL + "/{connection}";

}
