package org.springframework.social.twitter;

import static java.util.Arrays.*;
import static java.util.Collections.*;
import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.social.twitter.TwitterTemplate.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth.consumer.token.OAuthConsumerToken;
import org.springframework.social.oauth.AccessTokenServices;
import org.springframework.social.oauth.OAuthSpringSecurityOAuthTemplate;
import org.springframework.social.oauth.OAuthTemplate;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

public class TwitterTemplateTest {
	
	@Test
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void tweet() throws Exception {
		OAuthTemplate oauthHelper = mock(OAuthTemplate.class);
		when(oauthHelper.buildAuthorizationHeader(any(HttpMethod.class), eq(TwitterTemplate.TWEET_URL), any(Map.class)))
				.thenReturn("Auth_Header");

		TwitterTemplate twitter = new TwitterTemplate(oauthHelper);
		RestTemplate restTemplate = mock(RestTemplate.class);
		ResponseEntity<Map> responseEntity = new ResponseEntity<Map>(Collections.emptyMap(), HttpStatus.OK);
		when(restTemplate.exchange(eq(TWEET_URL), eq(HttpMethod.POST), any(HttpEntity.class), any(Class.class), 
				any(Map.class))).thenReturn(responseEntity);
		twitter.setRestTemplate(restTemplate);
		twitter.tweet("This is a test");
		MultiValueMap<String, String> form = new LinkedMultiValueMap<String, String>();
		form.add("status", "This is a test");
		verify(restTemplate).exchange(eq(TwitterTemplate.TWEET_URL), eq(HttpMethod.POST), any(HttpEntity.class),
				eq(Map.class), any(Map.class)); 
	}
	
	@Test
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void retweet() throws Exception {
		OAuthTemplate oauthHelper = mock(OAuthTemplate.class);
		when(oauthHelper.buildAuthorizationHeader(any(HttpMethod.class), eq(TwitterTemplate.RETWEET_URL), any(Map.class)))
				.thenReturn("Auth_Header");

		TwitterTemplate twitter = new TwitterTemplate(oauthHelper);
		RestTemplate restTemplate = mock(RestTemplate.class);
		ResponseEntity<Map> responseEntity = new ResponseEntity<Map>(Collections.emptyMap(), HttpStatus.OK);
		when(restTemplate.exchange(eq(RETWEET_URL), eq(HttpMethod.POST), any(HttpEntity.class), any(Class.class),
						any(Map.class))).thenReturn(responseEntity);
		twitter.setRestTemplate(restTemplate);

		twitter.retweet(41);

		MultiValueMap<String, String> form = new LinkedMultiValueMap<String, String>();
		form.add("tweet_id", "42");
		verify(restTemplate).exchange(eq(TwitterTemplate.RETWEET_URL), eq(HttpMethod.POST), any(HttpEntity.class),
				eq(Map.class), any(Map.class));
	}

	@Test
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void getScreenName() {
		OAuthConsumerToken accessToken = setupAccessToken();
		Map<String, String> responseMap = new HashMap<String, String>();
		responseMap.put("screen_name", "springdude");
		ResponseEntity<Map> responseEntity = new ResponseEntity<Map>(responseMap , HttpStatus.OK);
		RestTemplate restTemplate = mock(RestTemplate.class);
		when(restTemplate.exchange(eq(VERIFY_CREDENTIALS_URL), eq(HttpMethod.GET), any(HttpEntity.class), 
				any(Class.class), any(Map.class))).thenReturn(responseEntity);

		AccessTokenServices tokenServices = mock(AccessTokenServices.class);
		when(tokenServices.getToken("Twitter", "1")).thenReturn(accessToken);

		OAuthSpringSecurityOAuthTemplate oauthHelper = mock(OAuthSpringSecurityOAuthTemplate.class);
		// when(oauthHelper.resolveAccessToken("Twitter")).thenReturn(accessToken);

		TwitterTemplate twitter = new TwitterTemplate(oauthHelper);
		twitter.setRestTemplate(restTemplate);
		
		String screenName = twitter.getScreenName();
		assertEquals("springdude", screenName);
	}

	@Test
	@SuppressWarnings("unchecked")
	public void getFollowed() {
		OAuthConsumerToken accessToken = setupAccessToken();

		AccessTokenServices tokenServices = mock(AccessTokenServices.class);
		when(tokenServices.getToken("Twitter", "1")).thenReturn(accessToken);

		OAuthSpringSecurityOAuthTemplate oauthHelper = mock(OAuthSpringSecurityOAuthTemplate.class);
		// when(oauthHelper.resolveAccessToken("Twitter")).thenReturn(accessToken);

		TwitterTemplate twitter = new TwitterTemplate(oauthHelper);
		RestTemplate restTemplate = mock(RestTemplate.class);
		List<Map<String, String>> friends = asList(
				singletonMap("screen_name", "kdonald"),
				singletonMap("screen_name", "rclarkson"));
		ResponseEntity<List<Map<String, String>>> friendsResponse = new ResponseEntity<List<Map<String, String>>>(
				friends, HttpStatus.OK);
		when(restTemplate.exchange(eq(FRIENDS_STATUSES_URL), eq(HttpMethod.GET), any(HttpEntity.class), 
				any(Class.class), any(Map.class))).thenReturn(friendsResponse);
		twitter.setRestTemplate(restTemplate);
		assertEquals(asList("kdonald", "rclarkson"), twitter.getFollowed("habuma"));

	}

	@Test
	public void buildSearchResults() {
		TwitterTemplate twitter = new TwitterTemplate(null);
		Map<String, Object> response = new HashMap<String, Object>();
		response.put("max_id", 42);
		response.put("since_id", 24);
		response.put("next_page", "NextPage");
		SearchResults results = twitter.buildSearchResults(response, new ArrayList<Tweet>());
		assertEquals(42, results.getMaxId());
		assertEquals(24, results.getSinceId());
		assertEquals(false, results.isLastPage());
	}

	@Test
	public void buildSearchResults_nullNumbers() {
		TwitterTemplate twitter = new TwitterTemplate(null);
		Map<String, Object> response = new HashMap<String, Object>();
		response.put("next_page", "NextPage");
		SearchResults results = twitter.buildSearchResults(response, new ArrayList<Tweet>());
		assertEquals(0, results.getMaxId());
		assertEquals(0, results.getSinceId());
		assertEquals(false, results.isLastPage());
	}
	private OAuthConsumerToken setupAccessToken() {
	    OAuthConsumerToken accessToken = new OAuthConsumerToken();
		accessToken.setAccessToken(true);
		accessToken.setResourceId("twitter");
		accessToken.setSecret("twitterSecret");
		accessToken.setValue("twitterToken");
	    return accessToken;
    }	
}
