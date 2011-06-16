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
package org.springframework.social.connect.web;

import static org.junit.Assert.*;

import java.net.URL;

import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.social.connect.ApiAdapter;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionValues;
import org.springframework.social.connect.UserProfile;
import org.springframework.social.connect.support.OAuth1ConnectionFactory;
import org.springframework.social.connect.support.OAuth2Connection;
import org.springframework.social.connect.support.OAuth2ConnectionFactory;
import org.springframework.social.oauth1.AuthorizedRequestToken;
import org.springframework.social.oauth1.OAuth1Operations;
import org.springframework.social.oauth1.OAuth1Parameters;
import org.springframework.social.oauth1.OAuth1ServiceProvider;
import org.springframework.social.oauth1.OAuth1Version;
import org.springframework.social.oauth1.OAuthToken;
import org.springframework.social.oauth2.AccessGrant;
import org.springframework.social.oauth2.GrantType;
import org.springframework.social.oauth2.OAuth2Operations;
import org.springframework.social.oauth2.OAuth2Parameters;
import org.springframework.social.oauth2.OAuth2ServiceProvider;
import org.springframework.util.MultiValueMap;
import org.springframework.web.context.request.ServletWebRequest;

public class ConnectSupportTest {

	@Test
	public void buildOAuthUrl_OAuth10() {
		ConnectSupport support = new ConnectSupport();
		MockHttpServletRequest mockRequest = new PortAwareMockHttpServletRequest();
		mockRequest.setScheme("http");
		mockRequest.setServerName("somesite.com");
		mockRequest.setRequestURI("/connect/someprovider");
		ServletWebRequest request = new ServletWebRequest(mockRequest);
		String url = support.buildOAuthUrl(new TestOAuth1ConnectionFactory(OAuth1Version.CORE_10), request);
		assertEquals("https://serviceprovider.com/oauth/authorize?oauth_callback=http://somesite.com/connect/someprovider", url);
	}

	@Test
	public void buildOAuthUrl_OAuth10_withApplicationUrl() throws Exception {
		ConnectSupport support = new ConnectSupport();
		support.setApplicationUrl(new URL("https://someothersite.com:1234"));
		MockHttpServletRequest mockRequest = new PortAwareMockHttpServletRequest();
		mockRequest.setScheme("http");
		mockRequest.setServerName("somesite.com");
		mockRequest.setRequestURI("/connect/someprovider");
		ServletWebRequest request = new ServletWebRequest(mockRequest);
		String url = support.buildOAuthUrl(new TestOAuth1ConnectionFactory(OAuth1Version.CORE_10), request);
		assertEquals("https://serviceprovider.com/oauth/authorize?oauth_callback=https://someothersite.com:1234/connect/someprovider", url);
	}

	@Test
	public void buildOAuthUrl_OAuth10_useAuthenticateUrl() {
		ConnectSupport support = new ConnectSupport();
		support.setUseAuthenticateUrl(true);
		MockHttpServletRequest mockRequest = new PortAwareMockHttpServletRequest();
		mockRequest.setScheme("http");
		mockRequest.setServerName("somesite.com");
		mockRequest.setRequestURI("/connect/someprovider");
		ServletWebRequest request = new ServletWebRequest(mockRequest);
		String url = support.buildOAuthUrl(new TestOAuth1ConnectionFactory(OAuth1Version.CORE_10), request);
		assertEquals("https://serviceprovider.com/oauth/authenticate?oauth_callback=http://somesite.com/connect/someprovider", url);
	}

	@Test
	public void buildOAuthUrl_OAuth10a() {
		ConnectSupport support = new ConnectSupport();
		MockHttpServletRequest mockRequest = new PortAwareMockHttpServletRequest();
		mockRequest.setScheme("http");
		mockRequest.setServerName("somesite.com");
		mockRequest.setRequestURI("/connect/someprovider");
		ServletWebRequest request = new ServletWebRequest(mockRequest);
		String url = support.buildOAuthUrl(new TestOAuth1ConnectionFactory(OAuth1Version.CORE_10_REVISION_A), request);
		assertEquals("https://serviceprovider.com/oauth/authorize", url);
	}
	
	@Test
	public void buildOAuthUrl_OAuth10a_withApplicationUrl() throws Exception {
		ConnectSupport support = new ConnectSupport();
		support.setApplicationUrl(new URL("https://someothersite.com:1234"));
		MockHttpServletRequest mockRequest = new PortAwareMockHttpServletRequest();
		mockRequest.setScheme("http");
		mockRequest.setServerName("somesite.com");
		mockRequest.setRequestURI("/connect/someprovider");
		ServletWebRequest request = new ServletWebRequest(mockRequest);
		String url = support.buildOAuthUrl(new TestOAuth1ConnectionFactory(OAuth1Version.CORE_10_REVISION_A), request);
		assertEquals("https://serviceprovider.com/oauth/authorize", url);
	}

