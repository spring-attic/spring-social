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
package org.springframework.social.connect.support;

import java.io.Serializable;

import org.springframework.social.connect.ServiceProviderConnection;
import org.springframework.social.connect.ServiceProviderConnectionMemento;

public class ServiceProviderConnectionImpl<S> implements ServiceProviderConnection<S> {

	public Long getId() {
		return null;
	}

	public Serializable getAccountId() {
		return null;
	}

	public String getProviderId() {
		return null;
	}

	public String getProviderAccountId() {
		return null;
	}

	public String getProfileName() {
		return null;
	}

	public String getProfileUrl() {
		return null;
	}

	public String getProfilePictureUrl() {
		return null;
	}

	public boolean supportsSignIn() {
		return false;
	}

	public boolean test() {
		return false;
	}

	public void updateStatus(String message) {
	}

	public void sync() {
	}

	public S getServiceApi() {
		return null;
	}

	public ServiceProviderConnection<S> assignAccountId(Serializable accountId) {
		return null;
	}
	
	public ServiceProviderConnectionMemento createMemento() {
		return null;
	}

	public ServiceProviderConnection<S> assignId(Long id) {
		// TODO Auto-generated method stub
		return null;
	}

}