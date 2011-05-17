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
package org.springframework.social.twitter.api.impl;

import static org.junit.Assert.*;
import static org.springframework.http.HttpMethod.*;
import static org.springframework.social.test.client.RequestMatchers.*;
import static org.springframework.social.test.client.ResponseCreators.*;

import java.util.List;

import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.social.twitter.api.TwitterProfile;


/**
 * @author Craig Walls
 */
public class BlockTemplateTest extends AbstractTwitterApiTest {
	
	@Test
	public void block_userId() {
		mockServer.expect(requestTo("https://api.twitter.com/1/blocks/create.json"))
			.andExpect(method(POST))
			.andExpect(body("user_id=12345"))
			.andRespond(withResponse(new ClassPathResource("twitter-profile.json", getClass()), responseHeaders));
		TwitterProfile blockedUser = twitter.blockOperations().block(12345);
		assertTwitterProfile(blockedUser);
		mockServer.verify();
	}

	@Test
	public void block_screenName() {
		mockServer.expect(requestTo("https://api.twitter.com/1/blocks/create.json"))
			.andExpect(method(POST))
			.andExpect(body("screen_name=habuma"))
			.andRespond(withResponse(new ClassPathResource("twitter-profile.json", getClass()), responseHeaders));
		TwitterProfile blockedUser = twitter.blockOperations().block("habuma");
		assertTwitterProfile(blockedUser);
		mockServer.verify();
	}	
	
	@Test
	public void unblock_userId() {
		mockServer.expect(requestTo("https://api.twitter.com/1/blocks/destroy.json"))
			.andExpect(method(POST))
			.andExpect(body("user_id=12345"))
			.andRespond(withResponse(new ClassPathResource("twitter-profile.json", getClass()), responseHeaders));
		TwitterProfile blockedUser = twitter.blockOperations().unblock(12345);
		assertTwitterProfile(blockedUser);
		mockServer.verify();
	}

	@Test
	public void unblock_screenName() {
		mockServer.expect(requestTo("https://api.twitter.com/1/blocks/destroy.json"))
			.andExpect(method(POST))
			.andExpect(body("screen_name=habuma"))
			.andRespond(withResponse(new ClassPathResource("twitter-profile.json", getClass()), responseHeaders));
		TwitterProfile blockedUser = twitter.blockOperations().unblock("habuma");
		assertTwitterProfile(blockedUser);
		mockServer.verify();
	}	
	
	@Test
	public void getBlockedUsers() {
		mockServer.expect(requestTo("https://api.twitter.com/1/blocks/blocking.json"))
			.andExpect(method(GET))
			.andRespond(withResponse(new ClassPathResource("list-of-profiles.json", getClass()), responseHeaders));
		List<TwitterProfile> blockedUsers = twitter.blockOperations().getBlockedUsers();
		assertEquals(2, blockedUsers.size());
		mockServer.verify();
	}
	
	@Test
	public void getBlockedUserIds() {
		mockServer.expect(requestTo("https://api.twitter.com/1/blocks/blocking/ids.json"))
			.andExpect(method(GET))
			.andRespond(withResponse(new ClassPathResource("list-of-ids.json", getClass()), responseHeaders));
		List<Long> blockedUsers = twitter.blockOperations().getBlockedUserIds();
		assertEquals(4, blockedUsers.size());
		mockServer.verify();
	}
	
	@Test
	public void isBlocking_userId() {
		mockServer.expect(requestTo("https://api.twitter.com/1/blocks/exists.json?user_id=12345"))
			.andExpect(method(GET))
			.andRespond(withResponse(new ClassPathResource("twitter-profile.json", getClass()), responseHeaders));
		assertTrue(twitter.blockOperations().isBlocking(12345));		
	}

	@Test
	public void isBlocking_userId_false() {
		mockServer.expect(requestTo("https://api.twitter.com/1/blocks/exists.json?user_id=12345"))
			.andExpect(method(GET))
			.andRespond(withResponse("{}", responseHeaders, HttpStatus.NOT_FOUND, "Not Found"));
		assertFalse(twitter.blockOperations().isBlocking(12345));		
	}

	@Test
	public void isBlocking_screenName() {
		mockServer.expect(requestTo("https://api.twitter.com/1/blocks/exists.json?screen_name=habuma"))
			.andExpect(method(GET))
			.andRespond(withResponse(new ClassPathResource("twitter-profile.json", getClass()), responseHeaders));
		assertTrue(twitter.blockOperations().isBlocking("habuma"));		
	}

	@Test
	public void isBlocking_screenName_false() {
		mockServer.expect(requestTo("https://api.twitter.com/1/blocks/exists.json?screen_name=habuma"))
			.andExpect(method(GET))
			.andRespond(withResponse("{}", responseHeaders, HttpStatus.NOT_FOUND, "Not Found"));
		assertFalse(twitter.blockOperations().isBlocking("habuma"));		
	}

	// private helpers
	
	private void assertTwitterProfile(TwitterProfile blockedUser) {
		assertEquals(12345, blockedUser.getId());
		assertEquals("habuma", blockedUser.getScreenName());
		assertEquals("Craig Walls", blockedUser.getName());
		assertEquals("Spring Guy", blockedUser.getDescription());
		assertEquals("Plano, TX", blockedUser.getLocation());
		assertEquals("http://www.springsource.org", blockedUser.getUrl());
		assertEquals("http://a3.twimg.com/profile_images/1205746571/me2_300.jpg", blockedUser.getProfileImageUrl());
	}	

}
