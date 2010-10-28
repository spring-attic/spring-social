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
package org.springframework.social.greenhouse;

import org.codehaus.jackson.annotate.JsonProperty;

/**
 * A model class containing a Greenhouse user's profile information.
 * 
 * @author Craig Walls
 */
public class GreenhouseProfile {
	@JsonProperty
	long accountId;

	@JsonProperty
	String displayName;

	@JsonProperty
	String pictureUrl;

	/**
	 * The user's Greenhouse account ID.
	 * 
	 * @return The user's Greenhouse account ID.
	 */
	public long getAccountId() {
		return accountId;
	}

	/**
	 * The user's display name.
	 * 
	 * @return The user's display name.
	 */
	public String getDisplayName() {
		return displayName;
	}

	/**
	 * The URL of the user's profile picture.
	 * 
	 * @return The URL of the user's profile picture.
	 */
	public String getPictureUrl() {
		return pictureUrl;
	}
}
