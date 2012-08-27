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

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.context.ApplicationContext;
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

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class SocialNamespaceTest {
	
	@Inject
	private ApplicationContext context;

	@Test
	public void connectionFactoryLocator() {
		assertTrue(context.containsBean("connectionFactoryLocator"));
		assertTrue(context.getBean("connectionFactoryLocator") instanceof ConnectionFactoryLocator);
		ConnectionFactoryLocator cfl = context.getBean(ConnectionFactoryLocator.class);
		
		// TODO: Assert that key/secret are properly set
		assertNotNull(cfl.getConnectionFactory(Twitter.class));
		assertNotNull(cfl.getConnectionFactory(Facebook.class));
	}

	@Test
	@Ignore
	public void jdbcUsersConnectionRepository() {
		assertTrue(context.containsBean("usersConnectionRepository"));
		ConnectionFactoryLocator cfl = context.getBean(ConnectionFactoryLocator.class);
		UsersConnectionRepository usersConnectionRepository = context.getBean(UsersConnectionRepository.class);
		ConnectionRepository connectionRepository = usersConnectionRepository.createConnectionRepository("bob");
		testConnectionRepository(cfl, connectionRepository);
	}

	@Test
	@Ignore
	public void jdbcConnectionRepository() {
		assertTrue(context.containsBean("connectionRepository"));
		ConnectionFactoryLocator cfl = context.getBean(ConnectionFactoryLocator.class);
		ConnectionRepository connectionRepository = context.getBean(ConnectionRepository.class);
		testConnectionRepository(cfl, connectionRepository);
	}
	
//	@Test
//	public void twitterApiBinding() {
//		assertTrue(context.containsBean("twitter"));
//		assertNotNull(context.getBean("twitter"));
//	}

	private void testConnectionRepository(ConnectionFactoryLocator cfl, ConnectionRepository connectionRepository) {
		assertNull(connectionRepository.findPrimaryConnection(Twitter.class));
		ConnectionFactory<Twitter> twitterCF = cfl.getConnectionFactory(Twitter.class);
		Connection<Twitter> connection = twitterCF.createConnection(new ConnectionData("twitter", "bob", "Bob McBob", "http://www.twitter.com/mcbob", null, "someToken", "someSecret", null, null));
		connectionRepository.addConnection(connection);
		assertNotNull(connectionRepository.findPrimaryConnection(Twitter.class));
		connectionRepository.removeConnections("twitter");
		assertNull(connectionRepository.findPrimaryConnection(Twitter.class));
	}

}
