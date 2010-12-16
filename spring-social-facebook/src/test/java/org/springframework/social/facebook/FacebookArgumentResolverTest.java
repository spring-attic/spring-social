/*
 * Copyright 2010 the original author or authors.
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
package org.springframework.social.facebook;

import static org.junit.Assert.*;
import static org.springframework.web.bind.support.WebArgumentResolver.*;

import java.lang.reflect.Method;

import javax.servlet.http.Cookie;

import org.junit.Before;
import org.junit.Test;
import org.springframework.core.MethodParameter;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.ServletWebRequest;

/**
 * @author Craig Walls
 */
public class FacebookArgumentResolverTest {
	private static final String API_KEY = "API_KEY";
	private FacebookWebArgumentResolver resolver;

	@Before
	public void setup() {
		resolver = new FacebookWebArgumentResolver(API_KEY);
	}

	@Test(expected = IllegalStateException.class)
	public void resolveFacebookUserIdArgument_noCookies_required() throws Exception {
		NativeWebRequest request = new ServletWebRequest(new MockHttpServletRequest());
		Method method = FacebookArgumentResolverTest.class.getDeclaredMethod("annotatedMethod", String.class,
				String.class, String.class);

		MethodParameter idParameter = new MethodParameter(method, 0);
		assertEquals(UNRESOLVED, resolver.resolveArgument(idParameter, request));
	}

	@Test(expected = IllegalStateException.class)
	public void resolveFacebookAccessTokenArgument_noCookies_required() throws Exception {
		NativeWebRequest request = new ServletWebRequest(new MockHttpServletRequest());
		Method method = FacebookArgumentResolverTest.class.getDeclaredMethod("annotatedMethod", String.class,
				String.class, String.class);

		MethodParameter tokenParameter = new MethodParameter(method, 1);
		resolver.resolveArgument(tokenParameter, request);
	}

	@Test
	public void resolveArgument_noCookies_unrequired() throws Exception {
		NativeWebRequest request = new ServletWebRequest(new MockHttpServletRequest());
		Method method = FacebookArgumentResolverTest.class.getDeclaredMethod("unrequiredAnnotatedMethod", String.class,
				String.class, String.class);

		MethodParameter idParameter = new MethodParameter(method, 0);
		assertEquals(null, resolver.resolveArgument(idParameter, request));

		MethodParameter tokenParameter = new MethodParameter(method, 1);
		assertEquals(null, resolver.resolveArgument(tokenParameter, request));
	}

	@Test(expected = IllegalStateException.class)
	public void resolveFacebookUserIdArgument_noFacebookCookie_required() throws Exception {
		MockHttpServletRequest httpServletRequest = new MockHttpServletRequest();
		httpServletRequest.setCookies(new Cookie("not_a_facebook_cookie", "doesn't matter"));
		NativeWebRequest request = new ServletWebRequest(httpServletRequest);

		Method method = FacebookArgumentResolverTest.class.getDeclaredMethod("annotatedMethod", String.class,
				String.class, String.class);

		MethodParameter idParameter = new MethodParameter(method, 0);
		assertEquals(UNRESOLVED, resolver.resolveArgument(idParameter, request));
	}

	@Test(expected = IllegalStateException.class)
	public void resolveFacebookAccessTokenArgument_noFacebookCookie_required() throws Exception {
		MockHttpServletRequest httpServletRequest = new MockHttpServletRequest();
		httpServletRequest.setCookies(new Cookie("not_a_facebook_cookie", "doesn't matter"));
		NativeWebRequest request = new ServletWebRequest(httpServletRequest);

		Method method = FacebookArgumentResolverTest.class.getDeclaredMethod("annotatedMethod", String.class,
				String.class, String.class);

		MethodParameter tokenParameter = new MethodParameter(method, 1);
		resolver.resolveArgument(tokenParameter, request);
	}


