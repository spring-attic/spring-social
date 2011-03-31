package org.springframework.social.connect.jdbc;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseFactory;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.security.crypto.encrypt.Encryptors;
import org.springframework.social.connect.MapServiceProviderConnectionFactoryRegistry;
import org.springframework.social.connect.OAuth2ServiceProviderConnectionFactory;
import org.springframework.social.connect.spi.ProviderProfile;
import org.springframework.social.connect.spi.ServiceApiAdapter;
import org.springframework.social.oauth2.OAuth2Operations;
import org.springframework.social.oauth2.OAuth2ServiceProvider;

public class JdbcServiceProviderConnectionRepositoryTest {

	private EmbeddedDatabase database;
	
	private JdbcServiceProviderConnectionRepository connectionRepository;
	
	@Before
	public void setUp() {
		EmbeddedDatabaseFactory factory = new EmbeddedDatabaseFactory();
		factory.setDatabaseType(EmbeddedDatabaseType.H2);
		ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
		populator.addScript(new ClassPathResource("JdbcServiceProviderConnectionRepositorySchema.sql", getClass()));
		factory.setDatabasePopulator(populator);
		database = factory.getDatabase();
		MapServiceProviderConnectionFactoryRegistry connectionFactoryRegistry = new MapServiceProviderConnectionFactoryRegistry();
		connectionFactoryRegistry.addConnectionFactory(new FacebookServiceProviderConnectionFactory());
		this.connectionRepository = new JdbcServiceProviderConnectionRepository(database, Encryptors.noOpText(), connectionFactoryRegistry);		
	}

	@After
	public void tearDown() {
		if (database != null) {
			database.shutdown();
		}
	}

	@Test
	public void test() {
		
	}
	
	private static class FacebookServiceProviderConnectionFactory extends OAuth2ServiceProviderConnectionFactory<FacebookApi> {

		public FacebookServiceProviderConnectionFactory() {
			super("facebook", new FacebookServiceProvider(), new FacebookServiceApiAdapter(), true);
		}
		
	}

	private static class FacebookServiceProvider implements OAuth2ServiceProvider<FacebookApi> {

		public OAuth2Operations getOAuthOperations() {
			return null;
		}

		public FacebookApi getServiceApi(final String accessToken) {
			return new FacebookApi() {
				public String getAccessToken() {
					return accessToken;
				}
			};
		}
		
	}
		
	private interface FacebookApi {
		
		String getAccessToken();
		
	}
	
	private static class FacebookServiceApiAdapter implements ServiceApiAdapter<FacebookApi> {

		public boolean test(FacebookApi serviceApi) {
			return true;
		}

		public ProviderProfile getProfile(FacebookApi serviceApi) {
			return new ProviderProfile("1", "Keith Donald", "http://facebook.com/keith.donald", "http://facebook.com/keith.donald/picture");
		}

		public void updateStatus(FacebookApi serviceApi, String message) {
			
		}
		
	}
	
}
