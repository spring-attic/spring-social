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

import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

public class SocialNamespaceHandler extends NamespaceHandlerSupport {

	public void init() {
		registerBeanDefinitionParser("service-provider", new ServiceProviderElementParser());
		registerBeanDefinitionParser("facebook-provider", new FacebookProviderElementParser());
		registerBeanDefinitionParser("gowalla-provider", new GowallaProviderElementParser());
		registerBeanDefinitionParser("linkedin-provider", new LinkedInProviderElementParser());
		registerBeanDefinitionParser("tripit-provider", new TripItProviderElementParser());
		registerBeanDefinitionParser("twitter-provider", new TwitterProviderElementParser());

		registerBeanDefinitionParser("context-service-provider-factory",
				new ContextServiceProviderFactoryElementParser());
		registerBeanDefinitionParser("jdbc-service-provider-factory", new JdbcServiceProviderFactoryElementParser());
		registerBeanDefinitionParser("jdbc-connection-repository", new JdbcConnectionRepositoryElementParser());
	}
}
