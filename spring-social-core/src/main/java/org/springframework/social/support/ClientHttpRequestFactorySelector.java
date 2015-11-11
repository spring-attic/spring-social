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
package org.springframework.social.support;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URI;
import java.security.GeneralSecurityException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Properties;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;

import org.apache.http.HttpHost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.protocol.HttpContext;
import org.apache.http.ssl.SSLContextBuilder;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.util.ClassUtils;


/**
 * Chooses a request factory. Picks a HttpComponentsClientRequestFactory factory if Apache HttpComponents HttpClient is in the classpath.
 * If not, falls back to SimpleClientHttpRequestFactory.
 * @author Craig Walls
 * @author Roy Clarkson
 */
public class ClientHttpRequestFactorySelector {
	
	public static ClientHttpRequestFactory foo() {
		return new HttpComponentsClientHttpRequestFactory();
	}
	
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
	
	private static final boolean HTTP_COMPONENTS_AVAILABLE = ClassUtils.isPresent("org.apache.http.client.HttpClient", ClientHttpRequestFactory.class.getClassLoader());

	public static class HttpComponentsClientRequestFactoryCreator {
		
		private static boolean isAllTrust = false;
		
		public static ClientHttpRequestFactory createRequestFactory(String proxyHost, int proxyPort) {
			
			HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory() {
				@Override
				protected HttpContext createHttpContext(HttpMethod httpMethod, URI uri) {
					HttpClientContext context = new HttpClientContext();
					context.setAttribute("http.protocol.expect-continue", false);
					return context;
				}
			};
			
			if (proxyHost != null) {
				HttpHost proxy = new HttpHost(proxyHost, proxyPort);
				CloseableHttpClient httpClient = isAllTrust ? getAllTrustClient(proxy) : getClient(proxy);
				requestFactory.setHttpClient(httpClient);
			}
			
			return requestFactory;
			
		}

		private static CloseableHttpClient getClient(HttpHost proxy) {
			return HttpClients.custom()
					.setProxy(proxy)
					.build();
		}

		private static CloseableHttpClient getAllTrustClient(HttpHost proxy) {
			try {
				HttpClientBuilder clientBuilder = HttpClientBuilder.create();
				SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null, new TrustStrategy() {
					public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {
						return true;
					}
				}).build();
				clientBuilder.setSSLContext(sslContext);
				
				HostnameVerifier hostnameVerifier = new NoopHostnameVerifier();
	
				SSLConnectionSocketFactory sslSocketFactory = new SSLConnectionSocketFactory(sslContext, hostnameVerifier);
				Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
						.register("http", PlainConnectionSocketFactory.getSocketFactory())
						.register("https", sslSocketFactory)
						.build();
			 
				PoolingHttpClientConnectionManager connMgr = new PoolingHttpClientConnectionManager( socketFactoryRegistry);
				clientBuilder.setConnectionManager(connMgr);
			 
				return clientBuilder.build();
			} catch (GeneralSecurityException e) {
				// shouldn't happen
				throw new RuntimeException(e);
			}
		}
		
	}

	/**
	 * Trust all SSL certificates.
	 * For use when using {@link HttpComponentsClientHttpRequestFactory} in a test environment. Not recommended for general use.
	 * @param isAllTrust if true, all certificates will be trusted.
	 */
	public static void setAllTrust(boolean isAllTrust) {
		HttpComponentsClientRequestFactoryCreator.isAllTrust = isAllTrust;
	}

}
