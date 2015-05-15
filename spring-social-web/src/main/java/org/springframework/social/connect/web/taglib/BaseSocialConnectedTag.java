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
package org.springframework.social.connect.web.taglib;

import org.springframework.social.connect.ConnectionRepository;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.tags.RequestContextAwareTag;

/**
 * {@link SocialConnectedTag} and {@link SocialNotConnectedTag} extend to
 * provide functionality within a JSP to determine if you are connected to a
 * provider or not.
 * 
 * @author Rick Reumann
 * @author Craig Walls
 */
@SuppressWarnings("serial")
public abstract class BaseSocialConnectedTag extends RequestContextAwareTag {

	protected String provider;

	protected int evaluateBodyIfConnected(boolean evaluateIfConnected) {
		if (getConnectionRepository().findConnections(provider).size() > 0) {
			return evaluateIfConnected ? EVAL_BODY_INCLUDE : SKIP_BODY;
		}
		return evaluateIfConnected ? SKIP_BODY : EVAL_BODY_INCLUDE;
	}

	private ConnectionRepository getConnectionRepository() {
		WebApplicationContext applicationContext = getRequestContext().getWebApplicationContext();
		return applicationContext.getBean(ConnectionRepository.class);
	}

	public void setProvider(String provider) {
		this.provider = provider;
	}

}
