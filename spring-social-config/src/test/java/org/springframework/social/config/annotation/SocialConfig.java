package org.springframework.social.config.annotation;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseFactory;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.security.crypto.encrypt.Encryptors;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.social.config.xml.SimpleUserIdSource;
import org.springframework.social.config.xml.UserIdSource;
import org.springframework.social.connect.ConnectionFactoryLocator;
import org.springframework.social.connect.support.ConnectionFactoryRegistry;
import org.springframework.stereotype.Component;

@Component
@EnableJdbcConnectionRepository
public class SocialConfig {
	
	@Bean
	public ConnectionFactoryLocator connectionFactoryLocator() {
		return new ConnectionFactoryRegistry();
	}
	
	@Bean(destroyMethod="shutdown")
	public DataSource dataSource() {
		EmbeddedDatabaseFactory factory = new EmbeddedDatabaseFactory();
		factory.setDatabaseName("social");
		factory.setDatabaseType(EmbeddedDatabaseType.H2);
		return factory.getDatabase();		
	}
	
	@Bean
	public TextEncryptor textEncryptor() {
		return Encryptors.noOpText();
	}
	
	@Bean
	public UserIdSource userIdSource() {
		return new SimpleUserIdSource();
	}
	
}
