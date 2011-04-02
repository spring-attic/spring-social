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

public final class ServiceProviderConnectionKey {
	
	private final String providerId;
	
	private final String providerUserId;

	public ServiceProviderConnectionKey(String providerId, String providerUserId) {
		this.providerId = providerId;
		this.providerUserId = providerUserId;
	}
	
	/**
	 * The id of the provider as it is registered in the system.
	 * This value should never change.
	 * Never null.
	 */	
	public String getProviderId() {
		return providerId;
	}

	/**
	 * The id of the external provider user representing the remote end of the connection.
	 * May be null if this information is not exposed by the provider.
	 * This value should never change.
	 * Must be present to support sign-in by the provider user.
	 * Must be present to establish multiple connections with the provider.
	 */
	public String getProviderUserId() {
		return providerUserId;
	}
	
	public boolean equals(Object o) {
		if (!(o instanceof ServiceProviderConnectionKey)) {
			return false;
		}
		ServiceProviderConnectionKey other = (ServiceProviderConnectionKey) o;
		boolean sameProvider = providerId.equals(other.providerId);
		return providerUserId != null ? sameProvider && providerUserId.equals(providerUserId) : sameProvider;
	}
	
	public int hashCode() {
		int hashCode = providerId.hashCode();
		return providerUserId != null ? hashCode + providerUserId.hashCode() : hashCode;
	}

}
