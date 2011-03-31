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
import org.springframework.social.twitter.types.SavedSearch;
import org.springframework.social.twitter.types.SearchResults;
import org.springframework.social.twitter.types.Trend;
import org.springframework.social.twitter.types.Trends;
import org.springframework.social.twitter.types.Tweet;


/**
 * @author Craig Walls
 */
public class SearchApiImplTest extends AbstractTwitterApiTest {

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
	
	@Test
	public void getSavedSearches() {
		mockServer.expect(requestTo("https://api.twitter.com/1/saved_searches.json"))
			.andExpect(method(GET))
			.andRespond(withResponse(new ClassPathResource("saved-searches.json", getClass()), responseHeaders));
		List<SavedSearch> savedSearches = twitter.searchApi().getSavedSearches();
		assertEquals(2, savedSearches.size());
		SavedSearch search1 = savedSearches.get(0);
		assertEquals(26897775, search1.getId());
		assertEquals("#springsocial", search1.getQuery());
		assertEquals("#springsocial", search1.getName());
		assertEquals(0, search1.getPosition());
		SavedSearch search2 = savedSearches.get(1);
		assertEquals(56897772, search2.getId());
		assertEquals("#twitter", search2.getQuery());
		assertEquals("#twitter", search2.getName());
		assertEquals(1, search2.getPosition());
	}

	@Test
	public void getSavedSearch() {
		mockServer.expect(requestTo("https://api.twitter.com/1/saved_searches/show/26897775.json"))
				.andExpect(method(GET))
				.andRespond(withResponse(new ClassPathResource("saved-search.json", getClass()), responseHeaders));
		SavedSearch savedSearch = twitter.searchApi().getSavedSearch(26897775);
		assertEquals(26897775, savedSearch.getId());
		assertEquals("#springsocial", savedSearch.getQuery());
		assertEquals("#springsocial", savedSearch.getName());
		assertEquals(0, savedSearch.getPosition());
	}
	
	@Test
	public void createSavedSearch() {
		mockServer.expect(requestTo("https://api.twitter.com/1/saved_searches/create.json"))
			.andExpect(method(POST))
			.andExpect(body("query=%23twitter"))
			.andRespond(withResponse("{}", responseHeaders));
		twitter.searchApi().createSavedSearch("#twitter");
		mockServer.verify();
	}

	@Test
	public void deleteSavedSearch() {
		mockServer.expect(requestTo("https://api.twitter.com/1/saved_searches/destroy/26897775.json"))
			.andExpect(method(DELETE))
			.andRespond(withResponse("{}", responseHeaders));
		twitter.searchApi().deleteSavedSearch(26897775);
		mockServer.verify();
	}
	
	@Test
	public void getCurrentTrends() {
		mockServer.expect(requestTo("https://api.twitter.com/1/trends/current.json"))
			.andExpect(method(GET))
			.andRespond(withResponse(new ClassPathResource("current-trends.json", getClass()), responseHeaders));
		Trends currentTrends = twitter.searchApi().getCurrentTrends();
		List<Trend> trends = currentTrends.getTrends();
		assertEquals(2, trends.size());
		assertEquals("Cool Stuff", trends.get(0).getName());
		assertEquals("Cool Stuff", trends.get(0).getQuery());
		assertEquals("#springsocial", trends.get(1).getName());
		assertEquals("#springsocial", trends.get(1).getQuery());
	}
	
	@Test
	public void getCurrentTrends_excludeHashtags() {
		mockServer.expect(requestTo("https://api.twitter.com/1/trends/current.json?exclude=hashtags"))
			.andExpect(method(GET))
			.andRespond(withResponse(new ClassPathResource("current-trends.json", getClass()), responseHeaders));
		Trends currentTrends = twitter.searchApi().getCurrentTrends(true);
		List<Trend> trends = currentTrends.getTrends();
		assertEquals(2, trends.size());
	}
	
	@Test
	public void getDailyTrends() {
		mockServer.expect(requestTo("https://api.twitter.com/1/trends/daily.json"))
			.andExpect(method(GET))
			.andRespond(withResponse(new ClassPathResource("daily-trends.json", getClass()), responseHeaders));

		List<Trends> dailyTrends = twitter.searchApi().getDailyTrends();
		assertEquals(24, dailyTrends.size());
		int i = 0;
		for (Trends currentTrends : dailyTrends) {
			List<Trend> trends = currentTrends.getTrends();
			assertEquals(2, trends.size());
			assertEquals("Cool Stuff" + i, trends.get(0).getName());
			assertEquals("Cool Stuff" + i, trends.get(0).getQuery());
			assertEquals("#springsocial" + i, trends.get(1).getName());
			assertEquals("#springsocial" + i, trends.get(1).getQuery());
			i++;
		}
	}
	
	@Test
	public void getDailyTrends_excludeHashtags() {
		mockServer.expect(requestTo("https://api.twitter.com/1/trends/daily.json?exclude=hashtags"))
			.andExpect(method(GET))
			.andRespond(withResponse(new ClassPathResource("daily-trends.json", getClass()), responseHeaders));

		List<Trends> dailyTrends = twitter.searchApi().getDailyTrends(true);
		assertEquals(24, dailyTrends.size());
		mockServer.verify();
	}

