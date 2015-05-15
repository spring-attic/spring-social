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
package org.springframework.social.oauth2;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import org.junit.Before;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.client.ResponseActions;

public class OAuth2TemplateTest {

	private static final String AUTHORIZE_URL = "http://www.someprovider.com/oauth/authorize";

	private static final String ACCESS_TOKEN_URL = "http://www.someprovider.com/oauth/accessToken";

	private OAuth2Template oAuth2Template;
	private OAuth2Template oAuth2TemplateParamBased;

	@Before
	public void setup() {
		oAuth2Template = new OAuth2Template("client_id", "client_secret", AUTHORIZE_URL, null, ACCESS_TOKEN_URL);
		oAuth2TemplateParamBased = new OAuth2Template("client_id", "client_secret", AUTHORIZE_URL, null, ACCESS_TOKEN_URL);
		oAuth2TemplateParamBased.setUseParametersForClientAuthentication(true);
	}

	@Test
	public void buildAuthorizeUrl_codeResponseType() {
		OAuth2Parameters parameters = new OAuth2Parameters();
		parameters.setRedirectUri("http://www.someclient.com/connect/foo");
		parameters.setScope("read,write");
		String expected = AUTHORIZE_URL + "?client_id=client_id&response_type=code&redirect_uri=http%3A%2F%2Fwww.someclient.com%2Fconnect%2Ffoo&scope=read%2Cwrite";
		String actual = oAuth2Template.buildAuthorizeUrl(parameters);
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
		String actual = oAuth2Template.buildAuthorizeUrl(parameters);
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
		AccessGrant accessGrant = getAccessGrant("accessToken.json");
		assertEquals("8d0a88a5c4f1ae4937ad864cafa8e857", accessGrant.getAccessToken());
		assertEquals("6b0411401bf8751e34f57feb29fb8e32", accessGrant.getRefreshToken());
		long approximateExpirationTime = System.currentTimeMillis() + 40735000;
		long actualExpirationTime = (long) accessGrant.getExpireTime();
		//allow for 1 second of wiggle room on expiration time.
		assertTrue(approximateExpirationTime - actualExpirationTime < 1000);
		assertEquals("read", accessGrant.getScope());
	}

	
	@Test
	public void exchangeForAccess_paramBasedClientAuthentication_jsonResponse() {
		AccessGrant accessGrant = getAccessGrant_paramBasedClientAuth("accessToken.json");
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
		AccessGrant accessGrant = getAccessGrant("accessToken_noExpiresIn.json");
		assertEquals("8d0a88a5c4f1ae4937ad864cafa8e857", accessGrant.getAccessToken());
		assertEquals("6b0411401bf8751e34f57feb29fb8e32", accessGrant.getRefreshToken());
		assertNull(accessGrant.getExpireTime());
		assertEquals("read", accessGrant.getScope());
	}

	@Test
	public void exchangeForAccess_paramBasedClientAuthentication_jsonResponse_noExpiresIn() {
		AccessGrant accessGrant = getAccessGrant_paramBasedClientAuth("accessToken_noExpiresIn.json");
		assertEquals("8d0a88a5c4f1ae4937ad864cafa8e857", accessGrant.getAccessToken());
		assertEquals("6b0411401bf8751e34f57feb29fb8e32", accessGrant.getRefreshToken());
		assertNull(accessGrant.getExpireTime());
		assertEquals("read", accessGrant.getScope());
	}

	@Test
	public void exchangeForAccess_jsonResponse_noExpiresInOrScope() {
		AccessGrant accessGrant = getAccessGrant("accessToken_noExpiresInOrScope.json");
		assertEquals("8d0a88a5c4f1ae4937ad864cafa8e857", accessGrant.getAccessToken());
		assertEquals("6b0411401bf8751e34f57feb29fb8e32", accessGrant.getRefreshToken());
		assertNull(accessGrant.getExpireTime());
		assertNull(accessGrant.getScope());
	}
	
	@Test
	public void exchangeForAccess_paramBasedClientAuthentication_jsonResponse_noExpiresInOrScope() {
		AccessGrant accessGrant = getAccessGrant_paramBasedClientAuth("accessToken_noExpiresInOrScope.json");
		assertEquals("8d0a88a5c4f1ae4937ad864cafa8e857", accessGrant.getAccessToken());
		assertEquals("6b0411401bf8751e34f57feb29fb8e32", accessGrant.getRefreshToken());
		assertNull(accessGrant.getExpireTime());
		assertNull(accessGrant.getScope());
	}

