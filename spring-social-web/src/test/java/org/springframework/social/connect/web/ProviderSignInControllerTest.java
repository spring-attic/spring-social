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
package org.springframework.social.connect.web;

import static java.util.Arrays.*;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.*;

import java.util.Arrays;

import org.junit.Test;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionData;
import org.springframework.social.connect.ConnectionFactory;
import org.springframework.social.connect.support.ConnectionFactoryRegistry;
import org.springframework.social.connect.web.test.StubOAuth1ConnectionFactory;
import org.springframework.social.connect.web.test.StubOAuth2ConnectionFactory;
import org.springframework.social.connect.web.test.StubOAuthTemplateBehavior;
import org.springframework.social.connect.web.test.StubUsersConnectionRepository;
import org.springframework.social.connect.web.test.TestApi1;
import org.springframework.social.connect.web.test.TestApi2;
import org.springframework.social.oauth1.OAuthToken;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.request.NativeWebRequest;

public class ProviderSignInControllerTest {

	@Test
	public void constructor() {
		ConnectionFactoryRegistry connectionFactoryLocator = new ConnectionFactoryRegistry();
		ConnectionFactory<TestApi1> connectionFactory1 = new StubOAuth1ConnectionFactory("clientId", "clientSecret");
		connectionFactoryLocator.addConnectionFactory(connectionFactory1);
		StubUsersConnectionRepository usersConnectionRepository = new StubUsersConnectionRepository();
		usersConnectionRepository.createConnectionRepository("habuma").addConnection(connectionFactory1.createConnection(
			new ConnectionData("oauth1Provider", "provider1User1", null, null, null, null, null, null, null)));
		ProviderSignInController providerSignInController = new ProviderSignInController(connectionFactoryLocator, usersConnectionRepository, null);
		providerSignInController.setApplicationUrl("my.url");
	}
	// OAuth 1

	@Test
	public void signIn_nonExistentProvider() throws Exception {
		ConnectionFactoryRegistry connectionFactoryLocator = new ConnectionFactoryRegistry();
		ConnectionFactory<TestApi1> connectionFactory1 = new StubOAuth1ConnectionFactory("clientId", "clientSecret");
		connectionFactoryLocator.addConnectionFactory(connectionFactory1);
		StubUsersConnectionRepository usersConnectionRepository = new StubUsersConnectionRepository();
		usersConnectionRepository.createConnectionRepository("habuma").addConnection(connectionFactory1.createConnection(
			new ConnectionData("oauth1Provider", "provider1User1", null, null, null, null, null, null, null)));
		ProviderSignInController providerSignInController = new ProviderSignInController(connectionFactoryLocator, usersConnectionRepository, null);
		providerSignInController.afterPropertiesSet();
		MockMvc mockMvc = standaloneSetup(providerSignInController).build();
		mockMvc.perform(post("/signin/nonExistentOAuth1Provider"))
			.andExpect(redirectedUrl("/signin?error=provider"));
	}

	@Test
	public void signIn_OAuth1Provider() throws Exception {
		ConnectionFactoryRegistry connectionFactoryLocator = new ConnectionFactoryRegistry();
		ConnectionFactory<TestApi1> connectionFactory1 = new StubOAuth1ConnectionFactory("clientId", "clientSecret");
		connectionFactoryLocator.addConnectionFactory(connectionFactory1);
		StubUsersConnectionRepository usersConnectionRepository = new StubUsersConnectionRepository();
		usersConnectionRepository.createConnectionRepository("habuma").addConnection(connectionFactory1.createConnection(
			new ConnectionData("oauth1Provider", "provider1User1", null, null, null, null, null, null, null)));
		ProviderSignInController providerSignInController = new ProviderSignInController(connectionFactoryLocator, usersConnectionRepository, null);
		providerSignInController.afterPropertiesSet();
		MockMvc mockMvc = standaloneSetup(providerSignInController).build();
		mockMvc.perform(post("/signin/oauth1Provider"))
			.andExpect(redirectedUrl("https://someprovider.com/oauth/authorize?oauth_token=requestToken"))
			.andExpect(request().sessionAttribute("oauthToken", samePropertyValuesAs(new OAuthToken("requestToken", "requestTokenSecret"))));
	}

