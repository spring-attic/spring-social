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
import static org.junit.Assert.*;
import static org.springframework.social.connect.web.test.StubOAuthTemplateBehavior.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.junit.Ignore;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionData;
import org.springframework.social.connect.ConnectionFactory;
import org.springframework.social.connect.ConnectionRepository;
import org.springframework.social.connect.DuplicateConnectionException;
import org.springframework.social.connect.mem.InMemoryUsersConnectionRepository;
import org.springframework.social.connect.support.ConnectionFactoryRegistry;
import org.springframework.social.connect.web.test.StubConnectionRepository;
import org.springframework.social.connect.web.test.StubOAuth1ConnectionFactory;
import org.springframework.social.connect.web.test.StubOAuth2ConnectionFactory;
import org.springframework.social.connect.web.test.StubOAuthTemplateBehavior;
import org.springframework.social.connect.web.test.TestApi1;
import org.springframework.social.connect.web.test.TestApi2;
import org.springframework.social.oauth1.OAuthToken;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.context.request.WebRequest;


public class ConnectControllerTest {
	
	private static final String OAUTH2_AUTHORIZE_URL = "https://someprovider.com/oauth/authorize?client_id=clientId&response_type=code&redirect_uri=http%3A%2F%2Flocalhost%2Fconnect%2Foauth2Provider";

	@Test
	@Ignore("Revisit this and assert/fix expectations")
	public void connect_noSuchProvider() throws Exception {
		ConnectionFactoryRegistry connectionFactoryLocator = new ConnectionFactoryRegistry();
		ConnectionFactory<TestApi2> connectionFactory = new StubOAuth2ConnectionFactory("clientId", "clientSecret");
		connectionFactoryLocator.addConnectionFactory(connectionFactory);
		MockMvc mockMvc = standaloneSetup(new ConnectController(connectionFactoryLocator, null)).build();
		mockMvc.perform(post("/connect/noSuchProvider"));
	}

	@Test
	public void createConnectController_setApplicationUrl() throws Exception {
		ConnectionFactoryRegistry connectionFactoryLocator = new ConnectionFactoryRegistry();
		ConnectionRepository connectionRepository = new InMemoryUsersConnectionRepository(connectionFactoryLocator).createConnectionRepository("userid");
		ConnectController controller = new ConnectController(connectionFactoryLocator, connectionRepository);
		controller.setApplicationUrl("http://baseurl.com/");
	}
	
	@Test
	public void connectionStatus() throws Exception {
		ConnectionFactoryRegistry connectionFactoryLocator = new ConnectionFactoryRegistry();
		ConnectionFactory<TestApi1> connectionFactory1 = new StubOAuth1ConnectionFactory("clientId", "clientSecret", THROW_EXCEPTION);
		connectionFactoryLocator.addConnectionFactory(connectionFactory1);				
		ConnectionFactory<TestApi2> connectionFactory2 = new StubOAuth2ConnectionFactory("clientId", "clientSecret", THROW_EXCEPTION);
		connectionFactoryLocator.addConnectionFactory(connectionFactory2);				
		StubConnectionRepository connectionRepository = new StubConnectionRepository();
		connectionRepository.addConnection(connectionFactory1.createConnection(new ConnectionData("oauth1Provider", "provider1User1", null, null, null, null, null, null, null)));
		MockMvc mockMvc = standaloneSetup(new ConnectController(connectionFactoryLocator, connectionRepository)).build();
		
		mockMvc.perform(get("/connect"))
			.andExpect(view().name("connect/status"))
			.andExpect(model().attribute("providerIds", new HashSet<String>(asList("oauth1Provider", "oauth2Provider"))))
			.andExpect(model().attributeExists("connectionMap"));
		
		mockMvc.perform(get("/connect/oauth1Provider"))
			.andExpect(view().name("connect/oauth1ProviderConnected"))
			.andExpect(model().attributeExists("connections"))
			.andExpect(request().attribute("social.addConnection.duplicate", nullValue()))
			.andExpect(request().attribute("social.provider.error", nullValue()));
		mockMvc.perform(get("/connect/oauth2Provider"))
			.andExpect(view().name("connect/oauth2ProviderConnect"))
			.andExpect(request().attribute("social.addConnection.duplicate", nullValue()))
			.andExpect(request().attribute("social.provider.error", nullValue()));
	}

