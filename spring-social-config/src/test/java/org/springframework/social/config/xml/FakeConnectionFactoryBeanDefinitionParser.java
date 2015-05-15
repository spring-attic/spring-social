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
package org.springframework.social.config.xml;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.social.UserIdSource;
import org.springframework.social.config.Fake;
import org.springframework.social.config.FakeConnectionFactory;
import org.springframework.social.config.FakeSocialAuthenticationService;
import org.springframework.social.config.FakeTemplate;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.UsersConnectionRepository;
import org.springframework.social.security.provider.SocialAuthenticationService;

/**
 * Implementation of {@link AbstractProviderConfigBeanDefinitionParser} that creates a FakeConnectionFactory.
 * @author Craig Walls
 */
class FakeConnectionFactoryBeanDefinitionParser extends AbstractProviderConfigBeanDefinitionParser {

	public FakeConnectionFactoryBeanDefinitionParser() {
		super(FakeConnectionFactory.class, FakeApiHelper.class);
	}

	static class FakeApiHelper implements ApiHelper<Fake> {
		
		private final UsersConnectionRepository usersConnectionRepository;

		private final UserIdSource userIdSource;

		private FakeApiHelper(UsersConnectionRepository usersConnectionRepository, UserIdSource userIdSource) {
			this.usersConnectionRepository = usersConnectionRepository;
			this.userIdSource = userIdSource;		
		}

		public Fake getApi() {
			if (logger.isDebugEnabled()) {
				logger.debug("Getting API binding instance for Fake provider");
			}
					
			Connection<Fake> connection = usersConnectionRepository.createConnectionRepository(userIdSource.getUserId()).findPrimaryConnection(Fake.class);
			if (logger.isDebugEnabled() && connection == null) {
				logger.debug("No current connection; Returning default FakeTemplate instance.");
			}
			return connection != null ? connection.getApi() : new FakeTemplate();
		}
		
		private final static Log logger = LogFactory.getLog(FakeApiHelper.class);

	}

	@Override
	protected Class<? extends SocialAuthenticationService<?>> getAuthenticationServiceClass() {
		return FakeSocialAuthenticationService.class;
	}

}
