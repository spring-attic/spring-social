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
package org.springframework.social.connect.config;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

public class ServiceProviderElementParser extends AbstractServiceProviderElementParser {

	public BeanDefinition parse(Element element, ParserContext parserContext) {
		String name = element.getAttribute("id");
		String className = element.getAttribute("class");
		String displayName = element.getAttribute("display-name");
		if (displayName == null) {
			displayName = name.substring(0, 1).toUpperCase() + name.substring(1);
		}

		String consumerKey = element.getAttribute("consumer-key");
		String consumerSecret = element.getAttribute("consumer-secret");
		String appIdString = element.getAttribute("app-id");
		Long appId = appIdString != null && !appIdString.isEmpty() ? Long.valueOf(appIdString) : null;
		String requestTokenUrl = element.getAttribute("request-token-url");
		requestTokenUrl = requestTokenUrl != null && requestTokenUrl.isEmpty() ? null : requestTokenUrl;
		String authorizeUrl = element.getAttribute("authorization-url");
		String accessTokenUrl = element.getAttribute("access-token-url");
		String connectionRepositoryBean = element.getAttribute("connection-repository");
		
		return registerServiceProviderBean(parserContext, name, className, displayName, consumerKey, consumerSecret,
				appId, requestTokenUrl, authorizeUrl, accessTokenUrl, connectionRepositoryBean);
	}
}