	@Test
	public void connectionStatus_withErrorsInFlashScope() throws Exception {
		ConnectionFactoryRegistry connectionFactoryLocator = new ConnectionFactoryRegistry();
		ConnectionFactory<TestApi2> connectionFactory2 = new StubOAuth2ConnectionFactory("clientId", "clientSecret", THROW_EXCEPTION);
		connectionFactoryLocator.addConnectionFactory(connectionFactory2);				
		StubConnectionRepository connectionRepository = new StubConnectionRepository();
		MockMvc mockMvc = standaloneSetup(new ConnectController(connectionFactoryLocator, connectionRepository)).build();
		
		// Should convert errors in "flash" scope to model attributes and remove them from "flash"
		mockMvc.perform(get("/connect/oauth2Provider").sessionAttr("social_addConnection_duplicate", new DuplicateConnectionException(null)))
			.andExpect(view().name("connect/oauth2ProviderConnect"))
			.andExpect(request().sessionAttribute("social_addConnection_duplicate", nullValue()))
			.andExpect(request().attribute("social_addConnection_duplicate", true));

		mockMvc.perform(get("/connect/oauth2Provider").sessionAttr("social_provider_error", new HttpClientErrorException(HttpStatus.INTERNAL_SERVER_ERROR)))
			.andExpect(view().name("connect/oauth2ProviderConnect"))
			.andExpect(request().sessionAttribute("social_provider_error", nullValue()))
			.andExpect(request().attribute("social_provider_error", true));
}

	@Test
	public void removeConnections() throws Exception {
		ConnectionFactoryRegistry connectionFactoryLocator = new ConnectionFactoryRegistry();
		ConnectionFactory<TestApi2> connectionFactory = new StubOAuth2ConnectionFactory("clientId", "clientSecret", THROW_EXCEPTION);
		connectionFactoryLocator.addConnectionFactory(connectionFactory);				
		StubConnectionRepository connectionRepository = new StubConnectionRepository();
		connectionRepository.addConnection(connectionFactory.createConnection(new ConnectionData("provider1", "provider1User1", null, null, null, null, null, null, null)));
		connectionRepository.addConnection(connectionFactory.createConnection(new ConnectionData("provider1", "provider1User2", null, null, null, null, null, null, null)));
		connectionRepository.addConnection(connectionFactory.createConnection(new ConnectionData("oauth2Provider", "provider2User1", null, null, null, null, null, null, null)));
		connectionRepository.addConnection(connectionFactory.createConnection(new ConnectionData("oauth2Provider", "provider2User2", null, null, null, null, null, null, null)));
		assertEquals(2, connectionRepository.findConnections("provider1").size());		
		assertEquals(2, connectionRepository.findConnections("oauth2Provider").size());				
		ConnectController connectController = new ConnectController(connectionFactoryLocator, connectionRepository);
		List<DisconnectInterceptor<?>> interceptors = getDisconnectInterceptor();
		connectController.setDisconnectInterceptors(interceptors);
		MockMvc mockMvc = standaloneSetup(connectController).build();
		mockMvc.perform(delete("/connect/oauth2Provider"))
			.andExpect(redirectedUrl("/connect/oauth2Provider"));
		assertEquals(2, connectionRepository.findConnections("provider1").size());		
		assertEquals(0, connectionRepository.findConnections("oauth2Provider").size());		
		assertFalse(((TestConnectInterceptor<?>)(interceptors.get(0))).preDisconnectInvoked);
		assertFalse(((TestConnectInterceptor<?>)(interceptors.get(0))).postDisconnectInvoked);
		assertNull(((TestConnectInterceptor<?>)(interceptors.get(0))).connectionFactory);
		assertTrue(((TestConnectInterceptor<?>)(interceptors.get(1))).preDisconnectInvoked);
		assertTrue(((TestConnectInterceptor<?>)(interceptors.get(1))).postDisconnectInvoked);
		assertSame(connectionFactory, ((TestConnectInterceptor<?>)(interceptors.get(1))).connectionFactory);
	}

