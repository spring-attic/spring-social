/*
 * Copyright 2013 the original author or authors.
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
package org.springframework.social.config.annotation;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.security.crypto.encrypt.Encryptors;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.social.UserIdSource;
import org.springframework.social.config.FakeConnectionSignUp;
import org.springframework.social.config.SimpleUserIdSource;
import org.springframework.social.connect.ConnectionSignUp;
import org.springframework.stereotype.Component;

@Component
@PropertySource("classpath:/org/springframework/social/config/fake.properties")
@EnableJdbcConnectionRepository(connectionSignUpRef="connectionSignUp")
@EnableFake(appId="${fake.appId}", appSecret="${fake.appSecret}")
public class SocialConfig {
	
	@Bean
	public PropertySourcesPlaceholderConfigurer propertyPlaceholder() {
		return new PropertySourcesPlaceholderConfigurer();
	}
	
	@Bean(destroyMethod="shutdown")
	public DataSource dataSource() {
		return new EmbeddedDatabaseBuilder()
			.setType(EmbeddedDatabaseType.H2)
			.addScript("classpath:org/springframework/social/connect/jdbc/JdbcUsersConnectionRepository.sql")
			.build();
	}
	
	@Bean
	public TextEncryptor textEncryptor() {
		return Encryptors.noOpText();
	}
	
	@Bean
	public UserIdSource userIdSource() {
		return new SimpleUserIdSource();
	}
	
	@Bean ConnectionSignUp connectionSignUp() {
		return new FakeConnectionSignUp();
	}
}
