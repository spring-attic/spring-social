package org.springframework.social.twitter;

import static java.util.Arrays.*;
import static java.util.Collections.*;
import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.social.twitter.TwitterTemplate.*;

import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth.common.signature.SharedConsumerSecret;
import org.springframework.security.oauth.consumer.BaseProtectedResourceDetails;
import org.springframework.security.oauth.consumer.InMemoryProtectedResourceDetailsService;
import org.springframework.security.oauth.consumer.OAuthConsumerSupport;
import org.springframework.security.oauth.consumer.ProtectedResourceDetails;
import org.springframework.security.oauth.consumer.token.OAuthConsumerToken;
import org.springframework.social.oauth.AccessTokenProvider;
import org.springframework.social.oauth.OAuthHelper;
import org.springframework.social.oauth.OAuthSpringSecurityOAuthHelper;
import org.springframework.social.oauth.SimpleAccessTokenProvider;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

public class TwitterTemplateTest {
	
	@Test
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void updateStatus() throws Exception {
		OAuthConsumerToken accessToken = setupAccessToken();
		OAuthHelper oauthHelper = mock(OAuthHelper.class);
		when(oauthHelper.buildAuthorizationHeader(any(AccessTokenProvider.class), any(HttpMethod.class),
						eq(TwitterTemplate.UPDATE_STATUS_URL), any(String.class), any(Map.class))).thenReturn(
				"Auth_Header");

		TwitterTemplate twitter = new TwitterTemplate(oauthHelper);
		RestTemplate restTemplate = mock(RestTemplate.class);
		ResponseEntity<Map> responseEntity = new ResponseEntity<Map>(Collections.emptyMap(), HttpStatus.OK);
		when(restTemplate.exchange(eq(UPDATE_STATUS_URL), eq(HttpMethod.POST), any(HttpEntity.class), any(Class.class), 
				any(Map.class))).thenReturn(responseEntity);
		twitter.setRestTemplate(restTemplate);
		twitter.updateStatus("This is a test", new SimpleAccessTokenProvider<OAuthConsumerToken>(accessToken));
		MultiValueMap<String, String> form = new LinkedMultiValueMap<String, String>();
		form.add("status", "This is a test");
		verify(restTemplate).exchange(eq(TwitterTemplate.UPDATE_STATUS_URL), eq(HttpMethod.POST), any(HttpEntity.class),
				eq(Map.class), any(Map.class)); 
	}
	
	@Test
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void getScreenName() {
		OAuthConsumerToken accessToken = setupAccessToken();
		OAuthConsumerSupport oauthSupport = mockOAuthConsumerSupport(accessToken);		
		InMemoryProtectedResourceDetailsService resourceDetailsService = setupResourceDetailsService();
		Map<String, String> responseMap = new HashMap<String, String>();
		responseMap.put("screen_name", "s2greenhouse");
		ResponseEntity<Map> responseEntity = new ResponseEntity<Map>(responseMap , HttpStatus.OK);
		RestTemplate restTemplate = mock(RestTemplate.class);
		when(restTemplate.exchange(eq(VERIFY_CREDENTIALS_URL), eq(HttpMethod.GET), any(HttpEntity.class), 
				any(Class.class), any(Map.class))).thenReturn(responseEntity);
		TwitterTemplate twitter = new TwitterTemplate(new OAuthSpringSecurityOAuthHelper(oauthSupport,
				resourceDetailsService));
		twitter.setRestTemplate(restTemplate);
		
		String screenName = twitter.getScreenName(new SimpleAccessTokenProvider<OAuthConsumerToken>(accessToken));
		assertEquals("s2greenhouse", screenName);
	}

	@Test
	@SuppressWarnings("unchecked")
	public void getFriends() {
		OAuthConsumerToken accessToken = setupAccessToken();
		OAuthConsumerSupport oauthSupport = mockOAuthConsumerSupport(accessToken);
		InMemoryProtectedResourceDetailsService resourceDetailsService = setupResourceDetailsService();
		TwitterTemplate twitter = new TwitterTemplate(new OAuthSpringSecurityOAuthHelper(oauthSupport,
				resourceDetailsService));
		RestTemplate restTemplate = mock(RestTemplate.class);
		List<Map<String, String>> friends = asList(
				singletonMap("screen_name", "kdonald"),
				singletonMap("screen_name", "rclarkson"));
		ResponseEntity<List<Map<String, String>>> friendsResponse = new ResponseEntity<List<Map<String, String>>>(
				friends, HttpStatus.OK);
		when(restTemplate.exchange(eq(FRIENDS_STATUSES_URL), eq(HttpMethod.GET), any(HttpEntity.class), 
				any(Class.class), any(Map.class))).thenReturn(friendsResponse);
		twitter.setRestTemplate(restTemplate);
		assertEquals(asList("kdonald", "rclarkson"), twitter.getFriends("habuma"));

	}

	private InMemoryProtectedResourceDetailsService setupResourceDetailsService() {
	    InMemoryProtectedResourceDetailsService resourceDetailsService = new InMemoryProtectedResourceDetailsService();
		Map<String, ProtectedResourceDetails> detailsStore = new HashMap<String, ProtectedResourceDetails>();
		BaseProtectedResourceDetails twitterDetails = new BaseProtectedResourceDetails();
		twitterDetails.setConsumerKey("twitterKey");
		twitterDetails.setSharedSecret(new SharedConsumerSecret("twitterSecret"));		
		detailsStore.put("twitter", twitterDetails);
		resourceDetailsService.setResourceDetailsStore(detailsStore);
	    return resourceDetailsService;
    }

	@SuppressWarnings("unchecked")
	private OAuthConsumerSupport mockOAuthConsumerSupport(OAuthConsumerToken accessToken) {
	    OAuthConsumerSupport oauthSupport = mock(OAuthConsumerSupport.class);
		when(oauthSupport.getAuthorizationHeader(any(ProtectedResourceDetails.class), eq(accessToken), any(URL.class), 
				eq("POST"), any(Map.class))).thenReturn("OAuth_Header");
	    return oauthSupport;
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
