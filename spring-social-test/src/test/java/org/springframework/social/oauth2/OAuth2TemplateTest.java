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
package org.springframework.social.oauth2;

import static org.junit.Assert.*;
import static org.springframework.http.HttpMethod.*;
import static org.springframework.social.test.client.RequestMatchers.*;
import static org.springframework.social.test.client.ResponseCreators.*;

import org.junit.Before;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.social.test.client.MockRestServiceServer;

public class OAuth2TemplateTest {
	
	private static final String AUTHORIZE_URL = "http://www.someprovider.com/oauth/authorize";

	private static final String ACCESS_TOKEN_URL = "http://www.someprovider.com/oauth/accessToken";

	private OAuth2Template oAuth2Template;
	
	@Before
	public void setup() {
		oAuth2Template = new OAuth2Template("client_id", "client_secret", AUTHORIZE_URL, null, ACCESS_TOKEN_URL);
	}

	@Test
	public void buildAuthorizeUrl_codeResponseType() {
		OAuth2Parameters parameters = new OAuth2Parameters();
		parameters.setRedirectUri("http://www.someclient.com/connect/foo");
		parameters.setScope("read,write");
		String expected = AUTHORIZE_URL + "?client_id=client_id&response_type=code&redirect_uri=http%3A%2F%2Fwww.someclient.com%2Fconnect%2Ffoo&scope=read%2Cwrite";
		String actual = oAuth2Template.buildAuthorizeUrl(GrantType.AUTHORIZATION_CODE, parameters);
		assertEquals(expected, actual);
	}

	@Test
	public void buildAuthorizeUrl_tokenResponseType() {
		OAuth2Parameters parameters = new OAuth2Parameters();
		parameters.setRedirectUri("http://www.someclient.com/connect/foo");
		parameters.setScope("read,write");
		String expected = AUTHORIZE_URL + "?client_id=client_id&response_type=token&redirect_uri=http%3A%2F%2Fwww.someclient.com%2Fconnect%2Ffoo&scope=read%2Cwrite";
		String actual = oAuth2Template.buildAuthorizeUrl(GrantType.IMPLICIT_GRANT, parameters);
		assertEquals(expected, actual);
	}

	@Test
	public void buildAuthorizeUrl_noScopeInParameters() {
		OAuth2Parameters parameters = new OAuth2Parameters();
		parameters.setRedirectUri("http://www.someclient.com/connect/foo");
		String expected = AUTHORIZE_URL + "?client_id=client_id&response_type=code&redirect_uri=http%3A%2F%2Fwww.someclient.com%2Fconnect%2Ffoo";
		String actual = oAuth2Template.buildAuthorizeUrl(GrantType.AUTHORIZATION_CODE, parameters);
		assertEquals(expected, actual);
	}

	@Test
	public void buildAuthorizeUrl_additionalParameters() {
		OAuth2Parameters parameters = new OAuth2Parameters();
		parameters.setRedirectUri("http://www.someclient.com/connect/foo");
		parameters.setScope("read,write");
		parameters.add("display", "touch");
		parameters.add("anotherparam", "somevalue1");
		parameters.add("anotherparam", "somevalue2");
		String expected = AUTHORIZE_URL + "?client_id=client_id&response_type=token&redirect_uri=http%3A%2F%2Fwww.someclient.com%2Fconnect%2Ffoo&scope=read%2Cwrite&display=touch&anotherparam=somevalue1&anotherparam=somevalue2";
		String actual = oAuth2Template.buildAuthorizeUrl(GrantType.IMPLICIT_GRANT, parameters);
		assertEquals(expected, actual);
	}

	@Test
	public void exchangeForAccess_jsonResponse() {
		// The OAuth 2 spec draft specifies JSON as the response content type. Gowalla and Github return the access token this way.
		MediaType responseContentType = MediaType.APPLICATION_JSON;
		String responseFile = "accessToken.json";
		AccessGrant accessGrant = getAccessGrant(responseContentType, responseFile);
		assertEquals("8d0a88a5c4f1ae4937ad864cafa8e857", accessGrant.getAccessToken());
		assertEquals("6b0411401bf8751e34f57feb29fb8e32", accessGrant.getRefreshToken());
		long approximateExpirationTime = System.currentTimeMillis() + 40735000;
		long actualExpirationTime = (long) accessGrant.getExpireTime();
		//allow for 1 second of wiggle room on expiration time.
		assertTrue(approximateExpirationTime - actualExpirationTime < 1000);
		assertEquals("read", accessGrant.getScope());
	}