	@Test
	public void getDailyTrends_withStartDate() {
		mockServer.expect(requestTo("https://api.twitter.com/1/trends/daily.json?date=2011-03-17"))
			.andExpect(method(GET))
			.andRespond(withResponse(new ClassPathResource("daily-trends.json", getClass()), responseHeaders));

		List<Trends> dailyTrends = twitter.searchApi().getDailyTrends(false, "2011-03-17");
		assertEquals(24, dailyTrends.size());
		mockServer.verify();
	}

	@Test
	public void getDailyTrends_withStartDateAndExcludeHashtags() {
		mockServer.expect(requestTo("https://api.twitter.com/1/trends/daily.json?exclude=hashtags&date=2011-03-17"))
			.andExpect(method(GET))
			.andRespond(withResponse(new ClassPathResource("daily-trends.json", getClass()), responseHeaders));

		List<Trends> dailyTrends = twitter.searchApi().getDailyTrends(true, "2011-03-17");
		assertEquals(24, dailyTrends.size());
		mockServer.verify();
	}
	
	@Test
	public void getWeeklyTrends() {
		mockServer.expect(requestTo("https://api.twitter.com/1/trends/weekly.json"))
			.andExpect(method(GET))
			.andRespond(withResponse(new ClassPathResource("weekly-trends.json", getClass()), responseHeaders));

		List<Trends> dailyTrends = twitter.searchApi().getWeeklyTrends();
		assertEquals(7, dailyTrends.size());
		int i = 0;
		for (Trends currentTrends : dailyTrends) {
			List<Trend> trends = currentTrends.getTrends();
			assertEquals(2, trends.size());
			assertEquals("Cool Stuff" + i, trends.get(0).getName());
			assertEquals("Cool Stuff" + i, trends.get(0).getQuery());
			assertEquals("#springsocial" + i, trends.get(1).getName());
			assertEquals("#springsocial" + i, trends.get(1).getQuery());
			i++;
		}
	}
	
	@Test
	public void getWeeklyTrends_excludeHashtags() {
		mockServer.expect(requestTo("https://api.twitter.com/1/trends/weekly.json?exclude=hashtags"))
			.andExpect(method(GET))
			.andRespond(withResponse(new ClassPathResource("weekly-trends.json", getClass()), responseHeaders));

		List<Trends> dailyTrends = twitter.searchApi().getWeeklyTrends(true);
		assertEquals(7, dailyTrends.size());
		mockServer.verify();
	}
	
	@Test
	public void getWeeklyTrends_withStartDate() {
		mockServer.expect(requestTo("https://api.twitter.com/1/trends/weekly.json?date=2011-03-18"))
			.andExpect(method(GET))
			.andRespond(withResponse(new ClassPathResource("weekly-trends.json", getClass()), responseHeaders));

		List<Trends> dailyTrends = twitter.searchApi().getWeeklyTrends(false, "2011-03-18");
		assertEquals(7, dailyTrends.size());
		mockServer.verify();
	}
	
	@Test
	public void getWeeklyTrends_withStartDateAndExcludeHashtags() {
		mockServer.expect(requestTo("https://api.twitter.com/1/trends/weekly.json?exclude=hashtags&date=2011-03-18"))
			.andExpect(method(GET))
			.andRespond(withResponse(new ClassPathResource("weekly-trends.json", getClass()), responseHeaders));

		List<Trends> dailyTrends = twitter.searchApi().getWeeklyTrends(true, "2011-03-18");
		assertEquals(7, dailyTrends.size());
		mockServer.verify();
	}
	
	@Test
	public void getLocalTrends() {
		mockServer.expect(requestTo("https://api.twitter.com/1/trends/2442047.json"))
			.andExpect(method(GET))
			.andRespond(withResponse(new ClassPathResource("local-trends.json", getClass()), responseHeaders));
		Trends localTrends = twitter.searchApi().getLocalTrends(2442047);
		List<Trend> trends = localTrends.getTrends();
		assertEquals(2, trends.size());
		Trend trend1 = trends.get(0);
		assertEquals("Cool Stuff", trend1.getName());
		assertEquals("Cool+Stuff", trend1.getQuery());
		Trend trend2 = trends.get(1);
		assertEquals("#springsocial", trend2.getName());
		assertEquals("%23springsocial", trend2.getQuery());
	}
	
	@Test
	public void getLocalTrends_excludeHashtags() {
		mockServer.expect(requestTo("https://api.twitter.com/1/trends/2442047.json?exclude=hashtags"))
			.andExpect(method(GET))
			.andRespond(withResponse(new ClassPathResource("local-trends.json", getClass()), responseHeaders));
		Trends localTrends = twitter.searchApi().getLocalTrends(2442047, true);
		List<Trend> trends = localTrends.getTrends();
		assertEquals(2, trends.size());
	}	

	// test helpers

	private void assertSearchTweets(List<Tweet> tweets) {
		assertTimelineTweets(tweets);
		assertEquals("en", tweets.get(0).getLanguageCode());
		assertEquals("de", tweets.get(1).getLanguageCode());
	}
}