	@Test
	public void signIn_OAuth1Provider_exceptionWhileFetchingRequestToken() throws Exception {
		ConnectionFactoryRegistry connectionFactoryLocator = new ConnectionFactoryRegistry();
		ConnectionFactory<TestApi1> connectionFactory1 = new StubOAuth1ConnectionFactory("clientId", "clientSecret", StubOAuthTemplateBehavior.THROW_EXCEPTION);
		connectionFactoryLocator.addConnectionFactory(connectionFactory1);
		StubUsersConnectionRepository usersConnectionRepository = new StubUsersConnectionRepository();
		usersConnectionRepository.createConnectionRepository("habuma").addConnection(connectionFactory1.createConnection(
			new ConnectionData("oauth1Provider", "provider1User1", null, null, null, null, null, null, null)));
		ProviderSignInController providerSignInController = new ProviderSignInController(connectionFactoryLocator, usersConnectionRepository, null);
		providerSignInController.afterPropertiesSet();
		MockMvc mockMvc = standaloneSetup(providerSignInController).build();
		mockMvc.perform(post("/signin/oauth1Provider"))
			.andExpect(redirectedUrl("/signin?error=provider"));
	}

	@Test
	public void oauth1Callback_noMatchingUser() throws Exception {
		ConnectionFactoryRegistry connectionFactoryLocator = new ConnectionFactoryRegistry();
		ConnectionFactory<TestApi1> connectionFactory1 = new StubOAuth1ConnectionFactory("clientId", "clientSecret");
		connectionFactoryLocator.addConnectionFactory(connectionFactory1);
		StubUsersConnectionRepository usersConnectionRepository = new StubUsersConnectionRepository();
		ProviderSignInController providerSignInController = new ProviderSignInController(connectionFactoryLocator, usersConnectionRepository, null);
		providerSignInController.afterPropertiesSet();
		MockMvc mockMvc = standaloneSetup(providerSignInController).build();
		mockMvc.perform(get("/signin/oauth1Provider").param("verifier", "verifier").param("oauth_token", "requestToken"))
			.andExpect(redirectedUrl("/signup"))
			.andExpect(request().sessionAttribute(ProviderSignInAttempt.class.getName(), notNullValue()));
		// TODO: Assert attempt contents
	}

	@Test
	public void oauth1Callback_noMatchingUser_customSignUpUrl() throws Exception {
		ConnectionFactoryRegistry connectionFactoryLocator = new ConnectionFactoryRegistry();
		ConnectionFactory<TestApi1> connectionFactory1 = new StubOAuth1ConnectionFactory("clientId", "clientSecret");
		connectionFactoryLocator.addConnectionFactory(connectionFactory1);
		StubUsersConnectionRepository usersConnectionRepository = new StubUsersConnectionRepository();
		ProviderSignInController providerSignInController = new ProviderSignInController(connectionFactoryLocator, usersConnectionRepository, null);
		providerSignInController.setSignUpUrl("/register");
		providerSignInController.afterPropertiesSet();
		MockMvc mockMvc = standaloneSetup(providerSignInController).build();
		mockMvc.perform(get("/signin/oauth1Provider").param("verifier", "verifier").param("oauth_token", "requestToken"))
			.andExpect(redirectedUrl("/register"))
			.andExpect(request().sessionAttribute(ProviderSignInAttempt.class.getName(), notNullValue()));
		// TODO: Assert attempt contents
	}

	@Test
	public void oauth1Callback_multipleMatchingUsers() throws Exception {
		ConnectionFactoryRegistry connectionFactoryLocator = new ConnectionFactoryRegistry();
		ConnectionFactory<TestApi1> connectionFactory1 = new StubOAuth1ConnectionFactory("clientId", "clientSecret");
		connectionFactoryLocator.addConnectionFactory(connectionFactory1);
		StubUsersConnectionRepository usersConnectionRepository = new StubUsersConnectionRepository(Arrays.asList("testuser1", "testuser2"));
		ProviderSignInController providerSignInController = new ProviderSignInController(connectionFactoryLocator, usersConnectionRepository, null);
		providerSignInController.afterPropertiesSet();
		MockMvc mockMvc = standaloneSetup(providerSignInController).build();
		mockMvc.perform(get("/signin/oauth1Provider").param("verifier", "verifier").param("oauth_token", "requestToken"))
			.andExpect(redirectedUrl("/signin?error=multiple_users"));
	}