	@Test
	public void exchangeForAccess_jsonResponse_expiresInAsString() {
		AccessGrant accessGrant = getAccessGrant("accessToken_expiresInAsString.json");
		assertEquals("8d0a88a5c4f1ae4937ad864cafa8e857", accessGrant.getAccessToken());
		assertEquals("6b0411401bf8751e34f57feb29fb8e32", accessGrant.getRefreshToken());
		long approximateExpirationTime = System.currentTimeMillis() + 40735000;
		long actualExpirationTime = (long) accessGrant.getExpireTime();
		//allow for 1 second of wiggle room on expiration time.
		assertTrue(approximateExpirationTime - actualExpirationTime < 1000);
		assertEquals("read", accessGrant.getScope());
	}

	@Test
	public void exchangeForAccess_paramBasedClientAuthentication_jsonResponse_expiresInAsString() {
		AccessGrant accessGrant = getAccessGrant_paramBasedClientAuth("accessToken_expiresInAsString.json");
		assertEquals("8d0a88a5c4f1ae4937ad864cafa8e857", accessGrant.getAccessToken());
		assertEquals("6b0411401bf8751e34f57feb29fb8e32", accessGrant.getRefreshToken());
		long approximateExpirationTime = System.currentTimeMillis() + 40735000;
		long actualExpirationTime = (long) accessGrant.getExpireTime();
		//allow for 1 second of wiggle room on expiration time.
		assertTrue(approximateExpirationTime - actualExpirationTime < 1000);
		assertEquals("read", accessGrant.getScope());
	}

	@Test
	public void exchangeForAccess_jsonResponse_expiresInAsNonNumericString() {
		AccessGrant accessGrant = getAccessGrant("accessToken_expiresInAsNonNumericString.json");
		assertEquals("8d0a88a5c4f1ae4937ad864cafa8e857", accessGrant.getAccessToken());
		assertEquals("6b0411401bf8751e34f57feb29fb8e32", accessGrant.getRefreshToken());
		assertNull(accessGrant.getExpireTime());
		assertEquals("read", accessGrant.getScope());
	}

	@Test
	public void exchangeForAccess_paramBasedClientAuthentication_jsonResponse_expiresInAsNonNumericString() {
		AccessGrant accessGrant = getAccessGrant_paramBasedClientAuth("accessToken_expiresInAsNonNumericString.json");
		assertEquals("8d0a88a5c4f1ae4937ad864cafa8e857", accessGrant.getAccessToken());
		assertEquals("6b0411401bf8751e34f57feb29fb8e32", accessGrant.getRefreshToken());
		assertNull(accessGrant.getExpireTime());
		assertEquals("read", accessGrant.getScope());
	}

	@Test
	public void refreshAccessToken_jsonResponse() {
		AccessGrant accessGrant = refreshToken("refreshToken.json");
		assertEquals("8d0a88a5c4f1ae4937ad864cafa8e857", accessGrant.getAccessToken());
		assertEquals("6b0411401bf8751e34f57feb29fb8e32", accessGrant.getRefreshToken());
		long approximateExpirationTime = System.currentTimeMillis() + 40735000;
		long actualExpirationTime = (long) accessGrant.getExpireTime();
		//allow for 1 second of wiggle room on expiration time.
		assertTrue(approximateExpirationTime - actualExpirationTime < 1000);
		assertNull(accessGrant.getScope());
	}
	
	@Test
	public void exchangeCredentialsForAccess() {
		AccessGrant accessGrant = passwordGrant("accessToken.json");
		assertEquals("8d0a88a5c4f1ae4937ad864cafa8e857", accessGrant.getAccessToken());
		assertEquals("6b0411401bf8751e34f57feb29fb8e32", accessGrant.getRefreshToken());
		long approximateExpirationTime = System.currentTimeMillis() + 40735000;
		long actualExpirationTime = (long) accessGrant.getExpireTime();
		//allow for 1 second of wiggle room on expiration time.
		assertTrue(approximateExpirationTime - actualExpirationTime < 1000);
		assertEquals("read", accessGrant.getScope());
	}

