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
package org.springframework.social.facebook;

import org.junit.Before;
import org.junit.Test;

/**
 * @author Craig Walls
 */
public class FacebookTemplateTest {
	
	private static final String ACCESS_TOKEN = "someAccessToken";
	
	private FacebookTemplate facebook;

	@Before
	public void setup() {
		facebook = new FacebookTemplate(ACCESS_TOKEN);
	}

	// TODO complete with testing of json response reading/request writing behavior
	// the use of a mock object before was not testing this important interaction
	
	@Test
	public void getFriendIds() {
	}

	@Test
	public void getUserProfile() {
	}

	@Test
	public void getProfileId() {
	}

	@Test
	public void getProfileUrl() {
	}

	@Test
	public void getProfilePicture() {
	}

	@Test
	public void getProfilePicture_anotherUser() {
	}

	@Test
	public void updateStatus() {
	}

	@Test
	public void updateStatus_withLink() {
	}
	
}
