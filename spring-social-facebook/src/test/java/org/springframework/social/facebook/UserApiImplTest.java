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

import static org.junit.Assert.*;
import static org.springframework.http.HttpMethod.*;
import static org.springframework.social.test.client.RequestMatchers.*;
import static org.springframework.social.test.client.ResponseCreators.*;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.social.test.client.MockRestServiceServer;

/**
 * @author Craig Walls
 */
public class UserApiImplTest {
	
	private static final String ACCESS_TOKEN = "someAccessToken";
	
	private FacebookTemplate facebook;
	private MockRestServiceServer mockServer;
	private HttpHeaders responseHeaders;

	@Before
	public void setup() {
		facebook = new FacebookTemplate(ACCESS_TOKEN);
		mockServer = MockRestServiceServer.createServer(facebook.getRestTemplate());
		responseHeaders = new HttpHeaders();
		responseHeaders.setContentType(MediaType.APPLICATION_JSON);
	}
	
	@Test
	public void getLikes() {
		mockServer.expect(requestTo("https://graph.facebook.com/me/likes"))
			.andExpect(method(GET))
			.andExpect(header("Authorization", "OAuth someAccessToken"))
			.andRespond(withResponse(new ClassPathResource("user-likes.json", getClass()), responseHeaders));
		List<UserLike> likes = facebook.userApi().getLikes();
		assertEquals(3, likes.size());
		UserLike like1 = likes.get(0);
		assertEquals("113294925350820", like1.getId());
		assertEquals("Pirates of the Caribbean", like1.getName());
		assertEquals("Movie", like1.getCategory());
		UserLike like2 = likes.get(1);
		assertEquals("38073733123", like2.getId());
		assertEquals("Dublin Dr Pepper", like2.getName());
		assertEquals("Company", like2.getCategory());
		UserLike like3 = likes.get(2);
		assertEquals("10264922373", like3.getId());
		assertEquals("Freebirds World Burrito", like3.getName());
		assertEquals("Restaurant/cafe", like3.getCategory());
	}

}
