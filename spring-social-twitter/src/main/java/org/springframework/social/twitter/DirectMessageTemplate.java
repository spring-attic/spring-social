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

import org.springframework.social.twitter.support.json.DirectMessageList;
import org.springframework.social.twitter.types.DirectMessage;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

/**
 * Implementation of {@link DirectMessageOperations}, providing a binding to Twitter's direct message-oriented REST resources.
 * @author Craig Walls
 */
class DirectMessageTemplate extends AbstractTwitterOperations implements DirectMessageOperations {

	
	public DirectMessageTemplate(LowLevelTwitterApi lowLevelApi) {
		super(lowLevelApi);
	}

	public List<DirectMessage> getDirectMessagesReceived() {
		requireUserAuthorization();
		return getLowLevelTwitterApi().fetchObject("direct_messages.json", DirectMessageList.class).getList();
	}

	public List<DirectMessage> getDirectMessagesSent() {
		requireUserAuthorization();
		return getLowLevelTwitterApi().fetchObject("direct_messages/sent.json", DirectMessageList.class).getList();
	}

	public void sendDirectMessage(String toScreenName, String text) {
		requireUserAuthorization();
		MultiValueMap<String, Object> data = new LinkedMultiValueMap<String, Object>();
		data.add("screen_name", String.valueOf(toScreenName));
		data.add("text", text);
	    getLowLevelTwitterApi().publish("direct_messages/new.json", data);
	}

	public void sendDirectMessage(long toUserId, String text) {
		requireUserAuthorization();
		MultiValueMap<String, Object> data = new LinkedMultiValueMap<String, Object>();
		data.add("user_id", String.valueOf(toUserId));
		data.add("text", text);
	    getLowLevelTwitterApi().publish("direct_messages/new.json", data);
	}

	public void deleteDirectMessage(long messageId) {
		requireUserAuthorization();
		getLowLevelTwitterApi().delete("direct_messages/destroy/" + messageId + ".json");
	}

}