	@Test
	public void exchangeCredentialsForAccess_paramBasedClientAuthentication() {
		AccessGrant accessGrant = passwordGrant_paramBasedClientAuth("accessToken.json");
		assertEquals("8d0a88a5c4f1ae4937ad864cafa8e857", accessGrant.getAccessToken());
		assertEquals("6b0411401bf8751e34f57feb29fb8e32", accessGrant.getRefreshToken());
		long approximateExpirationTime = System.currentTimeMillis() + 40735000;
		long actualExpirationTime = (long) accessGrant.getExpireTime();
		//allow for 1 second of wiggle room on expiration time.
		assertTrue(approximateExpirationTime - actualExpirationTime < 1000);
		assertEquals("read", accessGrant.getScope());
	}
	
	@Test
	public void refreshAccessToken_paramBasedClientAuthentication_jsonResponse() {
		AccessGrant accessGrant = refreshToken_paramBasedClientAuth("refreshToken.json");
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
		AccessGrant accessGrant = refreshToken("refreshToken_noExpiresIn.json");
		assertEquals("8d0a88a5c4f1ae4937ad864cafa8e857", accessGrant.getAccessToken());
		assertEquals("6b0411401bf8751e34f57feb29fb8e32", accessGrant.getRefreshToken());
		assertNull(accessGrant.getExpireTime());
		assertNull(accessGrant.getScope());
	}
	
	@Test
	public void refreshAccessToken_paramBasedClientAuthentication_jsonResponse_noExpiresIn() {
		AccessGrant accessGrant = refreshToken_paramBasedClientAuth("refreshToken_noExpiresIn.json");
		assertEquals("8d0a88a5c4f1ae4937ad864cafa8e857", accessGrant.getAccessToken());
		assertEquals("6b0411401bf8751e34f57feb29fb8e32", accessGrant.getRefreshToken());
		assertNull(accessGrant.getExpireTime());
		assertNull(accessGrant.getScope());
	}

	@Test
	public void authenticateClient() {
		AccessGrant accessGrant = clientCredentials("accessToken_noUser.json");
		assertEquals("8d0a88a5c4f1ae4937ad864cafa8e857", accessGrant.getAccessToken());
		long approximateExpirationTime = System.currentTimeMillis() + 40735000;
		long actualExpirationTime = (long) accessGrant.getExpireTime();
		//allow for 1 second of wiggle room on expiration time.
		assertTrue(approximateExpirationTime - actualExpirationTime < 1000);
		assertEquals("read,write", accessGrant.getScope());
	}

