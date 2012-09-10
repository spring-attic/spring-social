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
import org.springframework.social.facebook.api.Facebook;
import org.springframework.social.facebook.api.impl.FacebookTemplate;
import org.springframework.social.facebook.connect.FacebookConnectionFactory;

/**
 * Implementation of {@link AbstractConnectionFactoryBeanDefinitionParser} that creates a {@link FacebookConnectionFactory}.
 * @author Craig Walls
 */
class FacebookConnectionFactoryBeanDefinitionParser extends AbstractConnectionFactoryBeanDefinitionParser {

	public FacebookConnectionFactoryBeanDefinitionParser() {
		super(FacebookConnectionFactory.class, FacebookApiHelper.class);
	}

	static class FacebookApiHelper implements ApiHelper<Facebook> {

		private final UsersConnectionRepository usersConnectionRepository;

		private final UserIdSource userIdSource;

		private FacebookApiHelper(UsersConnectionRepository usersConnectionRepository, UserIdSource userIdSource) {
			this.usersConnectionRepository = usersConnectionRepository;
			this.userIdSource = userIdSource;		
		}

		public Facebook getApi() {
			if (logger.isDebugEnabled()) {
				logger.debug("Getting API binding instance for Facebook");
			}
			
			Connection<Facebook> connection = usersConnectionRepository.createConnectionRepository(userIdSource.getUserId()).findPrimaryConnection(Facebook.class);
			if (logger.isDebugEnabled() && connection == null) {
				logger.debug("No current connection; Returning default FacebookTemplate instance.");
			}
			return connection != null ? connection.getApi() : new FacebookTemplate();
		}

		private final static Log logger = LogFactory.getLog(FacebookApiHelper.class);

	}
	
}
