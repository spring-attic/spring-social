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

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.social.ResponseStatusCodeTranslator;
import org.springframework.social.SocialException;
import org.springframework.social.twitter.DirectMessage;
import org.springframework.social.twitter.DirectMessageApi;
import org.springframework.social.twitter.TwitterTemplate;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.ObjectUtils;
import org.springframework.web.client.RestTemplate;

/**
 * Implementation of {@link DirectMessageApi}, providing a binding to Twitter's direct message-oriented REST resources.
 * @author Craig Walls
 */
public class DirectMessageApiImpl implements DirectMessageApi {

	private final RestTemplate restTemplate;
	private final ResponseStatusCodeTranslator statusCodeTranslator;

	public DirectMessageApiImpl(RestTemplate restTemplate, ResponseStatusCodeTranslator statusCodeTranslator) {
		this.restTemplate = restTemplate;
		this.statusCodeTranslator = statusCodeTranslator;
	}

	public List<DirectMessage> getDirectMessagesReceived() {
		ResponseEntity<List> response = restTemplate.getForEntity(DIRECT_MESSAGES_RECEIVED_URL, List.class);
		return extractDirectMessageListFromResponseEntity(response);
	}

	public List<DirectMessage> getDirectMessagesSent() {
		ResponseEntity<List> response = restTemplate.getForEntity(DIRECT_MESSAGES_SENT_URL, List.class);
		return extractDirectMessageListFromResponseEntity(response);
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

	private List<DirectMessage> extractDirectMessageListFromResponseEntity(ResponseEntity<List> response) {
		List<Map<String, Object>> results = (List<Map<String, Object>>) response.getBody();
		List<DirectMessage> messages = new ArrayList<DirectMessage>();
		for (Map<String, Object> item : results) {
			DirectMessage message = new DirectMessage();
			message.setId(Long.valueOf(String.valueOf(item.get("id"))));
			message.setText(String.valueOf(item.get("text")));
			message.setSenderId(Long.valueOf(String.valueOf(item.get("sender_id"))));
			message.setSenderScreenName(String.valueOf(item.get("sender_screen_name")));
			message.setRecipientId(Long.valueOf(String.valueOf(item.get("recipient_id"))));
			message.setRecipientScreenName(String.valueOf(item.get("recipient_screen_name")));
			message.setCreatedAt(toDate(ObjectUtils.nullSafeToString(item.get("created_at")), timelineDateFormat));
			messages.add(message);
		}
		return messages;
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
	private DateFormat timelineDateFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss ZZZZZ yyyy", Locale.ENGLISH);

	private Date toDate(String dateString, DateFormat dateFormat) {
		try {
			return dateFormat.parse(dateString);
		} catch (ParseException e) {
			return null;
		}
	}

	static final String DIRECT_MESSAGES_RECEIVED_URL = TwitterTemplate.API_URL_BASE + "direct_messages.json";
	static final String DIRECT_MESSAGES_SENT_URL = TwitterTemplate.API_URL_BASE + "direct_messages/sent.json";
	static final String SEND_DIRECT_MESSAGE_URL = TwitterTemplate.API_URL_BASE + "direct_messages/new.json";
	static final String DESTROY_DIRECT_MESSAGE_URL = TwitterTemplate.API_URL_BASE + "direct_messages/destroy/{dm_id}.json";

}