	@Test
	public void resolveArgument_noFacebookCookie_unrequired() throws Exception {
		MockHttpServletRequest httpServletRequest = new MockHttpServletRequest();
		httpServletRequest.setCookies(new Cookie("not_a_facebook_cookie", "doesn't matter"));
		NativeWebRequest request = new ServletWebRequest(httpServletRequest);

		Method method = FacebookArgumentResolverTest.class.getDeclaredMethod("unrequiredAnnotatedMethod", String.class,
				String.class, String.class);

		MethodParameter idParameter = new MethodParameter(method, 0);
		assertEquals(null, resolver.resolveArgument(idParameter, request));

		MethodParameter tokenParameter = new MethodParameter(method, 1);
		assertEquals(null, resolver.resolveArgument(tokenParameter, request));
	}
	
	@Test(expected = IllegalStateException.class)
	public void resolveUserIdArgument_facebookCookieWithoutEntries_required() throws Exception {
		MockHttpServletRequest httpServletRequest = new MockHttpServletRequest();
		httpServletRequest.setCookies(new Cookie("fbs_" + API_KEY, "foo=bar&cat=feline"));
		NativeWebRequest request = new ServletWebRequest(httpServletRequest);
		Method method = FacebookArgumentResolverTest.class.getDeclaredMethod("annotatedMethod", String.class,
				String.class, String.class);

		MethodParameter idParameter = new MethodParameter(method, 0);
		assertEquals(UNRESOLVED, resolver.resolveArgument(idParameter, request));
	}

	@Test(expected = IllegalStateException.class)
	public void resolveAccessTokenArgument_facebookCookieWithoutEntries_required() throws Exception {
		MockHttpServletRequest httpServletRequest = new MockHttpServletRequest();
		httpServletRequest.setCookies(new Cookie("fbs_" + API_KEY, "foo=bar&cat=feline"));
		NativeWebRequest request = new ServletWebRequest(httpServletRequest);
		Method method = FacebookArgumentResolverTest.class.getDeclaredMethod("annotatedMethod", String.class,
				String.class, String.class);

		MethodParameter tokenParameter = new MethodParameter(method, 1);
		assertEquals(UNRESOLVED, resolver.resolveArgument(tokenParameter, request));
	}

	@Test
	public void resolveArgument_facebookCookieWithoutEntries_unrequired() throws Exception {
		MockHttpServletRequest httpServletRequest = new MockHttpServletRequest();
		httpServletRequest.setCookies(new Cookie("fbs_" + API_KEY, "foo=bar&cat=feline"));
		NativeWebRequest request = new ServletWebRequest(httpServletRequest);
		Method method = FacebookArgumentResolverTest.class.getDeclaredMethod("unrequiredAnnotatedMethod", String.class,
				String.class, String.class);

		MethodParameter idParameter = new MethodParameter(method, 0);
		assertEquals(null, resolver.resolveArgument(idParameter, request));

		MethodParameter tokenParameter = new MethodParameter(method, 1);
		assertEquals(null, resolver.resolveArgument(tokenParameter, request));

		MethodParameter otherParameter = new MethodParameter(method, 2);
		assertEquals(UNRESOLVED, resolver.resolveArgument(otherParameter, request));
	}

	@Test
	public void resolveArgument() throws Exception {
		MockHttpServletRequest httpServletRequest = new MockHttpServletRequest();
		httpServletRequest.setCookies(new Cookie("fbs_" + API_KEY, "uid=24680&access_token=a1b2c3d4%7Ce5f6"));
		NativeWebRequest request = new ServletWebRequest(httpServletRequest);
		Method method = FacebookArgumentResolverTest.class.getDeclaredMethod("annotatedMethod", String.class,
				String.class, String.class);

		MethodParameter idParameter = new MethodParameter(method, 0);
		assertEquals("24680", (String) resolver.resolveArgument(idParameter, request));

		MethodParameter tokenParameter = new MethodParameter(method, 1);
		assertEquals("a1b2c3d4|e5f6", (String) resolver.resolveArgument(tokenParameter, request));
	}

	@SuppressWarnings("unused")
	private void annotatedMethod(@FacebookUserId String userId, @FacebookAccessToken String accessToken,
			String someOtherParameter) {

	}

	@SuppressWarnings("unused")
	private void unrequiredAnnotatedMethod(@FacebookUserId(required = false) String userId,
			@FacebookAccessToken(required = false) String accessToken, String someOtherParameter) {
	}
}
