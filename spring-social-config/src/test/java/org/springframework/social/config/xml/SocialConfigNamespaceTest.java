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
package org.springframework.social.config.xml;

import static org.junit.Assert.*;

import java.util.List;

import javax.inject.Inject;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.social.config.DummyConnection;
import org.springframework.social.config.Fake;
import org.springframework.social.config.FakeConnectionFactory;
import org.springframework.social.config.FakeTemplate;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionData;
import org.springframework.social.connect.ConnectionFactory;
import org.springframework.social.connect.ConnectionFactoryLocator;
import org.springframework.social.connect.ConnectionRepository;
import org.springframework.social.connect.UsersConnectionRepository;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.RequestScope;
import org.springframework.web.context.request.ServletRequestAttributes;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
@Ignore
public class SocialConfigNamespaceTest {

	@Inject
	private ApplicationContext context;

	@Inject
	private Fake fake;

	@Before
	public void setup() {
		setupRequestScope();
	}
	
	@Test
	public void connectionFactoryLocator() throws Exception {
		assertTrue(context.containsBean("connectionFactoryLocator"));
		assertTrue(context.getBean("connectionFactoryLocator") instanceof ConnectionFactoryLocator);
		ConnectionFactoryLocator cfl = context.getBean(ConnectionFactoryLocator.class);		
		FakeConnectionFactory fakeConnectionFactory = (FakeConnectionFactory) cfl.getConnectionFactory(Fake.class);
		assertNotNull(fakeConnectionFactory);
		assertEquals("fakeAppId", fakeConnectionFactory.getAppId());		
		assertEquals("fakeAppSecret", fakeConnectionFactory.getAppSecret());
	}

	@Test
	public void userIdString() {
		String userId = context.getBean("__userIdString", String.class);
		assertEquals("habuma", userId);
	}
	
	@Test
	public void jdbcConnectionRepository() {
		assertNotNull(context.getBean("usersConnectionRepository", UsersConnectionRepository.class));
		assertNotNull(context.getBean("connectionRepository", ConnectionRepository.class));
	}

	@Test
	public void jdbcConnectionRepository_connectionSignUp() {
		UsersConnectionRepository repository = context.getBean("usersConnectionRepository", UsersConnectionRepository.class);
		Connection<Fake> connection = new DummyConnection<Fake>("fake", "fakeuser", new FakeTemplate());
		List<String> users = repository.findUserIdsWithConnection(connection);
		assertEquals(1, users.size());
	}

	@Test
	public void jdbcConnectionRepository_addAndRemoveAConnection() {
		ConnectionFactoryLocator cfl = context.getBean(ConnectionFactoryLocator.class);
		ConnectionRepository connectionRepository = context.getBean(ConnectionRepository.class);
		testConnectionRepository(cfl, connectionRepository);
	}
	
	private void testConnectionRepository(ConnectionFactoryLocator cfl, ConnectionRepository connectionRepository) {
		assertNull(connectionRepository.findPrimaryConnection(Fake.class));
		ConnectionFactory<Fake> fakeCF = cfl.getConnectionFactory(Fake.class);
		Connection<Fake> connection = fakeCF.createConnection(new ConnectionData("fake", "bob", "Bob McBob", "http://www.twitter.com/mcbob", null, "someToken", "someSecret", null, null));
		connectionRepository.addConnection(connection);
		assertNotNull(connectionRepository.findPrimaryConnection(Fake.class));
		assertTrue(context.getBean(Fake.class).isAuthorized());
		assertNotNull(fake);
		assertTrue(fake.isAuthorized());
	}

	private void setupRequestScope() {
		GenericApplicationContext genericContext = (GenericApplicationContext) context;
		genericContext.getBeanFactory().registerScope("request", new RequestScope());
		RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(new MockHttpServletRequest()));
	}	

}
