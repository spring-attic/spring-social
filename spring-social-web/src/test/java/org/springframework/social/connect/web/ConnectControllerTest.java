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

import static org.hamcrest.beans.SamePropertyValuesAs.*;
import static org.springframework.test.web.server.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.server.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.server.setup.MockMvcBuilders.*;

import org.junit.Ignore;
import org.junit.Test;
import org.springframework.social.connect.ConnectionFactory;
import org.springframework.social.connect.support.ConnectionFactoryRegistry;
import org.springframework.social.connect.web.test.TestApi;
import org.springframework.social.connect.web.test.StubOAuth1ConnectionFactory;
import org.springframework.social.connect.web.test.StubOAuth2ConnectionFactory;
import org.springframework.social.oauth1.OAuthToken;
import org.springframework.test.web.server.MockMvc;


public class ConnectControllerTest {
	
	private static final String OAUTH2_AUTHORIZE_URL = "https://someprovider.com/oauth/authorize?client_id=clientId&response_type=code&redirect_uri=http%3A%2F%2Flocalhost%3A80%2Fconnect%2Foauth2Provider";

	@Test
	@Ignore("Revisit this and assert/fix expectations")
	public void connect_noSuchProvider() throws Exception {
		ConnectionFactoryRegistry connectionFactoryLocator = new ConnectionFactoryRegistry();
		ConnectionFactory<TestApi> connectionFactory = new StubOAuth2ConnectionFactory("clientId", "clientSecret");
		connectionFactoryLocator.addConnectionFactory(connectionFactory);
		MockMvc mockMvc = standaloneSetup(new ConnectController(connectionFactoryLocator, null)).build();
		mockMvc.perform(post("/connect/noSuchProvider"));
	}

	@Test
	public void connect_OAuth2Provider() throws Exception {
		ConnectionFactoryRegistry connectionFactoryLocator = new ConnectionFactoryRegistry();
		ConnectionFactory<TestApi> connectionFactory = new StubOAuth2ConnectionFactory("clientId", "clientSecret");
		connectionFactoryLocator.addConnectionFactory(connectionFactory);
		MockMvc mockMvc = standaloneSetup(new ConnectController(connectionFactoryLocator, null)).build();
		mockMvc.perform(post("/connect/oauth2Provider"))
			.andExpect(redirectedUrl(OAUTH2_AUTHORIZE_URL));
	}

	@Test
	public void connect_OAuth2Provider_withScope() throws Exception {
		ConnectionFactoryRegistry connectionFactoryLocator = new ConnectionFactoryRegistry();
		ConnectionFactory<TestApi> connectionFactory = new StubOAuth2ConnectionFactory("clientId", "clientSecret");
		connectionFactoryLocator.addConnectionFactory(connectionFactory);
		MockMvc mockMvc = standaloneSetup(new ConnectController(connectionFactoryLocator, null)).build();
		mockMvc.perform(post("/connect/oauth2Provider").param("scope", "read,write"))
			.andExpect(redirectedUrl(OAUTH2_AUTHORIZE_URL + "&scope=read%2Cwrite"));
	}

	@Test
	public void connect_OAuth1Provider() throws Exception {
		ConnectionFactoryRegistry connectionFactoryLocator = new ConnectionFactoryRegistry();
		ConnectionFactory<TestApi> connectionFactory = new StubOAuth1ConnectionFactory("clientId", "clientSecret");
		connectionFactoryLocator.addConnectionFactory(connectionFactory);
		MockMvc mockMvc = standaloneSetup(new ConnectController(connectionFactoryLocator, null)).build();
		mockMvc.perform(post("/connect/oauth1Provider"))
			.andExpect(redirectedUrl("https://someprovider.com/oauth/authorize?oauth_token=requestToken"))
			.andExpect(request().sessionAttribute("oauthToken", samePropertyValuesAs(new OAuthToken("requestToken", "requestTokenSecret"))));
	}
}
