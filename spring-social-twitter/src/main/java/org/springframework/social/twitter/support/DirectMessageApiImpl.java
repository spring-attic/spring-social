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
package org.springframework.social.twitter.support;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.social.ResponseStatusCodeTranslator;
import org.springframework.social.SocialException;
import org.springframework.social.twitter.DirectMessageApi;
import org.springframework.social.twitter.TwitterTemplate;
import org.springframework.social.twitter.support.extractors.DirectMessageResponseExtractor;
import org.springframework.social.twitter.types.DirectMessage;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

/**
 * Implementation of {@link DirectMessageApi}, providing a binding to Twitter's direct message-oriented REST resources.
 * @author Craig Walls
 */
public class DirectMessageApiImpl implements DirectMessageApi {

	private final RestTemplate restTemplate;
	private final ResponseStatusCodeTranslator statusCodeTranslator;
	private DirectMessageResponseExtractor directMessageExtractor;

	public DirectMessageApiImpl(RestTemplate restTemplate, ResponseStatusCodeTranslator statusCodeTranslator) {
		this.restTemplate = restTemplate;
		this.statusCodeTranslator = statusCodeTranslator;
		this.directMessageExtractor = new DirectMessageResponseExtractor();
	}

	public List<DirectMessage> getDirectMessagesReceived() {
		List<Map<String, Object>> response = restTemplate.getForObject(DIRECT_MESSAGES_RECEIVED_URL, List.class);
		return directMessageExtractor.extractObjects((List<Map<String, Object>>) response);
	}

	public List<DirectMessage> getDirectMessagesSent() {
		List<Map<String, Object>> response = restTemplate.getForObject(DIRECT_MESSAGES_SENT_URL, List.class);
		return directMessageExtractor.extractObjects((List<Map<String, Object>>) response);
	}

	public void sendDirectMessage(String toScreenName, String text) {
		MultiValueMap<String, Object> dmParams = new LinkedMultiValueMap<String, Object>();
		dmParams.add("screen_name", toScreenName);
		sendDirectMessage(text, dmParams);
	}

	public void sendDirectMessage(long toUserId, String text) {
		MultiValueMap<String, Object> dmParams = new LinkedMultiValueMap<String, Object>();
		dmParams.add("user_id", String.valueOf(toUserId));
		sendDirectMessage(text, dmParams);
	}

	public void deleteDirectMessage(long messageId) {
		restTemplate.delete(DESTROY_DIRECT_MESSAGE_URL, messageId);
	}

	private void sendDirectMessage(String text, MultiValueMap<String, Object> dmParams) {
		dmParams.add("text", text);
		ResponseEntity<Map> response = restTemplate.postForEntity(SEND_DIRECT_MESSAGE_URL, dmParams, Map.class);
		handleResponseErrors(response);
	}

	private void handleResponseErrors(ResponseEntity<Map> response) {
		SocialException exception = statusCodeTranslator.translate(response);
		if (exception != null) {
			throw exception;
		}
	}

	static final String DIRECT_MESSAGES_RECEIVED_URL = TwitterTemplate.API_URL_BASE + "direct_messages.json";
	static final String DIRECT_MESSAGES_SENT_URL = TwitterTemplate.API_URL_BASE + "direct_messages/sent.json";
	static final String SEND_DIRECT_MESSAGE_URL = TwitterTemplate.API_URL_BASE + "direct_messages/new.json";
	static final String DESTROY_DIRECT_MESSAGE_URL = TwitterTemplate.API_URL_BASE + "direct_messages/destroy/{dm_id}.json";

}
