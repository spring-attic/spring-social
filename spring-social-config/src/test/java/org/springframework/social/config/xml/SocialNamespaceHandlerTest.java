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
package org.springframework.social.config.xml;

import static org.junit.Assert.*;

import java.lang.reflect.Field;

import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.social.connect.AbstractServiceProvider;
import org.springframework.social.connect.ServiceProvider;
import org.springframework.social.connect.ServiceProviderParameters;
import org.springframework.social.connect.jdbc.ContextServiceProviderFactory;
import org.springframework.social.connect.jdbc.JdbcServiceProviderFactory;
import org.springframework.social.connect.providers.FacebookServiceProvider;
import org.springframework.social.connect.providers.GitHubServiceProvider;
import org.springframework.social.connect.providers.GowallaServiceProvider;
import org.springframework.social.connect.providers.LinkedInServiceProvider;
import org.springframework.social.connect.providers.TripItServiceProvider;
import org.springframework.social.connect.providers.TwitterServiceProvider;

public class SocialNamespaceHandlerTest {
	@Test
	public void jdbcServiceProviderFactory() throws Exception {
		ApplicationContext applicationContext = new ClassPathXmlApplicationContext(
				"socialNamespaceHandlerTests-jdbcServiceProviderFactory.xml", getClass());
		JdbcServiceProviderFactory providerFactory = applicationContext.getBean("serviceProviderFactory",
				JdbcServiceProviderFactory.class);
		assertNotNull(providerFactory);
	}

	@Test
	public void contextServiceProviderFactory() throws Exception {
		ApplicationContext applicationContext = new ClassPathXmlApplicationContext(
				"socialNamespaceHandlerTests-contextServiceProviderFactory.xml", getClass());
		ContextServiceProviderFactory providerFactory = applicationContext.getBean("serviceProviderFactory",
				ContextServiceProviderFactory.class);
		assertNotNull(providerFactory);
	}

	@Test
	public void genericServiceProvider() throws Exception {
		ApplicationContext applicationContext = new ClassPathXmlApplicationContext(
				"socialNamespaceHandlerTests-serviceProviders.xml", getClass());
		TestServiceProvider provider = applicationContext.getBean("foobar", TestServiceProvider.class);
		assertNotNull(provider);
		ServiceProviderParameters parameters = provider.getParameters();
		assertEquals("foobar", parameters.getName());
		assertEquals("FooBar", parameters.getDisplayName());
		assertEquals("consumer_key", parameters.getApiKey());
		assertEquals("consumer_secret", parameters.getSecret());
		assertEquals((Long) 1234L, parameters.getAppId());
		assertEquals("http://www.foobar.com/oauth/requestToken", parameters.getRequestTokenUrl());
		assertTrue(parameters.getAuthorizeUrl().matches("http://www.foobar.com/oauth/authorize"));
		assertEquals("http://www.foobar.com/oauth/accessToken", parameters.getAccessTokenUrl());
	}

	@Test
	public void twitterServiceProvider() throws Exception {
		ApplicationContext applicationContext = new ClassPathXmlApplicationContext(
				"socialNamespaceHandlerTests-serviceProviders.xml", getClass());
		TwitterServiceProvider provider = applicationContext.getBean("twitter", TwitterServiceProvider.class);
		assertNotNull(provider);
		ServiceProviderParameters parameters = peekAtServiceProviderParameters(provider);
		assertEquals("twitter", parameters.getName());
		assertEquals("Twitter", parameters.getDisplayName());
		assertEquals("twitter_key", parameters.getApiKey());
		assertEquals("twitter_secret", parameters.getSecret());
		assertNull(parameters.getAppId());
		assertEquals("https://twitter.com/oauth/request_token", parameters.getRequestTokenUrl());
		assertTrue(parameters.getAuthorizeUrl().matches(
				"https://twitter.com/oauth/authorize?oauth_token={requestToken}"));
		assertEquals("https://twitter.com/oauth/access_token", parameters.getAccessTokenUrl());
	}

	@Test
	public void facebookServiceProvider() throws Exception {
		ApplicationContext applicationContext = new ClassPathXmlApplicationContext(
				"socialNamespaceHandlerTests-serviceProviders.xml", getClass());
		FacebookServiceProvider provider = applicationContext.getBean("facebook", FacebookServiceProvider.class);
		assertNotNull(provider);
		ServiceProviderParameters parameters = peekAtServiceProviderParameters(provider);
		assertEquals("facebook", parameters.getName());
		assertEquals("Facebook", parameters.getDisplayName());
		assertEquals("facebook_key", parameters.getApiKey());
		assertEquals("facebook_secret", parameters.getSecret());
		assertEquals((Long) 1234L, parameters.getAppId());
		assertNull(parameters.getRequestTokenUrl());
		assertTrue(parameters.getAuthorizeUrl().matches(
						"https://graph.facebook.com/oauth/authorize?client_id={clientId}&redirect_uri={redirectUri}&scope={scope}"));
		assertEquals("https://graph.facebook.com/oauth/access_token", parameters.getAccessTokenUrl());
	}