	@Test
	public void oauth1Callback_multipleMatchingUsers_customSignInUrl() throws Exception {
		ConnectionFactoryRegistry connectionFactoryLocator = new ConnectionFactoryRegistry();
		ConnectionFactory<TestApi1> connectionFactory1 = new StubOAuth1ConnectionFactory("clientId", "clientSecret");
		connectionFactoryLocator.addConnectionFactory(connectionFactory1);
		StubUsersConnectionRepository usersConnectionRepository = new StubUsersConnectionRepository(Arrays.asList("testuser1", "testuser2"));
		ProviderSignInController providerSignInController = new ProviderSignInController(connectionFactoryLocator, usersConnectionRepository, null);
		providerSignInController.setSignInUrl("/customsignin?param=1234");
		providerSignInController.afterPropertiesSet();
		MockMvc mockMvc = standaloneSetup(providerSignInController).build();
		mockMvc.perform(get("/signin/oauth1Provider").param("verifier", "verifier").param("oauth_token", "requestToken"))
			.andExpect(redirectedUrl("/customsignin?param=1234&error=multiple_users"));
	}

	@Test
	public void oauth1Callback_errorWhileExchangingForAccessToken() throws Exception {
		ConnectionFactoryRegistry connectionFactoryLocator = new ConnectionFactoryRegistry();
		ConnectionFactory<TestApi1> connectionFactory1 = new StubOAuth1ConnectionFactory("clientId", "clientSecret", StubOAuthTemplateBehavior.THROW_EXCEPTION);
		connectionFactoryLocator.addConnectionFactory(connectionFactory1);
		StubUsersConnectionRepository usersConnectionRepository = new StubUsersConnectionRepository(Arrays.asList("testuser1"));
		MockMvc mockMvc = standaloneSetup(new ProviderSignInController(connectionFactoryLocator, usersConnectionRepository, null)).build();
		mockMvc.perform(get("/signin/oauth1Provider").param("verifier", "verifier").param("oauth_token", "requestToken"))
			.andExpect(redirectedUrl("/signin?error=provider"));
	}

	@Test
	public void oauth1Callback_matchingUser_noOriginalUrl() throws Exception {
		performOAuth1Callback(null, null);
	}

	@Test
	public void oauth1Callback_matchingUser_noOriginalUrl_withPostSignInUrl() throws Exception {
		performOAuth1Callback(null, "/postSignIn");
	}

	@Test
	public void oauth1Callback_matchingUser_withOriginalUrl() throws Exception {
		performOAuth1Callback("/original", null);
	}

	private void performOAuth1Callback(String originalUrl, String postSignInUrl) throws Exception {
		ConnectionFactoryRegistry connectionFactoryLocator = new ConnectionFactoryRegistry();
		ConnectionFactory<TestApi1> connectionFactory1 = new StubOAuth1ConnectionFactory("clientId", "clientSecret");
		connectionFactoryLocator.addConnectionFactory(connectionFactory1);
		StubUsersConnectionRepository usersConnectionRepository = new StubUsersConnectionRepository(asList("habuma"));
		SignInAdapter signInAdapter = new TestSignInAdapter(originalUrl);
		ProviderSignInController controller = new ProviderSignInController(connectionFactoryLocator, usersConnectionRepository, signInAdapter);
		if (postSignInUrl != null) {
			controller.setPostSignInUrl(postSignInUrl);
		}
		controller.afterPropertiesSet();

		String expectedRedirectUrl = calculateExpectedRedirectUrl(originalUrl, postSignInUrl);
		MockMvc mockMvc = standaloneSetup(controller).build();
		mockMvc.perform(get("/signin/oauth1Provider").param("verifier", "verifier").param("oauth_token", "requestToken"))
			.andExpect(redirectedUrl(expectedRedirectUrl));
		// TODO: Verify that the connection is updated (connectionRepository.updateConnection() is called)
	}

	// OAuth 2
	@Test
	public void signIn_OAuth2Provider() throws Exception {
		ConnectionFactoryRegistry connectionFactoryLocator = new ConnectionFactoryRegistry();
		ConnectionFactory<TestApi2> connectionFactory2 = new StubOAuth2ConnectionFactory("clientId", "clientSecret");
		connectionFactoryLocator.addConnectionFactory(connectionFactory2);
		StubUsersConnectionRepository usersConnectionRepository = new StubUsersConnectionRepository();
		usersConnectionRepository.createConnectionRepository("habuma").addConnection(connectionFactory2.createConnection(
			new ConnectionData("oauth2Provider", "provider2User1", null, null, null, null, null, null, null)));
		ProviderSignInController providerSignInController = new ProviderSignInController(connectionFactoryLocator, usersConnectionRepository, null);
		providerSignInController.afterPropertiesSet();
		MockMvc mockMvc = standaloneSetup(providerSignInController).build();
		mockMvc.perform(post("/signin/oauth2Provider"))
			.andExpect(redirectedUrl("https://someprovider.com/oauth/authorize?client_id=clientId&response_type=code&redirect_uri=http%3A%2F%2Flocalhost%2Fsignin%2Foauth2Provider&state=STATE"));
	}