	@Test
	public void authenticateClient_paramBasedClientAuthentication() {
		AccessGrant accessGrant = clientCredentials_paramBasedClientAuth("accessToken_noUser.json");
		assertEquals("8d0a88a5c4f1ae4937ad864cafa8e857", accessGrant.getAccessToken());
		long approximateExpirationTime = System.currentTimeMillis() + 40735000;
		long actualExpirationTime = (long) accessGrant.getExpireTime();
		//allow for 1 second of wiggle room on expiration time.
		assertTrue(approximateExpirationTime - actualExpirationTime < 1000);
		assertEquals("read,write", accessGrant.getScope());
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

	private AccessGrant getAccessGrant_paramBasedClientAuth(String responseFile) {
		return getAccessGrant(oAuth2TemplateParamBased, "client_id=client_id&client_secret=client_secret&", null, responseFile);
	}


	private AccessGrant getAccessGrant(String responseFile) {
		return getAccessGrant(oAuth2Template, "", "Basic Y2xpZW50X2lkOmNsaWVudF9zZWNyZXQ=", responseFile);
	}
	
	private AccessGrant getAccessGrant(OAuth2Template oauthTemplate, String expectedClientParams, String expectedAuthorizationHeader, String responseFile) {
		MockRestServiceServer mockServer = MockRestServiceServer.createServer(oauthTemplate.getRestTemplate());
		ResponseActions responseActions = mockServer.expect(requestTo(ACCESS_TOKEN_URL))
				.andExpect(method(POST))
				.andExpect(content().string(expectedClientParams + "code=code&redirect_uri=http%3A%2F%2Fwww.someclient.com%2Fcallback&grant_type=authorization_code"));
		if (expectedAuthorizationHeader != null) {
			responseActions.andExpect(header("Authorization", expectedAuthorizationHeader));
		}
		responseActions.andRespond(withSuccess(new ClassPathResource(responseFile, getClass()), MediaType.APPLICATION_JSON));
		return oauthTemplate.exchangeForAccess("code", "http://www.someclient.com/callback", null);
	}
	
	private AccessGrant passwordGrant_paramBasedClientAuth(String responseFile) {
		return passwordGrant(oAuth2TemplateParamBased, "client_id=client_id&client_secret=client_secret&", null, responseFile);
	}

	private AccessGrant passwordGrant(String responseFile) {
		return passwordGrant(oAuth2Template, "", "Basic Y2xpZW50X2lkOmNsaWVudF9zZWNyZXQ=", responseFile);
	}
	
	private AccessGrant passwordGrant(OAuth2Template oauthTemplate, String expectedClientParams, String expectedAuthorizationHeader, String responseFile) {
		MockRestServiceServer mockServer = MockRestServiceServer.createServer(oauthTemplate.getRestTemplate());
		ResponseActions responseActions = mockServer.expect(requestTo(ACCESS_TOKEN_URL))
				.andExpect(method(POST))
				.andExpect(content().string(expectedClientParams + "username=habuma&password=letmein01&grant_type=password&scope=read%2Cwrite"));
		if (expectedAuthorizationHeader != null) {
			responseActions.andExpect(header("Authorization", expectedAuthorizationHeader));
		}
		responseActions.andRespond(withSuccess(new ClassPathResource(responseFile, getClass()), MediaType.APPLICATION_JSON));
		OAuth2Parameters parameters = new OAuth2Parameters();
		parameters.setScope("read,write");
		return oauthTemplate.exchangeCredentialsForAccess("habuma", "letmein01", parameters);
	}

	private AccessGrant clientCredentials_paramBasedClientAuth(String responseFile) {
		return clientCredentials(oAuth2TemplateParamBased, "client_id=client_id&client_secret=client_secret&", null, responseFile);
	}

	private AccessGrant clientCredentials(String responseFile) {
		return clientCredentials(oAuth2Template, "", "Basic Y2xpZW50X2lkOmNsaWVudF9zZWNyZXQ=", responseFile);
	}

    private AccessGrant clientCredentials(OAuth2Template oauthTemplate, String expectedClientParams, String expectedAuthorizationHeader, String responseFile) {
		MockRestServiceServer mockServer = MockRestServiceServer.createServer(oauthTemplate.getRestTemplate());
		ResponseActions responseActions = mockServer.expect(requestTo(ACCESS_TOKEN_URL))
				.andExpect(method(POST))
				.andExpect(content().string(expectedClientParams + "grant_type=client_credentials&scope=read%2Cwrite"));
		if (expectedAuthorizationHeader != null) {
			responseActions.andExpect(header("Authorization", expectedAuthorizationHeader));
		}
		responseActions.andRespond(withSuccess(new ClassPathResource(responseFile, getClass()), MediaType.APPLICATION_JSON));
		OAuth2Parameters parameters = new OAuth2Parameters();
		parameters.setScope("read,write");
		return oauthTemplate.authenticateClient("read,write");
	}

	private AccessGrant refreshToken_paramBasedClientAuth(String responseFile) {
		return refreshToken(oAuth2TemplateParamBased, "client_id=client_id&client_secret=client_secret&", null, responseFile);
	}

	private AccessGrant refreshToken(String responseFile) {
		return refreshToken(oAuth2Template, "", "Basic Y2xpZW50X2lkOmNsaWVudF9zZWNyZXQ=", responseFile);
	}
	
	private AccessGrant refreshToken(OAuth2Template oauthTemplate, String expectedClientParams, String expectedAuthorizationHeader, String responseFile) {
		MockRestServiceServer mockServer = MockRestServiceServer.createServer(oauthTemplate.getRestTemplate());
		ResponseActions responseActions = mockServer.expect(requestTo(ACCESS_TOKEN_URL))
				.andExpect(method(POST))
				.andExpect(content().string(expectedClientParams + "refresh_token=r3fr35h_t0k3n&grant_type=refresh_token"));
		if (expectedAuthorizationHeader != null) {
			responseActions.andExpect(header("Authorization", expectedAuthorizationHeader));
		}
		responseActions.andRespond(withSuccess(new ClassPathResource(responseFile, getClass()), MediaType.APPLICATION_JSON));
		return oauthTemplate.refreshAccess("r3fr35h_t0k3n", null);
	}

}
