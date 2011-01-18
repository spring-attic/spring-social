package org.springframework.social.provider.jdbc;

import static org.junit.Assert.*;

import java.util.Collections;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.security.encrypt.SearchableStringEncryptor;
import org.springframework.security.encrypt.StringEncryptor;
import org.springframework.social.provider.ServiceProvider;
import org.springframework.social.provider.ServiceProviderFactory;
import org.springframework.social.test.utils.SpringSocialTestDatabaseBuilder;


public class JdbcServiceProviderFactoryTest {

	private EmbeddedDatabase db;

	private JdbcTemplate jdbcTemplate;

	private ServiceProviderFactory providerFactory;

	@Before
	public void setup() {
		db = new SpringSocialTestDatabaseBuilder().connectedAccount().testData(getClass()).getDatabase();
		jdbcTemplate = new JdbcTemplate(db);
		StringEncryptor encryptor = new SearchableStringEncryptor("secret", "5b8bd7612cdab5ed");
		providerFactory = new JdbcServiceProviderFactory(jdbcTemplate, encryptor);
	}

	@After
	public void destroy() {
		if (db != null) {
			db.shutdown();
		}
	}

	@Test
	public void getAccountProvider() {
		ServiceProvider<TestOperations> twitterProvider = (TestServiceProvider) providerFactory
				.getServiceProvider("twitter");
		assertEquals("twitter", twitterProvider.getName());
		assertEquals("Twitter", twitterProvider.getDisplayName());
		assertEquals("123456789", twitterProvider.getApiKey());
		assertEquals("http://www.twitter.com/authorize?oauth_token=123456789",
				twitterProvider.buildAuthorizeUrl(Collections.singletonMap("requestToken", "123456789")));

		ServiceProvider<TestOperations> facebookProvider = (TestServiceProvider) providerFactory
				.getServiceProvider("facebook");
		assertEquals("facebook", facebookProvider.getName());
		assertEquals("Facebook", facebookProvider.getDisplayName());
		assertEquals("345678901", facebookProvider.getApiKey());
	}

	@Test
	public void getAccountProviderByName() {
		ServiceProvider<TestOperations> twitterProvider = providerFactory.getServiceProvider("twitter",
				TestOperations.class);
		assertEquals("twitter", twitterProvider.getName());
		assertEquals("Twitter", twitterProvider.getDisplayName());
		assertEquals("123456789", twitterProvider.getApiKey());
		assertEquals("http://www.twitter.com/authorize?oauth_token=123456789",
				twitterProvider.buildAuthorizeUrl(Collections.singletonMap("requestToken", "123456789")));

		ServiceProvider<TestOperations> facebookProvider = providerFactory.getServiceProvider("facebook",
				TestOperations.class);
		assertEquals("facebook", facebookProvider.getName());
		assertEquals("Facebook", facebookProvider.getDisplayName());
		assertEquals("345678901", facebookProvider.getApiKey());
	}
}
