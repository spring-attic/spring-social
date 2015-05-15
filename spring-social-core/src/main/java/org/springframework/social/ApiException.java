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
package org.springframework.social;

/**
 * Indicates that an error occurred while consuming a provider API.
 * @author Craig Walls
 */
@SuppressWarnings("serial")
public class ApiException extends SocialException {

	private String providerId;

	public ApiException(String providerId, String message) {
		this(providerId, message, null);
	}

	public ApiException(String providerId, String message, Throwable cause) {
		super(message, cause);
		this.providerId = providerId;
	}
	
	/**
	 * The ID of the provider for which the API exception occurred.
	 * @return The ID of the provider for which the API exception occurred.
	 */
	public String getProviderId() {
		return providerId;
	}

}
