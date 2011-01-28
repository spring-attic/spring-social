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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.lang.reflect.Field;

import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.social.connect.oauth1.AbstractOAuth1ServiceProvider;
import org.springframework.social.connect.oauth2.AbstractOAuth2ServiceProvider;
import org.springframework.social.connect.support.AbstractServiceProvider;
import org.springframework.social.facebook.provider.FacebookServiceProvider;
import org.springframework.social.github.provider.GitHubServiceProvider;
import org.springframework.social.gowalla.provider.GowallaServiceProvider;
import org.springframework.social.linkedin.provider.LinkedInServiceProvider;
import org.springframework.social.oauth2.OAuth2Operations;
import org.springframework.social.oauth2.OAuth2Template;
import org.springframework.social.tripit.provider.TripItServiceProvider;
import org.springframework.social.twitter.provider.TwitterServiceProvider;

public class SocialNamespaceHandlerTest {

	@Test
	public void genericServiceProvider() throws Exception {
		ApplicationContext applicationContext = new ClassPathXmlApplicationContext(
				"socialNamespaceHandlerTests-serviceProviders.xml", getClass());
		TwitterServiceProvider provider = applicationContext.getBean("tweeter", TwitterServiceProvider.class);
		assertNotNull(provider);
		assertOAuth1ProviderConfiguration(provider, "twitter");
	}

	@Test
	public void twitterServiceProvider() throws Exception {
		ApplicationContext applicationContext = new ClassPathXmlApplicationContext(
				"socialNamespaceHandlerTests-serviceProviders.xml", getClass());
		TwitterServiceProvider provider = applicationContext.getBean("twitter", TwitterServiceProvider.class);
		assertOAuth1ProviderConfiguration(provider, "twitter");
	}

	@Test
	public void tripitServiceProvider() throws Exception {
		ApplicationContext applicationContext = new ClassPathXmlApplicationContext(
				"socialNamespaceHandlerTests-serviceProviders.xml", getClass());
		TripItServiceProvider provider = applicationContext.getBean("tripit", TripItServiceProvider.class);
		assertOAuth1ProviderConfiguration(provider, "tripit");
	}

	@Test
	public void linkedinServiceProvider() throws Exception {
		ApplicationContext applicationContext = new ClassPathXmlApplicationContext(
				"socialNamespaceHandlerTests-serviceProviders.xml", getClass());
		LinkedInServiceProvider provider = applicationContext.getBean("linkedin", LinkedInServiceProvider.class);
		assertOAuth1ProviderConfiguration(provider, "linkedin");
	}

	@Test
	public void facebookServiceProvider() throws Exception {
		ApplicationContext applicationContext = new ClassPathXmlApplicationContext(
				"socialNamespaceHandlerTests-serviceProviders.xml", getClass());
		FacebookServiceProvider provider = applicationContext.getBean("facebook", FacebookServiceProvider.class);
		assertOAuth2ProviderConfiguration(provider, "facebook");
	}

	@Test
	public void gowallaServiceProvider() throws Exception {
		ApplicationContext applicationContext = new ClassPathXmlApplicationContext(
				"socialNamespaceHandlerTests-serviceProviders.xml", getClass());
		GowallaServiceProvider provider = applicationContext.getBean("gowalla", GowallaServiceProvider.class);
		assertOAuth2ProviderConfiguration(provider, "gowalla");
	}

	@Test
	public void githubServiceProvider() throws Exception {
		ApplicationContext applicationContext = new ClassPathXmlApplicationContext(
				"socialNamespaceHandlerTests-serviceProviders.xml", getClass());
		GitHubServiceProvider provider = applicationContext.getBean("github", GitHubServiceProvider.class);
		assertOAuth2ProviderConfiguration(provider, "github");
	}

	private void assertOAuth1ProviderConfiguration(AbstractOAuth1ServiceProvider<?> provider, String name)
			throws Exception {
		assertNotNull(provider);
		assertEquals(name, peekAtServiceProviderProperty(provider, "id"));
		assertEquals(name + "_key", peekAtOAuth1ServiceProviderProperty(provider, "consumerKey"));
		assertEquals(name + "_secret", peekAtOAuth1ServiceProviderProperty(provider, "consumerSecret"));
	}

	private void assertOAuth2ProviderConfiguration(AbstractOAuth2ServiceProvider<?> provider, String name)
			throws Exception {
		assertNotNull(provider);
		assertEquals(name, peekAtServiceProviderProperty(provider, "id"));
		OAuth2Operations oauth2Operations = provider.getOAuth2Operations();
		assertEquals(name + "_key", peekAtOAuth2TemplateProperty(oauth2Operations, "clientId"));
		assertEquals(name + "_secret", peekAtOAuth2TemplateProperty(oauth2Operations, "clientSecret"));
	}

	private Object peekAtServiceProviderProperty(AbstractServiceProvider<?> provider, String propertyName)
			throws NoSuchFieldException, IllegalAccessException {
		Field parametersField = AbstractServiceProvider.class.getDeclaredField(propertyName);
		parametersField.setAccessible(true);
		return parametersField.get(provider);
	}

	private Object peekAtOAuth1ServiceProviderProperty(AbstractOAuth1ServiceProvider<?> provider, String propertyName)
			throws NoSuchFieldException, IllegalAccessException {
		Field parametersField = AbstractOAuth1ServiceProvider.class.getDeclaredField(propertyName);
		parametersField.setAccessible(true);
		return parametersField.get(provider);
	}

	private Object peekAtOAuth2TemplateProperty(OAuth2Operations oauth2Operations, String propertyName)
			throws NoSuchFieldException, IllegalAccessException {
		Field parametersField = OAuth2Template.class.getDeclaredField(propertyName);
		parametersField.setAccessible(true);
		return parametersField.get(oauth2Operations);
	}

}