	@Test
	public void oauth2Callback_noMatchingUser() throws Exception {
		ConnectionFactoryRegistry connectionFactoryLocator = new ConnectionFactoryRegistry();
		ConnectionFactory<TestApi2> connectionFactory2 = new StubOAuth2ConnectionFactory("clientId", "clientSecret");
		connectionFactoryLocator.addConnectionFactory(connectionFactory2);
		StubUsersConnectionRepository usersConnectionRepository = new StubUsersConnectionRepository();
		ProviderSignInController providerSignInController = new ProviderSignInController(connectionFactoryLocator, usersConnectionRepository, null);
		providerSignInController.afterPropertiesSet();
		MockMvc mockMvc = standaloneSetup(providerSignInController).build();
		mockMvc.perform(get("/signin/oauth2Provider").param("code", "authcode"))
			.andExpect(redirectedUrl("/signup"))
			.andExpect(request().sessionAttribute(ProviderSignInAttempt.class.getName(), notNullValue()));
		// TODO: Assert attempt contents
	}

	@Test
	public void oauth2Callback_noMatchingUser_customSignUpUrl() throws Exception {
		ConnectionFactoryRegistry connectionFactoryLocator = new ConnectionFactoryRegistry();
		ConnectionFactory<TestApi2> connectionFactory2 = new StubOAuth2ConnectionFactory("clientId", "clientSecret");
		connectionFactoryLocator.addConnectionFactory(connectionFactory2);
		StubUsersConnectionRepository usersConnectionRepository = new StubUsersConnectionRepository();
		ProviderSignInController controller = new ProviderSignInController(connectionFactoryLocator, usersConnectionRepository, null);
		controller.setSignUpUrl("/register");
		controller.afterPropertiesSet();
		MockMvc mockMvc = standaloneSetup(controller).build();
		mockMvc.perform(get("/signin/oauth2Provider").param("code", "authcode"))
			.andExpect(redirectedUrl("/register"))
			.andExpect(request().sessionAttribute(ProviderSignInAttempt.class.getName(), notNullValue()));
		// TODO: Assert attempt contents
	}

	@Test
	public void oauth2ErrorCallback() throws Exception {
		ConnectionFactoryRegistry connectionFactoryLocator = new ConnectionFactoryRegistry();
		ConnectionFactory<TestApi2> connectionFactory2 = new StubOAuth2ConnectionFactory("clientId", "clientSecret");
		connectionFactoryLocator.addConnectionFactory(connectionFactory2);
		StubUsersConnectionRepository usersConnectionRepository = new StubUsersConnectionRepository();
		ProviderSignInController controller = new ProviderSignInController(connectionFactoryLocator, usersConnectionRepository, null);
		MockMvc mockMvc = standaloneSetup(controller).build();
		mockMvc.perform(get("/signin/oauth2Provider").param("error", "access_denied"))
			.andExpect(redirectedUrl("/signin?error=access_denied"));

	}

	@Test
	public void oauth2ErrorCallback_withDescriptionAndUri() throws Exception {
		ConnectionFactoryRegistry connectionFactoryLocator = new ConnectionFactoryRegistry();
		ConnectionFactory<TestApi2> connectionFactory2 = new StubOAuth2ConnectionFactory("clientId", "clientSecret");
		connectionFactoryLocator.addConnectionFactory(connectionFactory2);
		StubUsersConnectionRepository usersConnectionRepository = new StubUsersConnectionRepository();
		ProviderSignInController controller = new ProviderSignInController(connectionFactoryLocator, usersConnectionRepository, null);
		MockMvc mockMvc = standaloneSetup(controller).build();
		mockMvc.perform(get("/signin/oauth2Provider")
			.param("error", "access_denied")
			.param("error_description", "The user said no.")
			.param("error_uri", "http://provider.com/user/said/no"))
			.andExpect(redirectedUrl("/signin?error=access_denied&error_description=The+user+said+no.&error_uri=http%3A%2F%2Fprovider.com%2Fuser%2Fsaid%2Fno"));
	}

