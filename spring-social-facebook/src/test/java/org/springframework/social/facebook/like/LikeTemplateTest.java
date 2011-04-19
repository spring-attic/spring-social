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
package org.springframework.social.facebook.like;

import static org.junit.Assert.*;
import static org.springframework.http.HttpMethod.*;
import static org.springframework.social.test.client.RequestMatchers.*;
import static org.springframework.social.test.client.ResponseCreators.*;

import java.util.List;

import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.social.facebook.AbstractFacebookApiTest;

public class LikeTemplateTest extends AbstractFacebookApiTest {

	@Test
	public void getLikes() {
		mockServer.expect(requestTo("https://graph.facebook.com/me/likes")).andExpect(method(GET))
				.andExpect(header("Authorization", "OAuth someAccessToken"))
				.andRespond(withResponse(new ClassPathResource("user-likes.json", getClass()), responseHeaders));
		List<UserLike> likes = facebook.likeOperations().getLikes();
		assertLikes(likes);
	}

	@Test
	public void getLikes_forSpecificUser() {
		mockServer.expect(requestTo("https://graph.facebook.com/123456789/likes")).andExpect(method(GET))
				.andExpect(header("Authorization", "OAuth someAccessToken"))
				.andRespond(withResponse(new ClassPathResource("user-likes.json", getClass()), responseHeaders));
		List<UserLike> likes = facebook.likeOperations().getLikes("123456789");
		assertLikes(likes);
	}
	
	@Test
	public void getInterests() {
		mockServer.expect(requestTo("https://graph.facebook.com/me/interests")).andExpect(method(GET))
				.andExpect(header("Authorization", "OAuth someAccessToken"))
				.andRespond(withResponse(new ClassPathResource("interests.json", getClass()), responseHeaders));
		List<UserLike> likes = facebook.likeOperations().getInterests();
		assertInterests(likes);
	}
	
	@Test
	public void getInterests_forSpecificUser() {
		mockServer.expect(requestTo("https://graph.facebook.com/12345678/interests")).andExpect(method(GET))
				.andExpect(header("Authorization", "OAuth someAccessToken"))
				.andRespond(withResponse(new ClassPathResource("interests.json", getClass()), responseHeaders));
		List<UserLike> likes = facebook.likeOperations().getInterests("12345678");
		assertInterests(likes);
	}

	@Test
	public void getActivities() {
		mockServer.expect(requestTo("https://graph.facebook.com/me/activities")).andExpect(method(GET))
				.andExpect(header("Authorization", "OAuth someAccessToken"))
				.andRespond(withResponse(new ClassPathResource("activities.json", getClass()), responseHeaders));
		List<UserLike> likes = facebook.likeOperations().getActivities();
		assertActivities(likes);
	}
	
	@Test
	public void getActivities_forSpecificUser() {
		mockServer.expect(requestTo("https://graph.facebook.com/12345678/activities")).andExpect(method(GET))
				.andExpect(header("Authorization", "OAuth someAccessToken"))
				.andRespond(withResponse(new ClassPathResource("activities.json", getClass()), responseHeaders));
		List<UserLike> likes = facebook.likeOperations().getActivities("12345678");
		assertActivities(likes);
	}
	
	@Test
	public void like() {
		mockServer.expect(requestTo("https://graph.facebook.com/123456/likes"))
			.andExpect(method(POST))
			.andExpect(header("Authorization", "OAuth someAccessToken"))
			.andRespond(withResponse("{}", responseHeaders));
		facebook.likeOperations().like("123456");
		mockServer.verify();
	}
	
	@Test
	public void unlike() {
		mockServer.expect(requestTo("https://graph.facebook.com/123456/likes"))
			.andExpect(method(POST))
			.andExpect(body("method=delete"))
			.andExpect(header("Authorization", "OAuth someAccessToken"))
			.andRespond(withResponse("{}", responseHeaders));
		facebook.likeOperations().unlike("123456");
		mockServer.verify();
	}

	private void assertLikes(List<UserLike> likes) {
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

	private void assertInterests(List<UserLike> interests) {
		assertEquals(2, interests.size());
		UserLike interest1 = interests.get(0);
		assertEquals("115137931834647", interest1.getId());
		assertEquals("Insects", interest1.getName());
		assertEquals("Animal", interest1.getCategory());
		UserLike interest2 = interests.get(1);
		assertEquals("375108804933", interest2.getId());
		assertEquals("Renaissance Festivals", interest2.getName());
		assertEquals("Interest", interest2.getCategory());
	}

	private void assertActivities(List<UserLike> activities) {
		assertEquals(2, activities.size());
		UserLike interest1 = activities.get(0);
		assertEquals("106302979408734", interest1.getId());
		assertEquals("Macrame", interest1.getName());
		assertEquals("Interest", interest1.getCategory());
		UserLike interest2 = activities.get(1);
		assertEquals("109364789081700", interest2.getId());
		assertEquals("Basket weaving", interest2.getName());
		assertEquals("Interest", interest2.getCategory());
	}

}
