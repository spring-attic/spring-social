/*
 * Copyright 2015 the original author or authors.
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
package org.springframework.social.oauth1;

import static org.hamcrest.core.StringContains.*;
import static org.junit.Assert.*;
import static org.springframework.http.HttpMethod.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;

import org.junit.Before;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.client.RequestMatcher;
import org.springframework.util.MultiValueMap;

public class OAuth1TemplateTest {
	
	private static final String ACCESS_TOKEN_URL = "http://www.someprovider.com/oauth/accessToken";
	
	private static final String AUTHENTICATE_URL = "https://www.someprovider.com/oauth/authenticate";

	private static final String AUTHORIZE_URL = "https://www.someprovider.com/oauth/authorize";

	private static final String REQUEST_TOKEN_URL = "https://www.someprovider.com/oauth/requestToken";
	
	private OAuth1Template oauth10a;
	
	private OAuth1Template oauth10;
	
	private OAuth1Template customOauth10;

	@Before
	public void setup() {
		oauth10a = new OAuth1Template("consumer_key", "consumer_secret", REQUEST_TOKEN_URL, AUTHORIZE_URL, null, ACCESS_TOKEN_URL, OAuth1Version.CORE_10_REVISION_A);
		oauth10 = new OAuth1Template("consumer_key", "consumer_secret", REQUEST_TOKEN_URL, AUTHORIZE_URL, AUTHENTICATE_URL, ACCESS_TOKEN_URL, OAuth1Version.CORE_10);

		customOauth10 = new OAuth1Template("consumer_key", "consumer_secret", REQUEST_TOKEN_URL, AUTHORIZE_URL, null, ACCESS_TOKEN_URL, OAuth1Version.CORE_10) {
			protected void addCustomAuthorizationParameters(MultiValueMap<String,String> parameters) {
				parameters.set("custom_parameter", "custom_parameter_value");
			};
		};
	}

	@Test
	public void buildAuthorizeUrl() {
		OAuth1Parameters parameters = new OAuth1Parameters(null);
		parameters.setCallbackUrl("http://www.someclient.com/oauth/callback");
		assertEquals(AUTHORIZE_URL + "?oauth_token=request_token",
				oauth10a.buildAuthorizeUrl("request_token", OAuth1Parameters.NONE));
		assertEquals(AUTHORIZE_URL + "?oauth_token=request_token&oauth_callback=http%3A%2F%2Fwww.someclient.com%2Foauth%2Fcallback",
				oauth10.buildAuthorizeUrl("request_token", parameters));
	}
	
	@Test
	public void buildAuthorizeUrl_customAuthorizeParameters() {
		OAuth1Parameters parameters = new OAuth1Parameters(null);
		parameters.setCallbackUrl("http://www.someclient.com/oauth/callback");
		assertEquals(AUTHORIZE_URL + "?oauth_token=request_token&oauth_callback=http%3A%2F%2Fwww.someclient.com%2Foauth%2Fcallback&custom_parameter=custom_parameter_value",
				customOauth10.buildAuthorizeUrl("request_token", parameters));
	}

	@Test
	public void fetchNewRequestToken_OAuth10a() {
		MockRestServiceServer mockServer = MockRestServiceServer.createServer(oauth10a.getRestTemplate());
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		
		mockServer
				.expect(requestTo(REQUEST_TOKEN_URL))
				.andExpect(method(POST))
				.andExpect(headerContains("Authorization", "oauth_callback=\"http%3A%2F%2Fwww.someclient.com%2Foauth%2Fcallback\""))
				.andExpect(headerContains("Authorization", "oauth_version=\"1.0\""))
				.andExpect(headerContains("Authorization", "oauth_signature_method=\"HMAC-SHA1\""))
				.andExpect(headerContains("Authorization", "oauth_consumer_key=\"consumer_key\""))
				.andExpect(headerContains("Authorization", "oauth_nonce=\""))
				.andExpect(headerContains("Authorization", "oauth_signature=\""))
				.andExpect(headerContains("Authorization", "oauth_timestamp=\""))
				.andRespond(withSuccess(new ClassPathResource("requestToken.formencoded", getClass()), MediaType.APPLICATION_FORM_URLENCODED));

		OAuthToken requestToken = oauth10a.fetchRequestToken("http://www.someclient.com/oauth/callback", null);
		assertEquals("1234567890", requestToken.getValue());
		assertEquals("abcdefghijklmnop", requestToken.getSecret());
	}

	@Test
	public void fetchNewRequestToken_OAuth10() {
		MockRestServiceServer mockServer = MockRestServiceServer.createServer(oauth10.getRestTemplate());
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		mockServer.expect(requestTo(REQUEST_TOKEN_URL))
				.andExpect(method(POST))
				.andExpect(headerContains("Authorization", "oauth_version=\"1.0\""))
				.andExpect(headerContains("Authorization", "oauth_signature_method=\"HMAC-SHA1\""))
				.andExpect(headerContains("Authorization", "oauth_consumer_key=\"consumer_key\""))
				.andExpect(headerContains("Authorization", "oauth_nonce=\""))
				.andExpect(headerContains("Authorization", "oauth_signature=\""))
				.andExpect(headerContains("Authorization", "oauth_timestamp=\""))
				.andRespond(withSuccess(new ClassPathResource("requestToken.formencoded", getClass()), MediaType.APPLICATION_FORM_URLENCODED));

		OAuthToken requestToken = oauth10.fetchRequestToken("http://www.someclient.com/oauth/callback", null);
		assertEquals("1234567890", requestToken.getValue());
		assertEquals("abcdefghijklmnop", requestToken.getSecret());
	}

	@Test
	public void exchangeForAccessToken_OAuth10a() {
		MockRestServiceServer mockServer = MockRestServiceServer.createServer(oauth10a.getRestTemplate());
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		mockServer
				.expect(requestTo(ACCESS_TOKEN_URL))
				.andExpect(method(POST))
				.andExpect(headerContains("Authorization", "oauth_version=\"1.0\""))
				.andExpect(headerContains("Authorization", "oauth_signature_method=\"HMAC-SHA1\""))
				.andExpect(headerContains("Authorization", "oauth_consumer_key=\"consumer_key\""))
				.andExpect(headerContains("Authorization", "oauth_token=\"1234567890\""))
				.andExpect(headerContains("Authorization", "oauth_verifier=\"verifier\""))
				.andExpect(headerContains("Authorization", "oauth_nonce=\""))
				.andExpect(headerContains("Authorization", "oauth_signature=\""))
				.andExpect(headerContains("Authorization", "oauth_timestamp=\""))
				.andRespond(withSuccess(new ClassPathResource("accessToken.formencoded", getClass()), MediaType.APPLICATION_FORM_URLENCODED));

		OAuthToken requestToken = new OAuthToken("1234567890", "abcdefghijklmnop");
		OAuthToken accessToken = oauth10a.exchangeForAccessToken(new AuthorizedRequestToken(requestToken, "verifier"), null);
		assertEquals("9876543210", accessToken.getValue());
		assertEquals("ponmlkjihgfedcba", accessToken.getSecret());
	}

	@Test
	public void exchangeForAccessToken_OAuth10() {
		MockRestServiceServer mockServer = MockRestServiceServer.createServer(oauth10.getRestTemplate());
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		mockServer
				.expect(requestTo(ACCESS_TOKEN_URL))
				.andExpect(method(POST))
				.andExpect(headerContains("Authorization", "oauth_version=\"1.0\""))
				.andExpect(headerContains("Authorization", "oauth_signature_method=\"HMAC-SHA1\""))
				.andExpect(headerContains("Authorization", "oauth_consumer_key=\"consumer_key\""))
				.andExpect(headerContains("Authorization", "oauth_token=\"1234567890\""))
				.andExpect(headerContains("Authorization", "oauth_nonce=\""))
				.andExpect(headerContains("Authorization", "oauth_signature=\""))
				.andExpect(headerContains("Authorization", "oauth_timestamp=\""))
				.andRespond(withSuccess(new ClassPathResource("accessToken.formencoded", getClass()), MediaType.APPLICATION_FORM_URLENCODED));

		OAuthToken requestToken = new OAuthToken("1234567890", "abcdefghijklmnop");
		OAuthToken accessToken = oauth10.exchangeForAccessToken(new AuthorizedRequestToken(requestToken, "verifier"), null);
		assertEquals("9876543210", accessToken.getValue());
		assertEquals("ponmlkjihgfedcba", accessToken.getSecret());
	}
	
	// parameter assertion tests
	
	@Test(expected = IllegalArgumentException.class)
	public void construct_nullConsumerKey() {
		new OAuth1Template(null, "secret", REQUEST_TOKEN_URL, AUTHORIZE_URL, ACCESS_TOKEN_URL);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void construct_nullConsumerSecret() {
		new OAuth1Template("key", null, REQUEST_TOKEN_URL, AUTHORIZE_URL, ACCESS_TOKEN_URL);
	}

	@Test(expected = IllegalArgumentException.class)
	public void construct_nullRequestTokenUrl() {
		new OAuth1Template("key", "secret", null, AUTHORIZE_URL, ACCESS_TOKEN_URL);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void construct_nullAuthorizeUrl() {
		new OAuth1Template("key", "secret", REQUEST_TOKEN_URL, null, ACCESS_TOKEN_URL);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void construct_nullAcessTokenUrl() {
		new OAuth1Template("key", "secret", REQUEST_TOKEN_URL, AUTHORIZE_URL, null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void setRequestFactory_null() {
		oauth10a.setRequestFactory(null);
	}
	

	// private helper
	@SuppressWarnings("unchecked")
	private RequestMatcher headerContains(String name, String substring) {
		return header(name, containsString(substring));
	}

}