	@Test
	public void buildOAuthUrl_OAuth10a_useAuthenticateUrl() {
		ConnectSupport support = new ConnectSupport();
		support.setUseAuthenticateUrl(true);
		MockHttpServletRequest mockRequest = new PortAwareMockHttpServletRequest();
		mockRequest.setScheme("http");
		mockRequest.setServerName("somesite.com");
		mockRequest.setRequestURI("/connect/someprovider");
		ServletWebRequest request = new ServletWebRequest(mockRequest);
		String url = support.buildOAuthUrl(new TestOAuth1ConnectionFactory(OAuth1Version.CORE_10_REVISION_A), request);
		assertEquals("https://serviceprovider.com/oauth/authenticate", url);
	}
	
	@Test
	public void buildOAuthUrl_OAuth2() {
		ConnectSupport support = new ConnectSupport();
		MockHttpServletRequest mockRequest = new PortAwareMockHttpServletRequest();
		mockRequest.setScheme("http");
		mockRequest.setServerName("somesite.com");
		mockRequest.setRequestURI("/connect/someprovider");
		ServletWebRequest request = new ServletWebRequest(mockRequest);
		String url = support.buildOAuthUrl(new TestOAuth2ConnectionFactory(), request);
		assertEquals("https://serviceprovider.com/oauth/authorize?redirect_uri=http://somesite.com/connect/someprovider", url);
	}

	@Test
	public void buildOAuthUrl_OAuth2_withApplicationUrl() throws Exception {
		ConnectSupport support = new ConnectSupport();
		support.setApplicationUrl(new URL("https://someothersite.com:1234"));
		MockHttpServletRequest mockRequest = new PortAwareMockHttpServletRequest();
		mockRequest.setScheme("http");
		mockRequest.setServerName("somesite.com");
		mockRequest.setRequestURI("/connect/someprovider");
		ServletWebRequest request = new ServletWebRequest(mockRequest);
		String url = support.buildOAuthUrl(new TestOAuth2ConnectionFactory(), request);
		assertEquals("https://serviceprovider.com/oauth/authorize?redirect_uri=https://someothersite.com:1234/connect/someprovider", url);
	}

	@Test
	public void buildOAuthUrl_OAuth2_useAuthenticateUrl() throws Exception {
		ConnectSupport support = new ConnectSupport();
		support.setUseAuthenticateUrl(true);
		MockHttpServletRequest mockRequest = new PortAwareMockHttpServletRequest();
		mockRequest.setScheme("http");
		mockRequest.setServerName("somesite.com");
		mockRequest.setRequestURI("/connect/someprovider");
		ServletWebRequest request = new ServletWebRequest(mockRequest);
		TestOAuth2ConnectionFactory connectionFactory = new TestOAuth2ConnectionFactory();
		String url = support.buildOAuthUrl(connectionFactory, request);
		assertEquals("https://serviceprovider.com/oauth/authenticate?redirect_uri=http://somesite.com/connect/someprovider", url);
	}

	private static class PortAwareMockHttpServletRequest extends MockHttpServletRequest {

		@Override
		public StringBuffer getRequestURL() {
			StringBuffer url = new StringBuffer(getScheme());
			url.append("://").append(getServerName());
			int port = getServerPort();
			// only add the port if not 80 or 443.
			// could consider scheme when deciding on the port, but this is fine for this test.
			if (port != 80 && port != 443) { 
				url.append(':').append(port);
			}
			url.append(getRequestURI());
			return url;
		}		
	}

	@Test
	public void completeConnection_OAuth1() {
		ConnectSupport support = new ConnectSupport();
		MockHttpServletRequest mockRequest = new PortAwareMockHttpServletRequest();
		mockRequest.addParameter("oauth_verifier", "verifier");
		mockRequest.getSession().setAttribute("oauthToken", new OAuthToken("requestToken", "requestTokenSecret"));
		mockRequest.setScheme("http");
		mockRequest.setServerName("somesite.com");
		mockRequest.setRequestURI("/connect/someprovider");
		ServletWebRequest request = new ServletWebRequest(mockRequest);
		Connection<?> connection = support.completeConnection(new TestOAuth1ConnectionFactory(OAuth1Version.CORE_10_REVISION_A), request);
		assertEquals("TestUser", connection.getDisplayName());
		assertEquals("http://someprovider.com/images/testuser.jpg", connection.getImageUrl());
		assertEquals("http://someprovider.com/testuser", connection.getProfileUrl());
	}

	@Test
	public void completeConnection_OAuth2() {
		ConnectSupport support = new ConnectSupport();
		MockHttpServletRequest mockRequest = new PortAwareMockHttpServletRequest();
		mockRequest.addParameter("code", "authorization-grant");
		mockRequest.setScheme("http");
		mockRequest.setServerName("somesite.com");
		mockRequest.setRequestURI("/connect/someprovider");
		ServletWebRequest request = new ServletWebRequest(mockRequest);
		Connection<?> connection = support.completeConnection(new TestOAuth2ConnectionFactory(), request);
		assertEquals("TestUser", connection.getDisplayName());
		assertEquals("http://someprovider.com/images/testuser.jpg", connection.getImageUrl());
		assertEquals("http://someprovider.com/testuser", connection.getProfileUrl());
	}

