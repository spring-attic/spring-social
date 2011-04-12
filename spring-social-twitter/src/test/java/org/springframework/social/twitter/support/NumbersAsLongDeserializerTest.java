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
package org.springframework.social.twitter.support;

import static org.junit.Assert.*;
import static org.springframework.social.test.client.RequestMatchers.*;
import static org.springframework.social.test.client.ResponseCreators.*;

import java.util.List;

import org.codehaus.jackson.Version;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.module.SimpleModule;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJacksonHttpMessageConverter;
import org.springframework.social.test.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

public class NumbersAsLongDeserializerTest {

	private RestTemplate restTemplate;

	@Before
	public void setup() {
		restTemplate = new RestTemplate();
		List<HttpMessageConverter<?>> converters = restTemplate.getMessageConverters();
		for (HttpMessageConverter<?> converter : converters) {
			if(converter instanceof MappingJacksonHttpMessageConverter) {
				MappingJacksonHttpMessageConverter jsonConverter = (MappingJacksonHttpMessageConverter) converter;
				ObjectMapper objectMapper = new ObjectMapper();
				
				SimpleModule testModule = new SimpleModule("MyModule", new Version(1, 0, 0, null))
				   .addDeserializer(Object.class, new NumbersAsLongDeserializer());
				objectMapper.registerModule(testModule);

				jsonConverter.setObjectMapper(objectMapper);
			}
		}
	}
	
	@Test
	public void deserializeNumbersToLongs() {
		MockRestServiceServer mockServer = MockRestServiceServer.createServer(restTemplate);
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.setContentType(MediaType.APPLICATION_JSON);
		mockServer.expect(requestTo("http://foo.bar/xyz"))
			.andRespond(withResponse("[12345,9223372036854775807,1]", responseHeaders));
		@SuppressWarnings("unchecked")
		List<Long> response = restTemplate.getForObject("http://foo.bar/xyz", List.class);
		assertEquals(3, response.size());
		assertEquals(12345, (long) response.get(0));
		assertEquals(9223372036854775807L, (long) response.get(1));
		assertEquals(1, (long) response.get(2));		
	}
}