	@Test
	public void gowallaServiceProvider() throws Exception {
		ApplicationContext applicationContext = new ClassPathXmlApplicationContext(
				"socialNamespaceHandlerTests-serviceProviders.xml", getClass());
		GowallaServiceProvider provider = applicationContext.getBean("gowalla", GowallaServiceProvider.class);
		assertNotNull(provider);
		ServiceProviderParameters parameters = peekAtServiceProviderParameters(provider);
		assertEquals("gowalla", parameters.getName());
		assertEquals("Gowalla", parameters.getDisplayName());
		assertEquals("gowalla_key", parameters.getApiKey());
		assertEquals("gowalla_secret", parameters.getSecret());
		assertNull(parameters.getAppId());
		assertNull(parameters.getRequestTokenUrl());
		assertTrue(parameters.getAuthorizeUrl().matches(
				"https://gowalla.com/api/oauth/new?client_id={clientId}&redirect_uri={redirectUri}&scope={scope}"));
		assertEquals("https://gowalla.com/api/oauth/token", parameters.getAccessTokenUrl());
	}

	@Test
	public void tripitServiceProvider() throws Exception {
		ApplicationContext applicationContext = new ClassPathXmlApplicationContext(
				"socialNamespaceHandlerTests-serviceProviders.xml", getClass());
		TripItServiceProvider provider = applicationContext.getBean("tripit", TripItServiceProvider.class);
		assertNotNull(provider);
		ServiceProviderParameters parameters = peekAtServiceProviderParameters(provider);
		assertEquals("tripit", parameters.getName());
		assertEquals("TripIt", parameters.getDisplayName());
		assertEquals("tripit_key", parameters.getApiKey());
		assertEquals("tripit_secret", parameters.getSecret());
		assertNull(parameters.getAppId());
		assertEquals("https://api.tripit.com/oauth/request_token", parameters.getRequestTokenUrl());
		assertTrue(parameters.getAuthorizeUrl().matches(
				"https://www.tripit.com/oauth/authorize?oauth_token={requestToken}&oauth_callback={redirectUri}"));
		assertEquals("https://api.tripit.com/oauth/access_token", parameters.getAccessTokenUrl());
	}

	@Test
	public void linkedinServiceProvider() throws Exception {
		ApplicationContext applicationContext = new ClassPathXmlApplicationContext(
				"socialNamespaceHandlerTests-serviceProviders.xml", getClass());
		LinkedInServiceProvider provider = applicationContext.getBean("linkedin", LinkedInServiceProvider.class);
		assertNotNull(provider);
		ServiceProviderParameters parameters = peekAtServiceProviderParameters(provider);
		assertEquals("linkedin", parameters.getName());
		assertEquals("LinkedIn", parameters.getDisplayName());
		assertEquals("linkedin_key", parameters.getApiKey());
		assertEquals("linkedin_secret", parameters.getSecret());
		assertNull(parameters.getAppId());
		assertEquals("https://api.linkedin.com/uas/oauth/requestToken", parameters.getRequestTokenUrl());
		assertTrue(parameters.getAuthorizeUrl().matches(
				"https://www.linkedin.com/uas/oauth/authorize?oauth_token={requestToken}"));
		assertEquals("https://api.linkedin.com/uas/oauth/accessToken", parameters.getAccessTokenUrl());
	}

	@Test
	public void githubServiceProvider() throws Exception {
		ApplicationContext applicationContext = new ClassPathXmlApplicationContext(
				"socialNamespaceHandlerTests-serviceProviders.xml", getClass());
		GitHubServiceProvider provider = applicationContext.getBean("github", GitHubServiceProvider.class);
		assertNotNull(provider);
		ServiceProviderParameters parameters = peekAtServiceProviderParameters(provider);
		assertEquals("github", parameters.getName());
		assertEquals("GitHub", parameters.getDisplayName());
		assertEquals("github_key", parameters.getApiKey());
		assertEquals("github_secret", parameters.getSecret());
		assertNull(parameters.getAppId());
		assertNull(parameters.getRequestTokenUrl());
		assertTrue(parameters.getAuthorizeUrl().matches(
						"https://github.com/login/oauth/authorize?client_id={clientId}&redirect_uri={redirectUri}&scope={scope}"));
		assertEquals("https://github.com/login/oauth/access_token", parameters.getAccessTokenUrl());
	}

	private ServiceProviderParameters peekAtServiceProviderParameters(ServiceProvider<?> bean)
			throws NoSuchFieldException, IllegalAccessException {
		Field parametersField = AbstractServiceProvider.class.getDeclaredField("parameters");
		parametersField.setAccessible(true);
		return (ServiceProviderParameters) parametersField.get(bean);
	}

}
