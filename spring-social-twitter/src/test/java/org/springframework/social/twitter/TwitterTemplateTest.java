/*
 * Copyright 2010 the original author or authors.
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

import org.junit.Before;
import org.junit.Test;
import org.springframework.social.AccountNotConnectedException;
import org.springframework.social.OperationNotPermittedException;

/**
 * @author Craig Walls
 */
public class TwitterTemplateTest {

	private TwitterTemplate twitter;

	@Before
	public void setup() {
		twitter = new TwitterTemplate();
	}

	@Test
	public void getProfileId() {
	}

	@Test
	public void getProfile() {
	}

	@Test
	public void getFollowed() {
	}

	@Test
	public void updateStatus() {
	}

	@Test
	public void updateStatus_withLocation() {
	}

	@Test(expected = DuplicateTweetException.class)
	public void updateStatus_duplicateTweet() {
	}

	@Test(expected = OperationNotPermittedException.class)
	public void updateStatus_forbidden() {
	}

	@Test(expected = AccountNotConnectedException.class)
	public void updateStatus_unauthorized() {
	}

	@Test
	public void retweet() {
	}

	public void retweet_duplicateTweet() {
	}

	public void retweet_forbidden() {
	}

	@Test(expected = AccountNotConnectedException.class)
	public void retweet_unauthorized() {
	}

	@Test
	public void getMentions() {
	}

	@Test
	public void getPublicTimeline() {
	}

	@Test
	public void getHomeTimeline() {
	}

	@Test
	public void getFriendsTimeline() {
	}

	@Test
	public void getUserTimeline() {
	}

	@Test
	public void getUserTimeline_forScreenName() {
	}

	@Test
	public void getUserTimeline_forUserId() {
	}

	@Test
	public void search_queryOnly() {
	}

	@Test
	public void search_pageAndResultsPerPage() {
	}

	@Test
	public void search_sinceAndMaxId() {
	}

}