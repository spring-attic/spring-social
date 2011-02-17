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
package org.springframework.social.connect.oauth2;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;
import org.springframework.social.connect.ServiceProviderConnection;
import org.springframework.social.connect.support.ConnectionRepository;
import org.springframework.social.connect.test.StubConnectionRepository;
import org.springframework.social.oauth2.AccessGrant;
import org.springframework.social.oauth2.OAuth2Operations;

public class OAuth2ServiceProviderTest {

	private ConnectionRepository connectionRepository = new StubConnectionRepository();

	private OAuth2ServiceProvider<TestApi> serviceProvider = new TestServiceProvider(connectionRepository);

	@Test
	public void connectFlow() {
		// preconditions
		Long accountId = 1L;
		assertEquals(false, serviceProvider.isConnected(accountId));
		assertEquals(0, serviceProvider.getConnections(accountId).size());
		
		// oauth 2 dance
		OAuth2Operations oauthClient = serviceProvider.getOAuthOperations();
		String authorizeUrl = oauthClient.buildAuthorizeUrl("http://localhost:8080/me", "READ_WRITE");
		assertEquals("http://springsource.org/oauth/authorize?scope=READ_WRITE", authorizeUrl);
		AccessGrant accessGrant = oauthClient.exchangeForAccess("authorizationGrant", "http://localhost:8080/me");

		// connect
		ServiceProviderConnection<TestApi> connection = serviceProvider.connect(accountId, accessGrant);
		TestApi api = connection.getServiceApi();
		assertEquals("Hello Keith!", api.testOperation("Keith"));

		TestApiImpl impl = (TestApiImpl) api;
		assertEquals("12345", impl.getAccessToken());
		
		// additional postconditions
		assertEquals(true, serviceProvider.isConnected(accountId));
		List<ServiceProviderConnection<TestApi>> connections = serviceProvider.getConnections(accountId);
		assertEquals(1, connections.size());
		assertEquals("Hello Keith!", connections.get(0).getServiceApi().testOperation("Keith"));

	}
	
	@Test
	public void equals() {
		Long accountId = 1L;
		AccessGrant accessGrant = new AccessGrant("12345", "23456");
		ServiceProviderConnection<TestApi> connection = serviceProvider.connect(accountId, accessGrant);
		List<ServiceProviderConnection<TestApi>> connections = serviceProvider.getConnections(accountId);		
		ServiceProviderConnection<TestApi> sameConnection = connections.get(0);
		assertEquals(connection, sameConnection);		
	}
	
	@Test
	public void disconnect() {
		Long accountId = 1L;
		AccessGrant accessGrant = new AccessGrant("12345", "23456");
		ServiceProviderConnection<TestApi> connection = serviceProvider.connect(accountId, accessGrant);	
		connection.disconnect();
		assertEquals(false, serviceProvider.isConnected(accountId));
		assertEquals(0, serviceProvider.getConnections(accountId).size());

		try {
			connection.getServiceApi();
			fail("Should be disconnected");
		} catch (IllegalStateException e) {
			
		}
		
		try {
			connection.disconnect();
			fail("Should already be disconnected");
		} catch (IllegalStateException e) {
			
		}
	}
	
	@Test
	public void duplicateConnection() {
		Long accountId = 1L;
		AccessGrant accessGrant = new AccessGrant("12345", "23456");
		serviceProvider.connect(accountId, accessGrant);
		try {
			serviceProvider.connect(accountId, accessGrant);
			fail("Should have failed on duplicate connection");
		} catch (IllegalArgumentException e) {
			
		}
	}
	
	static class TestServiceProvider extends AbstractOAuth2ServiceProvider<TestApi> {

		public TestServiceProvider(ConnectionRepository connectionRepository) {
			super("test", "key", "secret", connectionRepository, new StubOAuth2Operations());
		}

		protected TestApi getApi(String accessToken) {
			return new TestApiImpl(accessToken);
		}

		@Override
		protected String getProviderAccountId(TestApi api) {
			return "providerAccountId";
		}
	}
	
	interface TestApi {
		String testOperation(String arg);
	}
	
	static class TestApiImpl implements TestApi {

		private String accessToken;
		
		public TestApiImpl(String accessToken) {
			this.accessToken = accessToken;
		}
		
		public String getAccessToken() {
			return accessToken;
		}
		
		public String testOperation(String arg) {
			return "Hello " + arg + "!";
		}
		
	}

}
