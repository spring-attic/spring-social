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

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionRepository;
import org.springframework.social.twitter.api.Twitter;
import org.springframework.social.twitter.api.impl.TwitterTemplate;
import org.springframework.social.twitter.connect.TwitterConnectionFactory;

/**
 * Implementation of {@link AbstractConnectionFactoryBeanDefinitionParser} that creates a {@link TwitterConnectionFactory}.
 * @author Craig Walls
 */
class TwitterConnectionFactoryBeanDefinitionParser extends AbstractConnectionFactoryBeanDefinitionParser {

	public TwitterConnectionFactoryBeanDefinitionParser() {
		super(TwitterConnectionFactory.class, TwitterApiConfig.class);
	}

	private static final class TwitterApiConfig {		
		@Bean
		@Scope(value="request", proxyMode=ScopedProxyMode.INTERFACES)
		public Twitter twitter(ConnectionRepository connectionRepository) {
			Connection<Twitter> twitter = connectionRepository.findPrimaryConnection(Twitter.class);
			return twitter != null ? twitter.getApi() : new TwitterTemplate();
		}		
	}

}
