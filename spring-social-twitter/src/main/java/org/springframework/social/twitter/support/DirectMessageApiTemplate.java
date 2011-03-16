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

public class DirectMessageApiTemplate implements DirectMessageApi {

	private final RestTemplate restTemplate;
	private final ResponseStatusCodeTranslator statusCodeTranslator;

	public DirectMessageApiTemplate(RestTemplate restTemplate, ResponseStatusCodeTranslator statusCodeTranslator) {
		this.restTemplate = restTemplate;
		this.statusCodeTranslator = statusCodeTranslator;
	}

	public List<DirectMessage> getDirectMessagesReceived() {
		ResponseEntity<List> response = restTemplate.getForEntity(DIRECT_MESSAGES_URL, List.class);
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

	static final String DIRECT_MESSAGES_URL = TwitterTemplate.API_URL_BASE + "direct_messages.json";
	static final String SEND_DIRECT_MESSAGE_URL = TwitterTemplate.API_URL_BASE + "direct_messages/new.json";

}
