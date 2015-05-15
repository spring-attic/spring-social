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
package org.springframework.social.connect.web;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.junit.Test;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.social.UserIdSource;
import org.springframework.social.connect.ConnectionRepository;
import org.springframework.social.connect.UsersConnectionRepository;

public class ReconnectFilterTest {

	@Test
	public void shouldPerformRefreshPostRequest() throws Exception {
		performFilterForPostRequest("/connect/google", "google");
		performFilterForPostRequest("/connect/google/", "google");
	}

	private void performFilterForPostRequest(String servletPath, String providerId) throws IOException, ServletException {
		UsersConnectionRepository uconnRepo = mock(UsersConnectionRepository.class);
		UserIdSource userIdSource = new UserIdSource() {
			public String getUserId() {
				return "habuma";
			}
		};

		ConnectionRepository connRepo = mock(ConnectionRepository.class);
		when(uconnRepo.createConnectionRepository("habuma")).thenReturn(connRepo);
		ReconnectFilter filter = new ReconnectFilter(uconnRepo, userIdSource);
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setMethod("GET");
		request.setServletPath(servletPath);
		request.addParameter("reconnect", "true");
		MockHttpServletResponse response = new MockHttpServletResponse();
		MockFilterChain chain = new MockFilterChain();
		filter.doFilter(request, response, chain);
		HttpServletRequest redirectRequest = (HttpServletRequest) chain.getRequest();
		assertEquals("POST", redirectRequest.getMethod());
		assertEquals(servletPath, redirectRequest.getServletPath());
		
		verify(connRepo).removeConnections(providerId);
	}
	
}
