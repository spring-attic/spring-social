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
public class SearchApiTemplateTest extends AbstractTwitterApiTest {

	@Test
	public void search_queryOnly() {
		mockServer.expect(requestTo("https://search.twitter.com/search.json?q=%23spring&rpp=50&page=1"))
				.andExpect(method(GET))
				.andRespond(withResponse(new ClassPathResource("search.json", getClass()), responseHeaders));
		SearchResults searchResults = twitter.searchApi().search("#spring");
		assertEquals(10, searchResults.getSinceId());
		assertEquals(999, searchResults.getMaxId());
		List<Tweet> tweets = searchResults.getTweets();
		assertSearchTweets(tweets);
	}

	@Test
	public void search_pageAndResultsPerPage() {
		mockServer.expect(requestTo("https://search.twitter.com/search.json?q=%23spring&rpp=10&page=2"))
				.andExpect(method(GET))
				.andRespond(withResponse(new ClassPathResource("search.json", getClass()), responseHeaders));
		SearchResults searchResults = twitter.searchApi().search("#spring", 2, 10);
		assertEquals(10, searchResults.getSinceId());
		assertEquals(999, searchResults.getMaxId());
		List<Tweet> tweets = searchResults.getTweets();
		assertSearchTweets(tweets);
	}

	@Test
	public void search_sinceAndMaxId() {
		mockServer.expect(requestTo("https://search.twitter.com/search.json?q=%23spring&rpp=10&page=2&since_id=123&max_id=54321"))
				.andExpect(method(GET))
				.andRespond(withResponse(new ClassPathResource("search.json", getClass()), responseHeaders));
		SearchResults searchResults = twitter.searchApi().search("#spring", 2, 10, 123, 54321);
		assertEquals(10, searchResults.getSinceId());
		assertEquals(999, searchResults.getMaxId());
		List<Tweet> tweets = searchResults.getTweets();
		assertSearchTweets(tweets);
	}

	// test helpers

	private void assertSearchTweets(List<Tweet> tweets) {
		assertTimelineTweets(tweets);
		assertEquals("en", tweets.get(0).getLanguageCode());
		assertEquals("de", tweets.get(1).getLanguageCode());
	}
}
