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
package org.springframework.social.twitter.api.impl;

import java.util.List;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import org.springframework.social.twitter.api.TwitterProfile;

/**
 * Holder for list of TwitterProfile objects pulled from a JSON object's "users" property.
 * @author Craig Walls
 */
@JsonIgnoreProperties(ignoreUnknown = true)
class TwitterProfileUsersList {

	private final List<TwitterProfile> list;

	@JsonCreator
	public TwitterProfileUsersList(@JsonProperty("users") List<TwitterProfile> list) {
		this.list = list;		
	}

	public List<TwitterProfile> getList() {
		return list;
	}
}