	@Test
	public void removeConnection() throws Exception {
		ConnectionFactoryRegistry connectionFactoryLocator = new ConnectionFactoryRegistry();
		ConnectionFactory<TestApi2> connectionFactory = new StubOAuth2ConnectionFactory("clientId", "clientSecret", THROW_EXCEPTION);
		connectionFactoryLocator.addConnectionFactory(connectionFactory);				
		StubConnectionRepository connectionRepository = new StubConnectionRepository();
		connectionRepository.addConnection(connectionFactory.createConnection(new ConnectionData("oauth2Provider", "provider1User1", null, null, null, null, null, null, null)));
		connectionRepository.addConnection(connectionFactory.createConnection(new ConnectionData("oauth2Provider", "provider1User2", null, null, null, null, null, null, null)));
		assertEquals(2, connectionRepository.findConnections("oauth2Provider").size());		
		ConnectController connectController = new ConnectController(connectionFactoryLocator, connectionRepository);
		List<DisconnectInterceptor<?>> interceptors = getDisconnectInterceptor();
		connectController.setDisconnectInterceptors(interceptors);
		MockMvc mockMvc = standaloneSetup(connectController).build();
		mockMvc.perform(delete("/connect/oauth2Provider/provider1User1"))
			.andExpect(redirectedUrl("/connect/oauth2Provider"));
		assertEquals(1, connectionRepository.findConnections("oauth2Provider").size());		
		assertFalse(((TestConnectInterceptor<?>)(interceptors.get(0))).preDisconnectInvoked);
		assertFalse(((TestConnectInterceptor<?>)(interceptors.get(0))).postDisconnectInvoked);
		assertNull(((TestConnectInterceptor<?>)(interceptors.get(0))).connectionFactory);
		assertTrue(((TestConnectInterceptor<?>)(interceptors.get(1))).preDisconnectInvoked);
		assertTrue(((TestConnectInterceptor<?>)(interceptors.get(1))).postDisconnectInvoked);
		assertSame(connectionFactory, ((TestConnectInterceptor<?>)(interceptors.get(1))).connectionFactory);
	}
	
	// OAuth 1

	@Test
	public void connect_OAuth1Provider() throws Exception {
		ConnectionFactoryRegistry connectionFactoryLocator = new ConnectionFactoryRegistry();
		ConnectionFactory<TestApi1> connectionFactory = new StubOAuth1ConnectionFactory("clientId", "clientSecret");
		connectionFactoryLocator.addConnectionFactory(connectionFactory);
		ConnectController connectController = new ConnectController(connectionFactoryLocator, null);
		List<ConnectInterceptor<?>> interceptors = getConnectInterceptor();
		connectController.setConnectInterceptors(interceptors);
		connectController.afterPropertiesSet();
		MockMvc mockMvc = standaloneSetup(connectController).build();
		mockMvc.perform(post("/connect/oauth1Provider"))
			.andExpect(redirectedUrl("https://someprovider.com/oauth/authorize?oauth_token=requestToken"))
			.andExpect(request().sessionAttribute("oauthToken", samePropertyValuesAs(new OAuthToken("requestToken", "requestTokenSecret"))));
		// Check for preConnect() only. The postConnect() won't be invoked until after callback
		TestConnectInterceptor<?> textInterceptor1 = (TestConnectInterceptor<?>)(interceptors.get(0));
		assertTrue(textInterceptor1.preConnectInvoked);
		assertEquals("oauth1Provider", textInterceptor1.connectionFactory.getProviderId());			
		assertFalse(((TestConnectInterceptor<?>)(interceptors.get(1))).preConnectInvoked);
	}

	@Test
	public void connect_OAuth1Provider_exceptionWhileFetchingRequestToken() throws Exception {
		ConnectionFactoryRegistry connectionFactoryLocator = new ConnectionFactoryRegistry();
		ConnectionFactory<TestApi1> connectionFactory = new StubOAuth1ConnectionFactory("clientId", "clientSecret", StubOAuthTemplateBehavior.THROW_EXCEPTION);
		connectionFactoryLocator.addConnectionFactory(connectionFactory);
		MockMvc mockMvc = standaloneSetup(new ConnectController(connectionFactoryLocator, null)).build();
		mockMvc.perform(post("/connect/oauth1Provider"))
			.andExpect(redirectedUrl("/connect/oauth1Provider"))
			.andExpect(request().sessionAttribute("social_provider_error", notNullValue()));
	}
	
