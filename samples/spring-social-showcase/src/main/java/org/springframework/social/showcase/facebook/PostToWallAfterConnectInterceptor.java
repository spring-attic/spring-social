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
package org.springframework.social.showcase.facebook;

import org.springframework.social.connect.ServiceProvider;
import org.springframework.social.connect.ServiceProviderConnection;
import org.springframework.social.facebook.FacebookOperations;
import org.springframework.social.twitter.DuplicateTweetException;
import org.springframework.social.web.connect.ConnectInterceptor;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.WebRequest;

public class PostToWallAfterConnectInterceptor implements ConnectInterceptor<FacebookOperations> {

	public void preConnect(ServiceProvider<FacebookOperations> provider, WebRequest request) {
		if (StringUtils.hasText(request.getParameter(POST_TO_WALL_PARAMETER))) {
			request.setAttribute(POST_TO_WALL_ATTRIBUTE, Boolean.TRUE, WebRequest.SCOPE_SESSION);
		}
	}

	public void postConnect(ServiceProvider<FacebookOperations> provider,
			ServiceProviderConnection<FacebookOperations> connection, WebRequest request) {
		if (request.getAttribute(POST_TO_WALL_ATTRIBUTE, WebRequest.SCOPE_SESSION) != null) {
			try {
				connection.getServiceApi().updateStatus("I've connected with the Spring Social Showcase!");
			} catch (DuplicateTweetException e) {
			}
			request.removeAttribute(POST_TO_WALL_ATTRIBUTE, WebRequest.SCOPE_SESSION);
		}
	}

	private static final String POST_TO_WALL_PARAMETER = "postToWall";

	private static final String POST_TO_WALL_ATTRIBUTE = "facebookConnect." + POST_TO_WALL_PARAMETER;
}
