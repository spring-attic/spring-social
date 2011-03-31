package org.springframework.social.twitter.support.extractors;

import java.util.Map;

import org.springframework.social.twitter.types.DirectMessage;
import org.springframework.util.ObjectUtils;

public class DirectMessageResponseExtractor extends AbstractResponseExtractor<DirectMessage> {

	public DirectMessage extractObject(Map<String, Object> dmMap) {
		DirectMessage message = new DirectMessage();
		message.setId(Long.valueOf(String.valueOf(dmMap.get("id"))));
		message.setText(String.valueOf(dmMap.get("text")));
		message.setSenderId(Long.valueOf(String.valueOf(dmMap.get("sender_id"))));
		message.setSenderScreenName(String.valueOf(dmMap.get("sender_screen_name")));
		message.setRecipientId(Long.valueOf(String.valueOf(dmMap.get("recipient_id"))));
		message.setRecipientScreenName(String.valueOf(dmMap.get("recipient_screen_name")));
		message.setCreatedAt(toTimelineDate(ObjectUtils.nullSafeToString(dmMap.get("created_at"))));
		return message;
	}
	
}
