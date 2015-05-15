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
package org.springframework.social.config.annotation;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.core.env.Environment;
import org.springframework.social.UserIdSource;
import org.springframework.social.config.Fake;
import org.springframework.social.config.FakeConnectionFactory;
import org.springframework.social.config.SimpleUserIdSource;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionRepository;

@Configuration
@EnableSocial
@PropertySource("classpath:/org/springframework/social/config/annotation/app.properties")
public class MyAwesomeSocialConfig extends SocialConfigurerAdapter {
	public void addConnectionFactories(ConnectionFactoryConfigurer config, Environment env) {
		String fakeAppId = env.getProperty("fake.appId");
		String fakeAppSecret = env.getProperty("fake.appSecret");
		config.addConnectionFactory(new FakeConnectionFactory(fakeAppId, fakeAppSecret));
	}

	public UserIdSource getUserIdSource() {
		return new SimpleUserIdSource(); 
	}

	@Bean
	@Scope(value="request", proxyMode=ScopedProxyMode.INTERFACES)
	public Fake fake(ConnectionRepository repository) {
		Connection<Fake> connection = repository.findPrimaryConnection(Fake.class);
		return connection != null ? connection.getApi() : null;
	}
}
