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
package org.springframework.social.support;

import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.social.support.httpclient.HttpComponentsClientHttpRequestFactory;
import org.springframework.util.ClassUtils;

/**
 * Chooses a request factory. Picks an Apache HttpComponents HttpClient factory if Apache HttpComponents HttpClient is in the classpath.
 * If not, falls back to SimpleClientHttpRequestFactory.
 * @author Craig Walls
 */
public class ClientHttpRequestFactorySelector {
	
	public static ClientHttpRequestFactory getRequestFactory() {
		if (httpComponentsAvailable) {
			return new HttpComponentsClientHttpRequestFactory();
		} else {
			return new SimpleClientHttpRequestFactory();
		}		
	}
	
	private static boolean httpComponentsAvailable = ClassUtils.isPresent("org.apache.http.client.HttpClient", ClientHttpRequestFactory.class.getClassLoader());

}
