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

import static org.junit.Assert.*;
import static org.springframework.http.HttpMethod.*;
import static org.springframework.social.test.client.RequestMatchers.*;
import static org.springframework.social.test.client.ResponseCreators.*;

import java.util.List;

import org.junit.Test;
import org.springframework.core.io.ClassPathResource;


/**
 * @author Craig Walls
 */
public class DirectMessageApiImplTest extends AbstractTwitterApiTest {

	@Test
	public void getDirectMessagesReceived() {
		mockServer.expect(requestTo("https://api.twitter.com/1/direct_messages.json"))
				.andExpect(method(GET))
				.andRespond(withResponse(new ClassPathResource("messages.json", getClass()), responseHeaders));

		List<DirectMessage> messages = twitter.directMessageApi().getDirectMessagesReceived();
		assertDirectMessageListContents(messages);
	}

	@Test
	public void getDirectMessagesSent() {
		mockServer.expect(requestTo("https://api.twitter.com/1/direct_messages/sent.json"))
				.andExpect(method(GET))
				.andRespond(withResponse(new ClassPathResource("messages.json", getClass()), responseHeaders));

		List<DirectMessage> messages = twitter.directMessageApi().getDirectMessagesSent();
		assertDirectMessageListContents(messages);
	}

	@Test
	public void sendDirectMessage_toScreenName() {
		mockServer.expect(requestTo("https://api.twitter.com/1/direct_messages/new.json")).andExpect(method(POST))
				.andExpect(body("screen_name=habuma&text=Hello+there%21"))
				.andRespond(withResponse("{}", responseHeaders));
		twitter.directMessageApi().sendDirectMessage("habuma", "Hello there!");
		mockServer.verify();
	}

	@Test
	public void sendDirectMessage_toUserId() {
		mockServer.expect(requestTo("https://api.twitter.com/1/direct_messages/new.json")).andExpect(method(POST))
				.andExpect(body("user_id=11223&text=Hello+there%21")).andRespond(withResponse("{}", responseHeaders));
		twitter.directMessageApi().sendDirectMessage(11223, "Hello there!");
		mockServer.verify();
	}
	
	@Test
	public void deleteDirectMessage() {
		mockServer.expect(requestTo("https://api.twitter.com/1/direct_messages/destroy/42.json"))
				.andExpect(method(DELETE))
				.andRespond(withResponse(new ClassPathResource("directMessage.json", getClass()), responseHeaders));
		twitter.directMessageApi().deleteDirectMessage(42L);
		mockServer.verify();
	}

	private void assertDirectMessageListContents(List<DirectMessage> messages) {
		assertEquals(2, messages.size());
		assertEquals(12345, messages.get(0).getId());
		assertEquals("Hello there", messages.get(0).getText());
		assertEquals(24680, messages.get(0).getSenderId());
		assertEquals("rclarkson", messages.get(0).getSenderScreenName());
		assertEquals(13579, messages.get(0).getRecipientId());
		assertEquals("kdonald", messages.get(0).getRecipientScreenName());
		// assertTimelineDateEquals("Tue Jul 13 17:38:21 +0000 2010", messages.get(0).getCreatedAt());
		assertEquals(23456, messages.get(1).getId());
		assertEquals("Back at ya", messages.get(1).getText());
		assertEquals(13579, messages.get(1).getSenderId());
		assertEquals("kdonald", messages.get(1).getSenderScreenName());
		assertEquals(24680, messages.get(1).getRecipientId());
		assertEquals("rclarkson", messages.get(1).getRecipientScreenName());
	}

}
