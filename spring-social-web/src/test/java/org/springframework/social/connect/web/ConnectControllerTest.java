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

import static java.util.Arrays.*;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static org.springframework.social.connect.web.test.StubOAuthTemplateBehavior.*;
import static org.springframework.test.web.server.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.server.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.server.setup.MockMvcBuilders.*;

import java.util.HashSet;
import java.util.List;

import org.junit.Ignore;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionData;
import org.springframework.social.connect.ConnectionFactory;
import org.springframework.social.connect.DuplicateConnectionException;
import org.springframework.social.connect.support.ConnectionFactoryRegistry;
import org.springframework.social.connect.web.test.StubConnectionRepository;
import org.springframework.social.connect.web.test.StubOAuth1ConnectionFactory;
import org.springframework.social.connect.web.test.StubOAuth2ConnectionFactory;
import org.springframework.social.connect.web.test.StubOAuthTemplateBehavior;
import org.springframework.social.connect.web.test.TestApi1;
import org.springframework.social.connect.web.test.TestApi2;
import org.springframework.social.oauth1.OAuthToken;
import org.springframework.test.web.server.MockMvc;
import org.springframework.web.client.HttpClientErrorException;


public class ConnectControllerTest {
	
	private static final String OAUTH2_AUTHORIZE_URL = "https://someprovider.com/oauth/authorize?client_id=clientId&response_type=code&redirect_uri=http%3A%2F%2Flocalhost%3A80%2Fconnect%2Foauth2Provider";

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
		mockMvc.perform(get("/connect/oauth2Provider").sessionAttr("social.addConnection.duplicate", new DuplicateConnectionException(null)))
			.andExpect(view().name("connect/oauth2ProviderConnect"))
			.andExpect(request().sessionAttribute("social.addConnection.duplicate", nullValue()))
			.andExpect(request().attribute("social.addConnection.duplicate", true));