	@Test
	public void exchangeForAccess_jsonResponse_noExpiresIn() {
		// The OAuth 2 spec draft specifies JSON as the response content type. Gowalla and Github return the access token this way.
		MediaType responseContentType = MediaType.APPLICATION_JSON;
		String responseFile = "accessToken_noExpiresIn.json";
		AccessGrant accessGrant = getAccessGrant(responseContentType, responseFile);
		assertEquals("8d0a88a5c4f1ae4937ad864cafa8e857", accessGrant.getAccessToken());
		assertEquals("6b0411401bf8751e34f57feb29fb8e32", accessGrant.getRefreshToken());
		assertNull(accessGrant.getExpireTime());
		assertEquals("read", accessGrant.getScope());
	}
	
	@Test
	public void refreshAccessToken_jsonResponse() {
		MediaType responseContentType = MediaType.APPLICATION_JSON;
		String responseFile = "refreshToken.json";
		AccessGrant accessGrant = refreshToken(responseContentType, responseFile);
		assertEquals("8d0a88a5c4f1ae4937ad864cafa8e857", accessGrant.getAccessToken());
		assertEquals("6b0411401bf8751e34f57feb29fb8e32", accessGrant.getRefreshToken());
		long approximateExpirationTime = System.currentTimeMillis() + 40735000;
		long actualExpirationTime = (long) accessGrant.getExpireTime();
		//allow for 1 second of wiggle room on expiration time.
		assertTrue(approximateExpirationTime - actualExpirationTime < 1000);
		assertNull(accessGrant.getScope());
	}

	@Test
	public void refreshAccessToken_jsonResponse_noExpiresIn() {
		MediaType responseContentType = MediaType.APPLICATION_JSON;
		String responseFile = "refreshToken_noExpiresIn.json";
		AccessGrant accessGrant = refreshToken(responseContentType, responseFile);
		assertEquals("8d0a88a5c4f1ae4937ad864cafa8e857", accessGrant.getAccessToken());
		assertEquals("6b0411401bf8751e34f57feb29fb8e32", accessGrant.getRefreshToken());
		assertNull(accessGrant.getExpireTime());
		assertNull(accessGrant.getScope());
	}
	
	// parameter assertion tests

	@Test(expected = IllegalArgumentException.class)
	public void construct_nullClientId() {
		new OAuth2Template(null, "secret", AUTHORIZE_URL, ACCESS_TOKEN_URL);
	}

	@Test(expected = IllegalArgumentException.class)
	public void construct_nullClientSecret() {
		new OAuth2Template("id", null, AUTHORIZE_URL, ACCESS_TOKEN_URL);
	}

	@Test(expected = IllegalArgumentException.class)
	public void construct_nullAuthorizeUrl() {
		new OAuth2Template("id", "secret", null, ACCESS_TOKEN_URL);
	}

	@Test(expected = IllegalArgumentException.class)
	public void construct_nullAccessTokenUrl() {
		new OAuth2Template("id", "secret", AUTHORIZE_URL, null);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void setRequestFactory_null() {
		oAuth2Template.setRequestFactory(null);
	}


	// private helpers
	
	private AccessGrant getAccessGrant(MediaType responseContentType, String responseFile) {
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.setContentType(responseContentType);
		MockRestServiceServer mockServer = MockRestServiceServer.createServer(oAuth2Template.getRestTemplate());
		mockServer.expect(requestTo(ACCESS_TOKEN_URL))
				.andExpect(method(POST))
				.andExpect(body("client_id=client_id&client_secret=client_secret&code=code&"
								+ "redirect_uri=http%3A%2F%2Fwww.someclient.com%2Fcallback&grant_type=authorization_code"))
				.andRespond(withResponse(new ClassPathResource(responseFile, getClass()), responseHeaders));
		AccessGrant accessGrant = oAuth2Template.exchangeForAccess("code", "http://www.someclient.com/callback", null);
		return accessGrant;
	}

	private AccessGrant refreshToken(MediaType responseContentType, String responseFile) {
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.setContentType(responseContentType);
		MockRestServiceServer mockServer = MockRestServiceServer.createServer(oAuth2Template.getRestTemplate());
		mockServer.expect(requestTo(ACCESS_TOKEN_URL))
				.andExpect(method(POST))
				.andExpect(body("client_id=client_id&client_secret=client_secret&refresh_token=r3fr35h_t0k3n&"
								+ "grant_type=refresh_token"))
				.andRespond(withResponse(new ClassPathResource(responseFile, getClass()), responseHeaders));
		AccessGrant accessGrant = oAuth2Template.refreshAccess("r3fr35h_t0k3n", null, null);
		return accessGrant;
	}

}
