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

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.Properties;

import org.apache.http.HttpHost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpProtocolParams;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.util.ClassUtils;

/**
 * Chooses a request factory. Picks a HttpComponentsClientRequestFactory factory if Apache HttpComponents HttpClient is in the classpath.
 * If not, falls back to SimpleClientHttpRequestFactory.
 * @author Craig Walls
 * @author Roy Clarkson
 */
public class ClientHttpRequestFactorySelector {
	
	public static ClientHttpRequestFactory getRequestFactory() {
		Properties properties = System.getProperties();
		String proxyHost = properties.getProperty("http.proxyHost");
		int proxyPort = properties.containsKey("http.proxyPort") ? Integer.valueOf(properties.getProperty("http.proxyPort")) : 80;
		if (HTTP_COMPONENTS_AVAILABLE) {
			return HttpComponentsClientRequestFactoryCreator.createRequestFactory(proxyHost, proxyPort);
		} else {
			SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
			if (proxyHost != null) {
				requestFactory.setProxy(new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyHost, proxyPort)));
			}
			return requestFactory;
		}
	}
	
	/**
	 * Decorates a request factory to buffer responses so that the responses may be repeatedly read.
	 * @param requestFactory the request factory to be decorated for buffering
	 * @return a buffering request factory
	 */
	public static ClientHttpRequestFactory bufferRequests(ClientHttpRequestFactory requestFactory) {
		return new BufferingClientHttpRequestFactory(requestFactory);
	}
	
	private static boolean HTTP_COMPONENTS_AVAILABLE = ClassUtils.isPresent("org.apache.http.client.HttpClient", ClientHttpRequestFactory.class.getClassLoader());

	private static class HttpComponentsClientRequestFactoryCreator {
		
		public static ClientHttpRequestFactory createRequestFactory(String proxyHost, int proxyPort) {
			HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory() {
				@Override
				protected void postProcessHttpRequest(HttpUriRequest request) {
					HttpProtocolParams.setUseExpectContinue(request.getParams(), false);				}
			};
			if (proxyHost != null) {
				DefaultHttpClient httpClient = new DefaultHttpClient();
				HttpHost proxy = new HttpHost(proxyHost, proxyPort);
				httpClient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);
				requestFactory.setHttpClient(httpClient);
			}			
			return requestFactory;			
		}
	}
}
