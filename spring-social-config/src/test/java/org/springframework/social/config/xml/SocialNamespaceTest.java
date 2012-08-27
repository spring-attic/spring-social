/*
 * Copyright 2012 the original author or authors.
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
package org.springframework.social.config.xml;

import static org.junit.Assert.*;

import javax.inject.Inject;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionData;
import org.springframework.social.connect.ConnectionFactory;
import org.springframework.social.connect.ConnectionFactoryLocator;
import org.springframework.social.connect.ConnectionRepository;
import org.springframework.social.connect.UsersConnectionRepository;
import org.springframework.social.facebook.api.Facebook;
import org.springframework.social.twitter.api.Twitter;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.RequestScope;
import org.springframework.web.context.request.ServletRequestAttributes;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class SocialNamespaceTest {
	
	@Inject
	private ApplicationContext context;

	@Test
	public void connectionFactoryLocator() throws Exception {
		assertTrue(context.containsBean("connectionFactoryLocator"));
		assertTrue(context.getBean("connectionFactoryLocator") instanceof ConnectionFactoryLocator);
		ConnectionFactoryLocator cfl = context.getBean(ConnectionFactoryLocator.class);
		
		// TODO: Assert that key/secret are properly set
		assertNotNull(cfl.getConnectionFactory(Twitter.class));
		assertNotNull(cfl.getConnectionFactory(Facebook.class));
	}

	@Test
	public void userIdString() {
		setupRequestScope();
		String userId = context.getBean("_userIdString", String.class);
		assertEquals("habuma", userId);
	}
	
	@Test
	public void jdbcConnectionRepository() {
		setupRequestScope();
		assertNotNull(context.getBean("usersConnectionRepository", UsersConnectionRepository.class));		
		assertNotNull(context.getBean("connectionRepository", ConnectionRepository.class));		
	}
	
	@Test
	public void jdbcConnectionRepository_addAndRemoveAConnection() {
		setupRequestScope();
		ConnectionFactoryLocator cfl = context.getBean(ConnectionFactoryLocator.class);
		ConnectionRepository connectionRepository = context.getBean(ConnectionRepository.class);
		testConnectionRepository(cfl, connectionRepository);
	}
	
	private void testConnectionRepository(ConnectionFactoryLocator cfl, ConnectionRepository connectionRepository) {
		assertNull(connectionRepository.findPrimaryConnection(Twitter.class));
		ConnectionFactory<Twitter> twitterCF = cfl.getConnectionFactory(Twitter.class);
		Connection<Twitter> connection = twitterCF.createConnection(new ConnectionData("twitter", "bob", "Bob McBob", "http://www.twitter.com/mcbob", null, "someToken", "someSecret", null, null));
		connectionRepository.addConnection(connection);
		assertNotNull(connectionRepository.findPrimaryConnection(Twitter.class));
		connectionRepository.removeConnections("twitter");
		assertNull(connectionRepository.findPrimaryConnection(Twitter.class));
	}

	private void setupRequestScope() {
		GenericApplicationContext genericContext = (GenericApplicationContext) context;
		genericContext.getBeanFactory().registerScope("request", new RequestScope());
		RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(new MockHttpServletRequest()));
	}	

}
