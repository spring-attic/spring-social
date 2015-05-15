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

import static org.mockito.Mockito.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.net.URI;

import org.junit.After;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpResponse;

public class ClientHttpRequestFactorySelectorTest {

	@After
	public void teardown() {
		System.getProperties().remove("http.proxyHost");
		System.getProperties().remove("http.proxyPort");
	}
	
	@Test
	@Ignore("It's unclear how to read proxy settings from the request factory in HttpClient 4.3.x")
	public void getRequestFactory_noProxy() {
//		HttpComponentsClientHttpRequestFactory requestFactory = (HttpComponentsClientHttpRequestFactory) ClientHttpRequestFactorySelector.getRequestFactory();
//		HttpHost proxy = (HttpHost) requestFactory.getHttpClient().getParams().getParameter(ConnRoutePNames.DEFAULT_PROXY);
//		assertNull(proxy);
	}

	@Test
	@Ignore("It's unclear how to read proxy settings from the request factory in HttpClient 4.3.x")
	public void getRequestFactory_withProxy() {
//		System.getProperties().setProperty("http.proxyHost", "someproxyhost");
//		System.getProperties().setProperty("http.proxyPort", "8080");		
//		HttpComponentsClientHttpRequestFactory requestFactory = (HttpComponentsClientHttpRequestFactory) ClientHttpRequestFactorySelector.getRequestFactory();
//		HttpHost proxy = (HttpHost) requestFactory.getHttpClient().getParams().getParameter(ConnRoutePNames.DEFAULT_PROXY);
//		assertNotNull(proxy);
//		assertEquals("someproxyhost", proxy.getHostName());
//		assertEquals(8080, proxy.getPort());
	}

	@Test
	@Ignore("It's unclear how to read proxy settings from the request factory in HttpClient 4.3.x")
	public void getRequestFactory_withDefaultProxyPort() {
//		System.getProperties().setProperty("http.proxyHost", "someproxyhost");
//		HttpComponentsClientHttpRequestFactory requestFactory = (HttpComponentsClientHttpRequestFactory) ClientHttpRequestFactorySelector.getRequestFactory();
//		HttpHost proxy = (HttpHost) requestFactory.getHttpClient().getParams().getParameter(ConnRoutePNames.DEFAULT_PROXY);
//		assertNotNull(proxy);
//		assertEquals("someproxyhost", proxy.getHostName());
//		assertEquals(80, proxy.getPort());
	}

	@Test
	public void bufferRequests() throws Exception {
		ClientHttpRequest mockRequest = mock(ClientHttpRequest.class);
		ClientHttpResponse mockResponse = mock(ClientHttpResponse.class);
		when(mockResponse.getBody()).thenReturn(new ByteArrayInputStream("Test Body".getBytes()));
		when(mockRequest.getHeaders()).thenReturn(new HttpHeaders());
		when(mockRequest.getBody()).thenReturn(new ByteArrayOutputStream());
		when(mockRequest.execute()).thenReturn(mockResponse);
		ClientHttpRequestFactory mockRequestFactory = mock(ClientHttpRequestFactory.class);
		when(mockRequestFactory.createRequest(new URI("http://somehost.com/test"), HttpMethod.GET)).thenReturn(mockRequest);
		ClientHttpRequestFactory bufferingRequestFactory = ClientHttpRequestFactorySelector.bufferRequests(mockRequestFactory);
		ClientHttpRequest request = bufferingRequestFactory.createRequest(new URI("http://somehost.com/test"), HttpMethod.GET);
		ClientHttpResponse response = request.execute();
		response.getBody();
		response.getBody();
		response.getBody();
		verify(mockRequest, times(1)).getBody();
	}
}
