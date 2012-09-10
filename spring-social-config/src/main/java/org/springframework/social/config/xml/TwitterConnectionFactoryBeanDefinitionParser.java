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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.UsersConnectionRepository;
import org.springframework.social.twitter.api.Twitter;
import org.springframework.social.twitter.api.impl.TwitterTemplate;
import org.springframework.social.twitter.connect.TwitterConnectionFactory;

/**
 * Implementation of {@link AbstractConnectionFactoryBeanDefinitionParser} that creates a {@link TwitterConnectionFactory}.
 * @author Craig Walls
 */
class TwitterConnectionFactoryBeanDefinitionParser extends AbstractConnectionFactoryBeanDefinitionParser {

	public TwitterConnectionFactoryBeanDefinitionParser() {
		super(TwitterConnectionFactory.class, TwitterApiHelper.class);
	}

	static class TwitterApiHelper implements ApiHelper<Twitter> {
		
		private final UsersConnectionRepository usersConnectionRepository;

		private final UserIdSource userIdSource;

		private TwitterApiHelper(UsersConnectionRepository usersConnectionRepository, UserIdSource userIdSource) {
			this.usersConnectionRepository = usersConnectionRepository;
			this.userIdSource = userIdSource;		
		}

		public Twitter getApi() {
			if (logger.isDebugEnabled()) {
				logger.debug("Getting API binding instance for Twitter");
			}
					
			Connection<Twitter> connection = usersConnectionRepository.createConnectionRepository(userIdSource.getUserId()).findPrimaryConnection(Twitter.class);
			if (logger.isDebugEnabled() && connection == null) {
				logger.debug("No current connection; Returning default TwitterTemplate instance.");
			}
			return connection != null ? connection.getApi() : new TwitterTemplate();
		}
		
		private final static Log logger = LogFactory.getLog(TwitterApiHelper.class);

	}

}
