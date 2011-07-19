package org.springframework.social.connect.web;

import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.util.Set;

import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionData;
import org.springframework.social.connect.ConnectionFactory;
import org.springframework.social.connect.ConnectionFactoryLocator;
import org.springframework.social.connect.ConnectionRepository;
import org.springframework.social.connect.UsersConnectionRepository;
import org.springframework.social.connect.support.OAuth1Connection;
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
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.servlet.view.RedirectView;

public class ProviderSignInControllerTest {

	@Test
	public void oauth1Callback_noMatchingUser() {
		ConnectionFactoryLocator connectionFactoryLocator = new TestConnectionFactoryLocator();
		UsersConnectionRepository usersConnectionRepository = new TestUsersConnectionRepository("oauth1provider", null);
		SignInAdapter signInAdapter = null;
		ProviderSignInController controller = new ProviderSignInController(connectionFactoryLocator, usersConnectionRepository, signInAdapter);
		
		MockHttpServletRequest nativeRequest = new MockHttpServletRequest();
		nativeRequest.addParameter("verifier", "verifier");
		ServletWebRequest request = new ServletWebRequest(nativeRequest);
		RedirectView redirect = controller.oauth1Callback("oauth1provider", request);
		assertEquals("/signup", redirect.getUrl());
		ProviderSignInAttempt signInAttempt = (ProviderSignInAttempt) request.getAttribute(ProviderSignInAttempt.SESSION_ATTRIBUTE, RequestAttributes.SCOPE_SESSION);
		assertNotNull(signInAttempt);
		// TODO: Assert attempt contents
	}

	@Test
	public void oauth1Callback_noMatchingUser_customSignUpUrl() {
		ConnectionFactoryLocator connectionFactoryLocator = new TestConnectionFactoryLocator();
		UsersConnectionRepository usersConnectionRepository = new TestUsersConnectionRepository("oauth1provider", null);
		SignInAdapter signInAdapter = null;
		ProviderSignInController controller = new ProviderSignInController(connectionFactoryLocator, usersConnectionRepository, signInAdapter);
		controller.setSignUpUrl("/register");

		MockHttpServletRequest nativeRequest = new MockHttpServletRequest();
		nativeRequest.addParameter("verifier", "verifier");
		ServletWebRequest request = new ServletWebRequest(nativeRequest);
		RedirectView redirect = controller.oauth1Callback("oauth1provider", request);
		assertEquals("/register", redirect.getUrl());
		ProviderSignInAttempt signInAttempt = (ProviderSignInAttempt) request.getAttribute(ProviderSignInAttempt.SESSION_ATTRIBUTE, RequestAttributes.SCOPE_SESSION);
		assertNotNull(signInAttempt);
		// TODO: Assert attempt contents
	}

	@Test
	public void oauth1Callback_matchingUser_noOriginalUrl() {
		performOAuth1Callback(null, null);
	}

	@Test
	public void oauth1Callback_matchingUser_noOriginalUrl_withPostSignInUrl() {
		performOAuth1Callback(null, "/postSignIn");
	}

	@Test
	public void oauth1Callback_matchingUser_withOriginalUrl() {
		performOAuth1Callback("/original", null);
	}
	
	@Test
	public void oauth2Callback_noMatchingUser() {
		ConnectionFactoryLocator connectionFactoryLocator = new TestConnectionFactoryLocator();
		UsersConnectionRepository usersConnectionRepository = new TestUsersConnectionRepository("oauth2provider", null);
		SignInAdapter signInAdapter = null;
		ProviderSignInController controller = new ProviderSignInController(connectionFactoryLocator, usersConnectionRepository, signInAdapter);
		
		MockHttpServletRequest nativeRequest = new MockHttpServletRequest();
		nativeRequest.addParameter("code", "authcode");
		ServletWebRequest request = new ServletWebRequest(nativeRequest);
		RedirectView redirect = controller.oauth2Callback("oauth2provider", "authcode", request);
		assertEquals("/signup", redirect.getUrl());
		ProviderSignInAttempt signInAttempt = (ProviderSignInAttempt) request.getAttribute(ProviderSignInAttempt.SESSION_ATTRIBUTE, RequestAttributes.SCOPE_SESSION);
		assertNotNull(signInAttempt);
		// TODO: Assert attempt contents
	}

