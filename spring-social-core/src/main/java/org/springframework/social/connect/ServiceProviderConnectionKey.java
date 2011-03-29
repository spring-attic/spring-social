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
package org.springframework.social.connect;

import java.io.Serializable;

public final class ServiceProviderConnectionKey {

	private final Serializable accountId;
	
	private final String providerId;
	
	private final Integer id;

	public ServiceProviderConnectionKey(Serializable accountId, String providerId, Integer id) {
		this.accountId = accountId;
		this.providerId = providerId;
		this.id = id;
	}

	/**
	 * The local account id representing this end of the connection.
	 * A key property; never null.
	 */
	public Serializable getAccountId() {
		return accountId;
	}

	/**
	 * The id of the provider as it is registered in the system.
	 * A key property; never null.
	 */
	public String getProviderId() {
		return providerId;
	}
	
	/**
	 * The locally assigned id of this connection.
	 * Unique relative to the accountId and providerId.
	 * This should never change.
	 * A key property; never null.
	 */
	public Integer getId() {
		return id;
	}
	
}