	// private helpers
	
	private static class TestOAuth1ConnectionFactory extends OAuth1ConnectionFactory<TestApi> {

		public TestOAuth1ConnectionFactory(OAuth1Version version) {
			super("someprovider", new TestOAuth1ServiceProvider(version), new TestApiAdapter());
		}
		
	}
	
	private static class TestOAuth1ServiceProvider implements OAuth1ServiceProvider<TestApi> {

		private final OAuth1Version version;

		public TestOAuth1ServiceProvider(OAuth1Version version) {
			this.version = version;
		}
		
		public OAuth1Operations getOAuthOperations() {
			return new OAuth1Operations() {
				public OAuth1Version getVersion() {
					return version;
				}

				public OAuthToken fetchRequestToken(String callbackUrl, MultiValueMap<String, String> additionalParameters) {
					return new OAuthToken("requestTokenValue", "requestTokenSecret");
				}

				public String buildAuthorizeUrl(String requestToken, OAuth1Parameters params) {
					String callbackUrl = params.getCallbackUrl();
					return "https://serviceprovider.com/oauth/authorize" + callbackQuery(callbackUrl);
				}

				public String buildAuthenticateUrl(String requestToken, OAuth1Parameters params) {
					String callbackUrl = params.getCallbackUrl();
					return "https://serviceprovider.com/oauth/authenticate" + callbackQuery(callbackUrl);
				}

				public OAuthToken exchangeForAccessToken(AuthorizedRequestToken requestToken, MultiValueMap<String, String> additionalParameters) {
					assertEquals("requestToken", requestToken.getValue());
					assertEquals("requestTokenSecret", requestToken.getSecret());
					assertNull(additionalParameters);
					return new OAuthToken("accessToken", "accessTokenSecret");
				}								

				private String callbackQuery(String callbackUrl) {
					String callbackQuery = "";
					if (callbackUrl != null) {
						callbackQuery = "?oauth_callback=" + callbackUrl;
					}
					return callbackQuery;
				}

			};
		}
		
		public TestApi getApi(String accessToken, String secret) {
			return null;
		}
	}

	private static class TestOAuth2ConnectionFactory extends OAuth2ConnectionFactory<TestApi> {

		private static final TestApiAdapter API_ADAPTER = new TestApiAdapter();

		private static final TestOAuth2ServiceProvider SERVICE_PROVIDER = new TestOAuth2ServiceProvider();

		public TestOAuth2ConnectionFactory() {
			super("someprovider", SERVICE_PROVIDER, API_ADAPTER);
		}
		
		@Override
		public Connection<TestApi> createConnection(AccessGrant accessGrant) {
			return new OAuth2Connection<TestApi>("someprovider", "providerUserId", accessGrant.getAccessToken(),
					accessGrant.getRefreshToken(), accessGrant.getExpireTime(), SERVICE_PROVIDER, API_ADAPTER);		
		}
	}
	
	private static class TestOAuth2ServiceProvider implements OAuth2ServiceProvider<TestApi> {

		public OAuth2Operations getOAuthOperations() {
			return new OAuth2Operations() {
				public String buildAuthorizeUrl(GrantType grantType, OAuth2Parameters params) {
					return "https://serviceprovider.com/oauth/authorize?redirect_uri=" + params.getRedirectUri();
				}
				public String buildAuthenticateUrl(GrantType grantType, OAuth2Parameters params) {
					return "https://serviceprovider.com/oauth/authenticate?redirect_uri=" + params.getRedirectUri();
				}
				public AccessGrant exchangeForAccess(String authorizationGrant, String redirectUri, MultiValueMap<String, String> additionalParameters) {
					assertEquals("authorization-grant", authorizationGrant);
					assertEquals("http://somesite.com/connect/someprovider", redirectUri);
					assertNull(additionalParameters);
					return new AccessGrant("access-token");
				}
				public AccessGrant refreshAccess(String refreshToken, String scope, MultiValueMap<String, String> additionalParameters) {
					return null;
				}								
			};
		}

		public TestApi getApi(final String accessToken) {
			return null;
		}		
	}
	
	public interface TestApi {
	}
	
	private static class TestApiAdapter implements ApiAdapter<TestApi> {

		public boolean test(TestApi api) {
			return true;
		}

		public void setConnectionValues(TestApi api, ConnectionValues values) {
			values.setDisplayName("TestUser");
			values.setImageUrl("http://someprovider.com/images/testuser.jpg");
			values.setProfileUrl("http://someprovider.com/testuser");
			values.setProviderUserId("testuser");
		}

		public UserProfile fetchUserProfile(TestApi api) {
			return null;
		}

		public void updateStatus(TestApi api, String message) {
			
		}
		
	}
}
