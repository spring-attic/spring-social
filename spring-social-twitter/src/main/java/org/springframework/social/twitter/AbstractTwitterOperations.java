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
package org.springframework.social.twitter;

import java.net.URI;
import java.util.Collections;
import java.util.Map;

import org.springframework.social.util.URIBuilder;

public class AbstractTwitterOperations {
	
	private final boolean isAuthorizedForUser;

	public AbstractTwitterOperations(boolean isAuthorizedForUser) {
		this.isAuthorizedForUser = isAuthorizedForUser;
	}
	
	protected void requireUserAuthorization() {
		if(!isAuthorizedForUser) {
			throw new IllegalStateException("User authorization required: TwitterTemplate must be created with OAuth credentials to perform this operation.");
		}
	}
	
	protected URI buildUri(String path) {
		return buildUri(path, Collections.<String, String>emptyMap());
	}
	
	protected URI buildUri(String path, Map<String, String> params) {
		URIBuilder uriBuilder = URIBuilder.fromUri(API_URL_BASE + path);
		for (String paramName : params.keySet()) {
			uriBuilder.queryParam(paramName, String.valueOf(params.get(paramName)));
		}
		URI uri = uriBuilder.build();
		return uri;
	}
	
	private static final String API_URL_BASE = "https://api.twitter.com/1/";

}