	@Test
	public void oauth1Callback() throws Exception {
		ConnectionFactoryRegistry connectionFactoryLocator = new ConnectionFactoryRegistry();
		ConnectionFactory<TestApi1> connectionFactory = new StubOAuth1ConnectionFactory("clientId", "clientSecret");
		connectionFactoryLocator.addConnectionFactory(connectionFactory);
		StubConnectionRepository connectionRepository = new StubConnectionRepository();
		ConnectController connectController = new ConnectController(connectionFactoryLocator, connectionRepository);
		List<ConnectInterceptor<?>> interceptors = getConnectInterceptor();
		connectController.setConnectInterceptors(interceptors);
		connectController.afterPropertiesSet();
		MockMvc mockMvc = standaloneSetup(connectController).build();
		assertEquals(0, connectionRepository.findConnections("oauth2Provider").size());		
		mockMvc.perform(get("/connect/oauth1Provider")
						.sessionAttr("oauthToken", new OAuthToken("requestToken", "requestTokenSecret"))
						.param("oauth_token", "requestToken")
						.param("oauth_verifier", "verifier"))
			.andExpect(redirectedUrl("/connect/oauth1Provider"));
		List<Connection<?>> connections = connectionRepository.findConnections("oauth1Provider");
		assertEquals(1, connections.size());
		assertEquals("oauth1Provider", connections.get(0).getKey().getProviderId());
		// Check for postConnect() only. The preConnect() is only invoked during the initial portion of the flow
		TestConnectInterceptor<?> testInterceptor1 = (TestConnectInterceptor<?>)(interceptors.get(0));
		assertTrue(testInterceptor1.postConnectInvoked);
		assertFalse(((TestConnectInterceptor<?>)(interceptors.get(1))).postConnectInvoked);
	}

	@Test
	public void oauth1Callback_exceptionWhileFetchingAccessToken() throws Exception {
		ConnectionFactoryRegistry connectionFactoryLocator = new ConnectionFactoryRegistry();
		ConnectionFactory<TestApi1> connectionFactory = new StubOAuth1ConnectionFactory("clientId", "clientSecret", THROW_EXCEPTION);
		connectionFactoryLocator.addConnectionFactory(connectionFactory);
		StubConnectionRepository connectionRepository = new StubConnectionRepository();
		MockMvc mockMvc = standaloneSetup(new ConnectController(connectionFactoryLocator, connectionRepository)).build();
		assertEquals(0, connectionRepository.findConnections("oauth2Provider").size());		
		mockMvc.perform(get("/connect/oauth1Provider")
						.sessionAttr("oauthToken", new OAuthToken("requestToken", "requestTokenSecret"))
						.param("oauth_token", "requestToken")
						.param("oauth_verifier", "verifier"))
			.andExpect(redirectedUrl("/connect/oauth1Provider"))
			.andExpect(request().sessionAttribute("social_provider_error", notNullValue()));
		assertEquals(0, connectionRepository.findConnections("oauth2Provider").size());		
	}

	// OAuth 2
	
	@Test
	public void connect_OAuth2Provider() throws Exception {
		ConnectionFactoryRegistry connectionFactoryLocator = new ConnectionFactoryRegistry();
		ConnectionFactory<TestApi2> connectionFactory = new StubOAuth2ConnectionFactory("clientId", "clientSecret");
		connectionFactoryLocator.addConnectionFactory(connectionFactory);
		ConnectController connectController = new ConnectController(connectionFactoryLocator, null);
		List<ConnectInterceptor<?>> interceptors = getConnectInterceptor();
		connectController.setConnectInterceptors(interceptors);
		connectController.afterPropertiesSet();
		MockMvc mockMvc = standaloneSetup(connectController).build();
		mockMvc.perform(post("/connect/oauth2Provider"))
			.andExpect(redirectedUrl(OAUTH2_AUTHORIZE_URL + "&state=STATE"));
		// Check for preConnect() only. The postConnect() won't be invoked until after callback
		assertFalse(((TestConnectInterceptor<?>)(interceptors.get(0))).preConnectInvoked);
		TestConnectInterceptor<?> testInterceptor2 = (TestConnectInterceptor<?>)(interceptors.get(1));
		assertTrue(testInterceptor2.preConnectInvoked);
		assertEquals("oauth2Provider", testInterceptor2.connectionFactory.getProviderId());
	}

