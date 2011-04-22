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

final class NullServiceApiAdapter implements ServiceApiAdapter<Object> {

	public static final NullServiceApiAdapter INSTANCE = new NullServiceApiAdapter();
	
	public boolean test(Object serviceApi) {
		return true;
	}

	public void setConnectionValues(Object serviceApi, ServiceProviderConnectionValues connectionValues) {
		
	}

	public ServiceProviderUserProfile fetchUserProfile(Object serviceApi) {
		return EMPTY_USER_PROFILE;
	}

	public void updateStatus(Object serviceApi, String message) {
	}

	// internal helpers
	
	private NullServiceApiAdapter() {}
	
	private static final ServiceProviderUserProfile EMPTY_USER_PROFILE = new ServiceProviderUserProfile(null, null, null, null, null);

}