	@Test
	public void oauth2Callback_multipleMatchingUsers() throws Exception {
		ConnectionFactoryRegistry connectionFactoryLocator = new ConnectionFactoryRegistry();
		ConnectionFactory<TestApi2> connectionFactory2 = new StubOAuth2ConnectionFactory("clientId", "clientSecret");
		connectionFactoryLocator.addConnectionFactory(connectionFactory2);
		StubUsersConnectionRepository usersConnectionRepository = new StubUsersConnectionRepository(asList("testuser1", "testuser2"));
		ProviderSignInController providerSignInController = new ProviderSignInController(connectionFactoryLocator, usersConnectionRepository, null);
		providerSignInController.afterPropertiesSet();
		MockMvc mockMvc = standaloneSetup(providerSignInController).build();
		mockMvc.perform(get("/signin/oauth2Provider").param("code", "authcode"))
			.andExpect(redirectedUrl("/signin?error=multiple_users"));
	}

	@Test
	public void oauth2Callback_multipleMatchingUsers_customSignInUrl() throws Exception {
		ConnectionFactoryRegistry connectionFactoryLocator = new ConnectionFactoryRegistry();
		ConnectionFactory<TestApi2> connectionFactory2 = new StubOAuth2ConnectionFactory("clientId", "clientSecret");
		connectionFactoryLocator.addConnectionFactory(connectionFactory2);
		StubUsersConnectionRepository usersConnectionRepository = new StubUsersConnectionRepository(asList("testuser1", "testuser2"));
		ProviderSignInController controller = new ProviderSignInController(connectionFactoryLocator, usersConnectionRepository, null);
		controller.afterPropertiesSet();
		controller.setSignInUrl("/customsignin?someparameter=1234");
		MockMvc mockMvc = standaloneSetup(controller).build();
		mockMvc.perform(get("/signin/oauth2Provider").param("code", "authcode"))
			.andExpect(redirectedUrl("/customsignin?someparameter=1234&error=multiple_users"));
	}

	@Test
	public void oauth2Callback_errorWhileExchangingForAccessToken() throws Exception {
		ConnectionFactoryRegistry connectionFactoryLocator = new ConnectionFactoryRegistry();
		ConnectionFactory<TestApi2> connectionFactory2 = new StubOAuth2ConnectionFactory("clientId", "clientSecret", StubOAuthTemplateBehavior.THROW_EXCEPTION);
		connectionFactoryLocator.addConnectionFactory(connectionFactory2);
		StubUsersConnectionRepository usersConnectionRepository = new StubUsersConnectionRepository(asList("testuser1"));
		MockMvc mockMvc = standaloneSetup(new ProviderSignInController(connectionFactoryLocator, usersConnectionRepository, null)).build();
		mockMvc.perform(get("/signin/oauth2Provider").param("code", "authcode"))
			.andExpect(redirectedUrl("/signin?error=provider"));
	}

	@Test
	public void oauth2Callback_matchingUser_noOriginalUrl() throws Exception {
		performOAuth2Callback(null, null);
	}

	@Test
	public void oauth2Callback_matchingUser_noOriginalUrl_withPostSignInUrl() throws Exception {
		performOAuth2Callback(null, "/postSignIn");
	}

	@Test
	public void oauth2Callback_matchingUser_withOriginalUrl() throws Exception {
		performOAuth2Callback("/original", null);
	}

	private void performOAuth2Callback(String originalUrl, String postSignInUrl) throws Exception {
		ConnectionFactoryRegistry connectionFactoryLocator = new ConnectionFactoryRegistry();
		ConnectionFactory<TestApi2> connectionFactory2 = new StubOAuth2ConnectionFactory("clientId", "clientSecret");
		connectionFactoryLocator.addConnectionFactory(connectionFactory2);
		StubUsersConnectionRepository usersConnectionRepository = new StubUsersConnectionRepository(asList("testuser"));
		SignInAdapter signInAdapter = new TestSignInAdapter(originalUrl);
		ProviderSignInController controller = new ProviderSignInController(connectionFactoryLocator, usersConnectionRepository, signInAdapter);
		controller.afterPropertiesSet();
		if (postSignInUrl != null) {
			controller.setPostSignInUrl(postSignInUrl);
		}
		MockMvc mockMvc = standaloneSetup(controller).build();
		mockMvc.perform(get("/signin/oauth2Provider").param("code", "authcode"))
			.andExpect(redirectedUrl(calculateExpectedRedirectUrl(originalUrl, postSignInUrl)));
		// TODO: Verify that the connection is updated (connectionRepository.updateConnection() is called)
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

	private String calculateExpectedRedirectUrl(String originalUrl, String postSignInUrl) {
		String expectedRedirectUrl = "/";
		if (originalUrl == null) {
			if (postSignInUrl != null) {
				expectedRedirectUrl = postSignInUrl;
			}
		} else {
			expectedRedirectUrl = originalUrl;
		}
		return expectedRedirectUrl;
	}

}