	@Test
	public void connect_OAuth2Provider_withScope() throws Exception {
		ConnectionFactoryRegistry connectionFactoryLocator = new ConnectionFactoryRegistry();
		ConnectionFactory<TestApi2> connectionFactory = new StubOAuth2ConnectionFactory("clientId", "clientSecret");
		connectionFactoryLocator.addConnectionFactory(connectionFactory);
		ConnectController connectController = new ConnectController(connectionFactoryLocator, null);
		connectController.afterPropertiesSet();
		MockMvc mockMvc = standaloneSetup(connectController).build();
		mockMvc.perform(post("/connect/oauth2Provider").param("scope", "read,write"))
			.andExpect(redirectedUrl(OAUTH2_AUTHORIZE_URL + "&scope=read%2Cwrite&state=STATE"));
	}
	
	@Test
	public void oauth2Callback() throws Exception {
		ConnectionFactoryRegistry connectionFactoryLocator = new ConnectionFactoryRegistry();
		ConnectionFactory<TestApi2> connectionFactory = new StubOAuth2ConnectionFactory("clientId", "clientSecret");
		connectionFactoryLocator.addConnectionFactory(connectionFactory);
		StubConnectionRepository connectionRepository = new StubConnectionRepository();
		ConnectController connectController = new ConnectController(connectionFactoryLocator, connectionRepository);
		List<ConnectInterceptor<?>> interceptors = getConnectInterceptor();
		connectController.setConnectInterceptors(interceptors);
		connectController.afterPropertiesSet();
		MockMvc mockMvc = standaloneSetup(connectController).build();
		assertEquals(0, connectionRepository.findConnections("oauth2Provider").size());		
		mockMvc.perform(get("/connect/oauth2Provider").param("code", "oauth2Code"))
			.andExpect(redirectedUrl("/connect/oauth2Provider"));
		List<Connection<?>> connections = connectionRepository.findConnections("oauth2Provider");
		assertEquals(1, connections.size());
		assertEquals("oauth2Provider", connections.get(0).getKey().getProviderId());
		// Check for postConnect() only. The preConnect() is only invoked during the initial portion of the flow
		assertFalse(((TestConnectInterceptor<?>)(interceptors.get(0))).postConnectInvoked);
		TestConnectInterceptor<?> testInterceptor2 = (TestConnectInterceptor<?>)(interceptors.get(1));
		assertTrue(testInterceptor2.postConnectInvoked);
	}

	@Test
	public void oauth2Callback_exceptionWhileFetchingAccessToken() throws Exception {
		ConnectionFactoryRegistry connectionFactoryLocator = new ConnectionFactoryRegistry();
		ConnectionFactory<TestApi2> connectionFactory = new StubOAuth2ConnectionFactory("clientId", "clientSecret", THROW_EXCEPTION);
		connectionFactoryLocator.addConnectionFactory(connectionFactory);
		StubConnectionRepository connectionRepository = new StubConnectionRepository();
		MockMvc mockMvc = standaloneSetup(new ConnectController(connectionFactoryLocator, connectionRepository)).build();
		assertEquals(0, connectionRepository.findConnections("oauth2Provider").size());		
		mockMvc.perform(get("/connect/oauth2Provider").param("code", "oauth2Code"))
			.andExpect(redirectedUrl("/connect/oauth2Provider"))
			.andExpect(request().sessionAttribute("social_provider_error", notNullValue()));
		assertEquals(0, connectionRepository.findConnections("oauth2Provider").size());		
	}
	
	@Test
	public void oauth2ErrorCallback() throws Exception {
		ConnectionFactoryRegistry connectionFactoryLocator = new ConnectionFactoryRegistry();
		ConnectionFactory<TestApi2> connectionFactory = new StubOAuth2ConnectionFactory("clientId", "clientSecret", THROW_EXCEPTION);
		connectionFactoryLocator.addConnectionFactory(connectionFactory);
		StubConnectionRepository connectionRepository = new StubConnectionRepository();
		MockMvc mockMvc = standaloneSetup(new ConnectController(connectionFactoryLocator, connectionRepository)).build();
		assertEquals(0, connectionRepository.findConnections("oauth2Provider").size());		
		HashMap<String, String> expectedError = new HashMap<String, String>();
		expectedError.put("error", "access_denied");
		expectedError.put("errorDescription", "The user said no.");
		expectedError.put("errorUri", "http://provider.com/user/said/no");
		mockMvc.perform(get("/connect/oauth2Provider").param("error", "access_denied")
													  .param("error_description", "The user said no.")
													  .param("error_uri", "http://provider.com/user/said/no"))
			.andExpect(redirectedUrl("/connect/oauth2Provider"))
			.andExpect(request().sessionAttribute("social_authorization_error", notNullValue()))
			.andExpect(request().sessionAttribute("social_authorization_error", expectedError));
	}

