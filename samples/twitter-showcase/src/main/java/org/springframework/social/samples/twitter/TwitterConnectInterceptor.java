/*
 * Copyright 2010 the original author or authors.
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
package org.springframework.social.samples.twitter;

import org.springframework.social.connect.ServiceProvider;
import org.springframework.social.connect.web.ConnectInterceptor;
import org.springframework.social.twitter.TwitterOperations;
import org.springframework.web.context.request.WebRequest;


public class TwitterConnectInterceptor implements ConnectInterceptor<TwitterOperations> {
	public void preConnect(ServiceProvider<TwitterOperations> provider, WebRequest request) {
		// nothing to do
	}

	public void postConnect(ServiceProvider<TwitterOperations> provider, WebRequest request) {
		provider.getServiceOperations(getAccountId()).updateStatus("I just connected with the Spring Social Showcase!");
	}

	private Long getAccountId() {
		// ...
		return 0L;
	}
}