		mockMvc.perform(get("/connect/oauth2Provider").sessionAttr("social.provider.error", new HttpClientErrorException(HttpStatus.INTERNAL_SERVER_ERROR)))
			.andExpect(view().name("connect/oauth2ProviderConnect"))
			.andExpect(request().sessionAttribute("social.provider.error", nullValue()))
			.andExpect(request().attribute("social.provider.error", true));
}

	@Test
	public void removeConnections() throws Exception {
		ConnectionFactoryRegistry connectionFactoryLocator = new ConnectionFactoryRegistry();
		ConnectionFactory<TestApi2> connectionFactory = new StubOAuth2ConnectionFactory("clientId", "clientSecret", THROW_EXCEPTION);
		connectionFactoryLocator.addConnectionFactory(connectionFactory);				
		StubConnectionRepository connectionRepository = new StubConnectionRepository();
		connectionRepository.addConnection(connectionFactory.createConnection(new ConnectionData("provider1", "provider1User1", null, null, null, null, null, null, null)));
		connectionRepository.addConnection(connectionFactory.createConnection(new ConnectionData("provider1", "provider1User2", null, null, null, null, null, null, null)));
		connectionRepository.addConnection(connectionFactory.createConnection(new ConnectionData("provider2", "provider2User1", null, null, null, null, null, null, null)));
		connectionRepository.addConnection(connectionFactory.createConnection(new ConnectionData("provider2", "provider2User2", null, null, null, null, null, null, null)));
		assertEquals(2, connectionRepository.findConnections("provider1").size());		
		assertEquals(2, connectionRepository.findConnections("provider2").size());		
		MockMvc mockMvc = standaloneSetup(new ConnectController(connectionFactoryLocator, connectionRepository)).build();
		mockMvc.perform(delete("/connect/provider2"))
			.andExpect(redirectedUrl("/connect/provider2"));
		assertEquals(2, connectionRepository.findConnections("provider1").size());		
		assertEquals(0, connectionRepository.findConnections("provider2").size());		
	}

	@Test
	public void removeConnection() throws Exception {
		ConnectionFactoryRegistry connectionFactoryLocator = new ConnectionFactoryRegistry();
		ConnectionFactory<TestApi2> connectionFactory = new StubOAuth2ConnectionFactory("clientId", "clientSecret", THROW_EXCEPTION);
		connectionFactoryLocator.addConnectionFactory(connectionFactory);				
		StubConnectionRepository connectionRepository = new StubConnectionRepository();
		connectionRepository.addConnection(connectionFactory.createConnection(new ConnectionData("provider1", "provider1User1", null, null, null, null, null, null, null)));
		connectionRepository.addConnection(connectionFactory.createConnection(new ConnectionData("provider1", "provider1User2", null, null, null, null, null, null, null)));
		assertEquals(2, connectionRepository.findConnections("provider1").size());		
		MockMvc mockMvc = standaloneSetup(new ConnectController(connectionFactoryLocator, connectionRepository)).build();
		mockMvc.perform(delete("/connect/provider1/provider1User1"))
			.andExpect(redirectedUrl("/connect/provider1"));
		assertEquals(1, connectionRepository.findConnections("provider1").size());		
	}
	
	// OAuth 1

	@Test
	public void connect_OAuth1Provider() throws Exception {
		ConnectionFactoryRegistry connectionFactoryLocator = new ConnectionFactoryRegistry();
		ConnectionFactory<TestApi1> connectionFactory = new StubOAuth1ConnectionFactory("clientId", "clientSecret");
		connectionFactoryLocator.addConnectionFactory(connectionFactory);
		MockMvc mockMvc = standaloneSetup(new ConnectController(connectionFactoryLocator, null)).build();
		mockMvc.perform(post("/connect/oauth1Provider"))
			.andExpect(redirectedUrl("https://someprovider.com/oauth/authorize?oauth_token=requestToken"))
			.andExpect(request().sessionAttribute("oauthToken", samePropertyValuesAs(new OAuthToken("requestToken", "requestTokenSecret"))));
			
	}

	@Test
	public void connect_OAuth1Provider_exceptionWhileFetchingRequestToken() throws Exception {
		ConnectionFactoryRegistry connectionFactoryLocator = new ConnectionFactoryRegistry();
		ConnectionFactory<TestApi1> connectionFactory = new StubOAuth1ConnectionFactory("clientId", "clientSecret", StubOAuthTemplateBehavior.THROW_EXCEPTION);
		connectionFactoryLocator.addConnectionFactory(connectionFactory);
		MockMvc mockMvc = standaloneSetup(new ConnectController(connectionFactoryLocator, null)).build();
		mockMvc.perform(post("/connect/oauth1Provider"))
			.andExpect(redirectedUrl("/connect/oauth1Provider"))
			.andExpect(request().sessionAttribute("social.provider.error", notNullValue()));
	}
	
	@Test
	public void oauth1Callback() throws Exception {
		ConnectionFactoryRegistry connectionFactoryLocator = new ConnectionFactoryRegistry();
		ConnectionFactory<TestApi1> connectionFactory = new StubOAuth1ConnectionFactory("clientId", "clientSecret");
		connectionFactoryLocator.addConnectionFactory(connectionFactory);
		StubConnectionRepository connectionRepository = new StubConnectionRepository();
		MockMvc mockMvc = standaloneSetup(new ConnectController(connectionFactoryLocator, connectionRepository)).build();
		assertEquals(0, connectionRepository.findConnections("oauth2Provider").size());		
		mockMvc.perform(get("/connect/oauth1Provider")
						.sessionAttr("oauthToken", new OAuthToken("requestToken", "requestTokenSecret"))
						.param("oauth_token", "requestToken")
						.param("oauth_verifier", "verifier"))
			.andExpect(redirectedUrl("/connect/oauth1Provider"));
		List<Connection<?>> connections = connectionRepository.findConnections("oauth1Provider");
		assertEquals(1, connections.size());
		assertEquals("oauth1Provider", connections.get(0).getKey().getProviderId());
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
			.andExpect(request().sessionAttribute("social.provider.error", notNullValue()));
		assertEquals(0, connectionRepository.findConnections("oauth2Provider").size());		
	}

	// OAuth 2
	
	@Test
	public void connect_OAuth2Provider() throws Exception {
		ConnectionFactoryRegistry connectionFactoryLocator = new ConnectionFactoryRegistry();
		ConnectionFactory<TestApi2> connectionFactory = new StubOAuth2ConnectionFactory("clientId", "clientSecret");
		connectionFactoryLocator.addConnectionFactory(connectionFactory);
		MockMvc mockMvc = standaloneSetup(new ConnectController(connectionFactoryLocator, null)).build();
		mockMvc.perform(post("/connect/oauth2Provider"))
			.andExpect(redirectedUrl(OAUTH2_AUTHORIZE_URL));
	}

	@Test
	public void connect_OAuth2Provider_withScope() throws Exception {
		ConnectionFactoryRegistry connectionFactoryLocator = new ConnectionFactoryRegistry();
		ConnectionFactory<TestApi2> connectionFactory = new StubOAuth2ConnectionFactory("clientId", "clientSecret");
		connectionFactoryLocator.addConnectionFactory(connectionFactory);
		MockMvc mockMvc = standaloneSetup(new ConnectController(connectionFactoryLocator, null)).build();
		mockMvc.perform(post("/connect/oauth2Provider").param("scope", "read,write"))
			.andExpect(redirectedUrl(OAUTH2_AUTHORIZE_URL + "&scope=read%2Cwrite"));
	}
	
	@Test
	public void oauth2Callback() throws Exception {
		ConnectionFactoryRegistry connectionFactoryLocator = new ConnectionFactoryRegistry();
		ConnectionFactory<TestApi2> connectionFactory = new StubOAuth2ConnectionFactory("clientId", "clientSecret");
		connectionFactoryLocator.addConnectionFactory(connectionFactory);
		StubConnectionRepository connectionRepository = new StubConnectionRepository();
		MockMvc mockMvc = standaloneSetup(new ConnectController(connectionFactoryLocator, connectionRepository)).build();
		assertEquals(0, connectionRepository.findConnections("oauth2Provider").size());		
		mockMvc.perform(get("/connect/oauth2Provider").param("code", "oauth2Code"))
			.andExpect(redirectedUrl("/connect/oauth2Provider"));
		List<Connection<?>> connections = connectionRepository.findConnections("oauth2Provider");
		assertEquals(1, connections.size());
		assertEquals("oauth2Provider", connections.get(0).getKey().getProviderId());
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
			.andExpect(request().sessionAttribute("social.provider.error", notNullValue()));
		assertEquals(0, connectionRepository.findConnections("oauth2Provider").size());		
	}

}
