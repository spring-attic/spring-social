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

	public ServiceProviderUser getUser(Object serviceApi) {
		return EMPTY_PROFILE;
	}

	public void updateStatus(Object serviceApi, String message) {
	}

	private NullServiceApiAdapter() {}
	
	private static final ServiceProviderUser EMPTY_PROFILE = new ServiceProviderUser(null, null, null, null);
}
