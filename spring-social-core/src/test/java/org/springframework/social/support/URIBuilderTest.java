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

import static org.junit.Assert.*;

import java.net.URI;

import org.junit.Test;

public class URIBuilderTest {

	@Test
	public void addParameterWithNoExistingParameters() {
		URI uri = URIBuilder.fromUri("http://someurl.com/foo/bar").queryParam("xyz", "987").build();
		assertEquals("xyz=987", uri.getQuery());
	}

	@Test
	public void addParameterToExistingParameters() {
		URI uri = URIBuilder.fromUri("http://someurl.com/foo/bar?abc=123").queryParam("xyz", "987").build();
		assertEquals("abc=123&xyz=987", uri.getQuery());
	}
	
	@Test
	public void addParameterToExistingParameters_fromURI() throws Exception {
		URI baseUri = new URI("http://someurl.com/foo/bar");
		URI uri = URIBuilder.fromUri(baseUri).queryParam("xyz", "987").build();
		assertEquals("xyz=987", uri.getQuery());
	}
	
	@Test
	public void addParameterWithNoExistingParameters_fromURI() throws Exception {
		URI baseUri = new URI("http://someurl.com/foo/bar?abc=123");
		URI uri = URIBuilder.fromUri(baseUri).queryParam("xyz", "987").build();
		assertEquals("abc=123&xyz=987", uri.getQuery());
	}

}