	@Test
	public void oauth2Callback_noMatchingUser_customSignUpUrl() {
		ConnectionFactoryLocator connectionFactoryLocator = new TestConnectionFactoryLocator();
		UsersConnectionRepository usersConnectionRepository = new TestUsersConnectionRepository("oauth2provider", null);
		SignInAdapter signInAdapter = null;
		ProviderSignInController controller = new ProviderSignInController(connectionFactoryLocator, usersConnectionRepository, signInAdapter);
		controller.setSignUpUrl("/register");
		
		MockHttpServletRequest nativeRequest = new MockHttpServletRequest();
		nativeRequest.addParameter("code", "authcode");
		ServletWebRequest request = new ServletWebRequest(nativeRequest);
		RedirectView redirect = controller.oauth2Callback("oauth2provider", "authcode", request);
		assertEquals("/register", redirect.getUrl());
		ProviderSignInAttempt signInAttempt = (ProviderSignInAttempt) request.getAttribute(ProviderSignInAttempt.SESSION_ATTRIBUTE, RequestAttributes.SCOPE_SESSION);
		assertNotNull(signInAttempt);
		// TODO: Assert attempt contents
	}
	
	@Test
	public void oauth2Callback_matchingUser_noOriginalUrl() {
		performOAuth2Callback(null, null);
	}

	@Test
	public void oauth2Callback_matchingUser_noOriginalUrl_withPostSignInUrl() {
		performOAuth2Callback(null, "/postSignIn");
	}

	@Test
	public void oauth2Callback_matchingUser_withOriginalUrl() {
		performOAuth2Callback("/original", null);
	}

	private void performOAuth1Callback(String originalUrl, String postSignInUrl) {
		ConnectionFactoryLocator connectionFactoryLocator = new TestConnectionFactoryLocator();
		TestUsersConnectionRepository usersConnectionRepository = new TestUsersConnectionRepository("oauth1provider", "testuser");
		SignInAdapter signInAdapter = new TestSignInAdapter(originalUrl);
		ProviderSignInController controller = new ProviderSignInController(connectionFactoryLocator, usersConnectionRepository, signInAdapter);
		if (postSignInUrl != null) {
			controller.setPostSignInUrl(postSignInUrl);
		}
		
		MockHttpServletRequest nativeRequest = new MockHttpServletRequest();
		nativeRequest.addParameter("verifier", "verifier");
		ServletWebRequest request = new ServletWebRequest(nativeRequest);
		RedirectView redirect = controller.oauth1Callback("oauth1provider", request);
		if (originalUrl == null) {
			if (postSignInUrl == null) {
				assertEquals("/", redirect.getUrl());
			} else {
				assertEquals(postSignInUrl, redirect.getUrl());
			}
		} else {			
			assertEquals(originalUrl, redirect.getUrl());
		}
		usersConnectionRepository.verifyUpdateConnection();
	}

	private void performOAuth2Callback(String originalUrl, String postSignInUrl) {
		ConnectionFactoryLocator connectionFactoryLocator = new TestConnectionFactoryLocator();
		TestUsersConnectionRepository usersConnectionRepository = new TestUsersConnectionRepository("oauth2provider", "testuser");
		SignInAdapter signInAdapter = new TestSignInAdapter(originalUrl);
		ProviderSignInController controller = new ProviderSignInController(connectionFactoryLocator, usersConnectionRepository, signInAdapter);
		if (postSignInUrl != null) {
			controller.setPostSignInUrl(postSignInUrl);
		}
		
		MockHttpServletRequest nativeRequest = new MockHttpServletRequest();
		nativeRequest.addParameter("code", "authcode");
		ServletWebRequest request = new ServletWebRequest(nativeRequest);
		RedirectView redirect = controller.oauth2Callback("oauth2provider", "authcode", request);
		if (originalUrl == null) {
			if (postSignInUrl == null) {
				assertEquals("/", redirect.getUrl());
			} else {
				assertEquals(postSignInUrl, redirect.getUrl());
			}
		} else {			
			assertEquals(originalUrl, redirect.getUrl());
		}
		usersConnectionRepository.verifyUpdateConnection();
	}
	
	private static class TestConnectionFactoryLocator implements ConnectionFactoryLocator {
		public ConnectionFactory<?> getConnectionFactory(String providerId) {
			if(providerId.equals("oauth1provider")) {
				return new TestOAuth1ConnectionFactory();
			} else {
				return new TestOAuth2ConnectionFactory();
			}
		}

		public <A> ConnectionFactory<A> getConnectionFactory(Class<A> apiType) {
			return null;
		}

		public Set<String> registeredProviderIds() {
			return null;
		}		
	}
	
	private static class TestUsersConnectionRepository implements UsersConnectionRepository {
		private final String matchingUserId;
		private ConnectionRepository connectionRepository;
		private final String providerId;

