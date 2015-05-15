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
package org.springframework.social.util;

import static org.junit.Assert.*;

import java.net.URI;

import org.junit.Test;
import org.springframework.social.support.URIBuilder;

public class URIBuilderTest {
	
	@Test
	public void buildSimpleURI() {
		URI uri = URIBuilder.fromUri("http://example.com").build();
		assertEquals("http://example.com", uri.toString());		
	}
	
	@Test
	public void buildURIWithExistingParameters() {
		URI uri = URIBuilder.fromUri("http://example.com?foo=bar").build();
		assertEquals("http://example.com?foo=bar", uri.toString());				
	}

	@Test
	public void buildURIWithExistingAndNewParameters() {
		URI uri = URIBuilder.fromUri("http://example.com?foo=bar&x=1").queryParam("salt", "NaCl").build();
		assertEquals("http://example.com?foo=bar&x=1&salt=NaCl", uri.toString());				
	}

	@Test
	public void buildURIWithOneParameter() {
		URI uri = URIBuilder.fromUri("http://example.com").queryParam("foo", "bar").build();
		assertEquals("http://example.com?foo=bar", uri.toString());		
	}
	
	@Test
	public void buildURIWithMultipleParameters() {
		URI uri = URIBuilder.fromUri("http://example.com").queryParam("foo", "bar").queryParam("abc", "123")
			.queryParam("xyz", "987").build();
		assertEquals("http://example.com?foo=bar&abc=123&xyz=987", uri.toString());		
	}
	
	@Test
	public void buildURIWithSameNamedParameters() {
		URI uri = URIBuilder.fromUri("http://example.com").queryParam("foo", "bar").queryParam("foo", "123")
			.queryParam("foo", "987").build();
		assertEquals("http://example.com?foo=bar&foo=123&foo=987", uri.toString());		
	}
	
	@Test
	public void buildURIWithEmptyParameter() {
		URI uri = URIBuilder.fromUri("http://example.com").queryParam("foo", "").build();
		assertEquals("http://example.com?foo=", uri.toString());		
	}
	
	@Test
	public void buildURIWithNullParameter() {
		URI uri = URIBuilder.fromUri("http://example.com").queryParam("foo", null).build();
		assertEquals("http://example.com?foo=", uri.toString());		
	}
	
	@Test
	public void buildURIWithFormEncodedParameterValue() {
		URI uri = URIBuilder.fromUri("http://example.com").queryParam("foo", "2 + 3 = 5").build();
		assertEquals("http://example.com?foo=2+%2B+3+%3D+5", uri.toString());		
	}
	
	@Test
	public void buildURIWithFormEncodedParameterName() {
		URI uri = URIBuilder.fromUri("http://example.com").queryParam("f@@", "2 + 3 = 5").build();
		assertEquals("http://example.com?f%40%40=2+%2B+3+%3D+5", uri.toString());		
	}
}