	@Test
	public void oauth2ErrorCallback_noDescriptionOrUri() throws Exception {
		ConnectionFactoryRegistry connectionFactoryLocator = new ConnectionFactoryRegistry();
		ConnectionFactory<TestApi2> connectionFactory = new StubOAuth2ConnectionFactory("clientId", "clientSecret", THROW_EXCEPTION);
		connectionFactoryLocator.addConnectionFactory(connectionFactory);
		StubConnectionRepository connectionRepository = new StubConnectionRepository();
		MockMvc mockMvc = standaloneSetup(new ConnectController(connectionFactoryLocator, connectionRepository)).build();
		assertEquals(0, connectionRepository.findConnections("oauth2Provider").size());		
		HashMap<String, String> expectedError = new HashMap<String, String>();
		expectedError.put("error", "access_denied");
		mockMvc.perform(get("/connect/oauth2Provider").param("error", "access_denied"))
			.andExpect(redirectedUrl("/connect/oauth2Provider"))
			.andExpect(request().sessionAttribute("social_authorization_error", notNullValue()))
			.andExpect(request().sessionAttribute("social_authorization_error", expectedError));
	}

	private List<ConnectInterceptor<?>> getConnectInterceptor() {
		List<ConnectInterceptor<?>> interceptors = new ArrayList<ConnectInterceptor<?>>();
		interceptors.add(new TestConnectInterceptor<TestApi1>() {});
		interceptors.add(new TestConnectInterceptor<TestApi2>() {});
		return interceptors;
	}

	private List<DisconnectInterceptor<?>> getDisconnectInterceptor() {
		List<DisconnectInterceptor<?>> interceptors = new ArrayList<DisconnectInterceptor<?>>();
		interceptors.add(new TestConnectInterceptor<TestApi1>() {});
		interceptors.add(new TestConnectInterceptor<TestApi2>() {});
		return interceptors;
	}

	
	private static abstract class TestConnectInterceptor<T> implements ConnectInterceptor<T>, DisconnectInterceptor<T> {
		ConnectionFactory<T> connectionFactory = null;
		@SuppressWarnings("unused")
		MultiValueMap<String, String> parameters = null;
		@SuppressWarnings("unused")
		WebRequest preConnectRequest = null;
		@SuppressWarnings("unused")
		WebRequest postConnectRequest = null;
		@SuppressWarnings("unused")
		WebRequest preDisconnectRequest = null;
		@SuppressWarnings("unused")
		WebRequest postDisconnectRequest = null;
		@SuppressWarnings("unused")
		Connection<T> connection = null;
		boolean preConnectInvoked = false;
		boolean postConnectInvoked = false;
		boolean preDisconnectInvoked = false;
		boolean postDisconnectInvoked = false;

		public void preConnect(ConnectionFactory<T> connectionFactory, MultiValueMap<String, String> parameters, WebRequest request) {
			this.connectionFactory = connectionFactory;
			this.parameters = parameters;
			this.preConnectRequest = request;
			this.preConnectInvoked = true;
		}
		
		public void postConnect(Connection<T> connection, WebRequest request) {
			this.connection = connection;
			this.postConnectRequest = request;
			this.postConnectInvoked = true;
		}
		
		public void preDisconnect(ConnectionFactory<T> connectionFactory, WebRequest request) {
			this.connectionFactory = connectionFactory;
			this.preDisconnectRequest = request;
			this.preDisconnectInvoked = true;
		}
		
		public void postDisconnect(ConnectionFactory<T> connectionFactory, WebRequest request) {
			this.connectionFactory = connectionFactory;
			this.postDisconnectRequest = request;
			this.postDisconnectInvoked = true;
		}
	}

}