		public TestUsersConnectionRepository(String providerId, String matchingUserId) {
			this.providerId = providerId;
			this.matchingUserId = matchingUserId;
			connectionRepository = mock(ConnectionRepository.class);
		}
		
		public String findUserIdWithConnection(Connection<?> connection) {
			return matchingUserId;
		}

		public Set<String> findUserIdsConnectedTo(String providerId, Set<String> providerUserIds) {
			return null;
		}

		public ConnectionRepository createConnectionRepository(String userId) {
			return connectionRepository;
		}
		
		public void verifyUpdateConnection() {			
			ArgumentMatcher<Connection<?>> matcher = new ArgumentMatcher<Connection<?>>() {
				public boolean matches(Object argument) {
					Connection<?> connection = (Connection<?>) argument;
					return connection.getKey().getProviderId().equals(providerId) && connection.getKey().getProviderUserId().equals("testuser");
				}
			};			
			verify(connectionRepository, times(1)).updateConnection(argThat(matcher));
		}
	}
	
	private static class TestSignInAdapter implements SignInAdapter {
		private final String originalUrl;
		
		public TestSignInAdapter(String originalUrl) {
			this.originalUrl = originalUrl;
		}
		
		public String signIn(String userId, Connection<?> connection, NativeWebRequest request) {
			return originalUrl;
		}
	}

	private static class TestOAuth1ConnectionFactory extends OAuth1ConnectionFactory<Object> {
		public TestOAuth1ConnectionFactory() {
			super("oauth1provider", new TestOAuth1ServiceProvider(), null);
		}
		
		@Override
		public Connection<Object> createConnection(OAuthToken accessToken) {
			return new OAuth1Connection<Object>(getProviderId(), "testuser", accessToken.getValue(), accessToken.getSecret(), (OAuth1ServiceProvider<Object>) getServiceProvider(), getApiAdapter());
		}
	}
	
	private static class TestOAuth2ConnectionFactory extends OAuth2ConnectionFactory<Object> {
		public TestOAuth2ConnectionFactory() {
			super("oauth2provider", new TestOAuth2ServiceProvider(), null);
		}

		public Connection<Object> createConnection(ConnectionData data) {
			return null;
		}
		
		public Connection<Object> createConnection(AccessGrant accessGrant) {
			return new OAuth2Connection<Object>(getProviderId(), "testuser", accessGrant.getAccessToken(), accessGrant.getRefreshToken(), accessGrant.getExpireTime(), 
					(OAuth2ServiceProvider<Object>) getServiceProvider(), getApiAdapter());
		}
	}
	
	private static class TestOAuth1ServiceProvider implements OAuth1ServiceProvider<Object> {
		public Object getApi(String accessToken, String secret) {
			return null;
		}
		
		public OAuth1Operations getOAuthOperations() {
			return new TestOAuth1Operations();
		}
	}
	
	private static class TestOAuth2ServiceProvider implements OAuth2ServiceProvider<Object> {
		public OAuth2Operations getOAuthOperations() {
			return new TestOAuth2Operations();
		}

		public Object getApi(String accessToken) {
			return null;
		}		
	}
	
	private static class TestOAuth1Operations implements OAuth1Operations {
		public OAuth1Version getVersion() {
			return null;
		}

		public OAuthToken fetchRequestToken(String callbackUrl, MultiValueMap<String, String> additionalParameters) {
			return null;
		}

		public String buildAuthorizeUrl(String requestToken, OAuth1Parameters parameters) {
			return null;
		}

		public String buildAuthenticateUrl(String requestToken, OAuth1Parameters parameters) {
			return null;
		}

		public OAuthToken exchangeForAccessToken(AuthorizedRequestToken requestToken, MultiValueMap<String, String> additionalParameters) {
			return new OAuthToken("access_token", "access_token_secret");
		}
	}
	
	private static class TestOAuth2Operations implements OAuth2Operations {

		public String buildAuthorizeUrl(GrantType grantType, OAuth2Parameters parameters) {
			return null;
		}

		public String buildAuthenticateUrl(GrantType grantType, OAuth2Parameters parameters) {
			return null;
		}

		public AccessGrant exchangeForAccess(String authorizationCode, String redirectUri, MultiValueMap<String, String> additionalParameters) {
			return new AccessGrant("access_token");
		}

		public AccessGrant refreshAccess(String refreshToken, String scope, MultiValueMap<String, String> additionalParameters) {
			return null;
		}
		
	}
}
