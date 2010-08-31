package org.springframework.social.twitter;

import static java.util.Arrays.*;
import static java.util.Collections.*;
import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.social.twitter.TwitterTemplate.*;

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
import org.springframework.social.oauth.OAuthConsumerTokenServices;
import org.springframework.social.oauth.OAuthHelper;
import org.springframework.social.oauth.OAuthSpringSecurityOAuthHelper;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

public class TwitterTemplateTest {
	
	@Test
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void tweet() throws Exception {
		OAuthHelper oauthHelper = mock(OAuthHelper.class);
		when(oauthHelper.buildAuthorizationHeader(any(HttpMethod.class),
						eq(TwitterTemplate.TWEET_URL), any(String.class), any(Map.class))).thenReturn(
				"Auth_Header");

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
		OAuthHelper oauthHelper = mock(OAuthHelper.class);
		when(oauthHelper.buildAuthorizationHeader(any(HttpMethod.class),
						eq(TwitterTemplate.RETWEET_URL), any(String.class), any(Map.class))).thenReturn("Auth_Header");

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

		OAuthConsumerTokenServices tokenServices = mock(OAuthConsumerTokenServices.class);
		when(tokenServices.getToken("Twitter", "1")).thenReturn(accessToken);

		OAuthSpringSecurityOAuthHelper oauthHelper = mock(OAuthSpringSecurityOAuthHelper.class);
		when(oauthHelper.resolveAccessToken("Twitter")).thenReturn(accessToken);

		TwitterTemplate twitter = new TwitterTemplate(oauthHelper);
		twitter.setRestTemplate(restTemplate);
		
		String screenName = twitter.getScreenName();
		assertEquals("springdude", screenName);
	}

	@Test
	@SuppressWarnings("unchecked")
	public void getFollowed() {
		OAuthConsumerToken accessToken = setupAccessToken();

		OAuthConsumerTokenServices tokenServices = mock(OAuthConsumerTokenServices.class);
		when(tokenServices.getToken("Twitter", "1")).thenReturn(accessToken);

		OAuthSpringSecurityOAuthHelper oauthHelper = mock(OAuthSpringSecurityOAuthHelper.class);
		when(oauthHelper.resolveAccessToken("Twitter")).thenReturn(accessToken);

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

	private OAuthConsumerToken setupAccessToken() {
	    OAuthConsumerToken accessToken = new OAuthConsumerToken();
		accessToken.setAccessToken(true);
		accessToken.setResourceId("twitter");
		accessToken.setSecret("twitterSecret");
		accessToken.setValue("twitterToken");
	    return accessToken;
    }	
}
