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
