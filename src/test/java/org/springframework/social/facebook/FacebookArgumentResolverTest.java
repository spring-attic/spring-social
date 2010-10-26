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

public class FacebookArgumentResolverTest {
	private static final String APPLICATION_KEY = "APPLICATION_KEY";
	private FacebookWebArgumentResolver resolver;

	@Before
	public void setup() {
		resolver = new FacebookWebArgumentResolver(APPLICATION_KEY);
	}

	@Test
	public void resolveArgument_noCookies() throws Exception {
		NativeWebRequest request = new ServletWebRequest(new MockHttpServletRequest());
		assertEquals(UNRESOLVED, resolver.resolveArgument(null, request));
	}

	@Test
	public void resolveArgument_noFacebookCookie() throws Exception {
		MockHttpServletRequest httpServletRequest = new MockHttpServletRequest();
		httpServletRequest.setCookies(new Cookie("not_a_facebook_cookie", "doesn't matter"));
		NativeWebRequest request = new ServletWebRequest(httpServletRequest);
		assertEquals(UNRESOLVED, resolver.resolveArgument(null, request));
	}

	@Test
	public void resolveArgument_facebookCookieWithoutEntries() throws Exception {
		MockHttpServletRequest httpServletRequest = new MockHttpServletRequest();
		httpServletRequest.setCookies(new Cookie("fbs_" + APPLICATION_KEY, "foo=bar&cat=feline"));
		NativeWebRequest request = new ServletWebRequest(httpServletRequest);
		Method method = FacebookArgumentResolverTest.class.getDeclaredMethod("annotatedMethod", String.class,
				String.class, String.class);

		MethodParameter idParameter = new MethodParameter(method, 0);
		assertNull(resolver.resolveArgument(idParameter, request));

		MethodParameter tokenParameter = new MethodParameter(method, 1);
		assertNull(resolver.resolveArgument(tokenParameter, request));

		MethodParameter otherParameter = new MethodParameter(method, 2);
		assertEquals(UNRESOLVED, resolver.resolveArgument(otherParameter, request));
	}

	@Test
	public void resolveArgument() throws Exception {
		MockHttpServletRequest httpServletRequest = new MockHttpServletRequest();
		httpServletRequest.setCookies(new Cookie("fbs_" + APPLICATION_KEY, "uid=24680&access_token=a1b2c3d4%7Ce5f6"));
		NativeWebRequest request = new ServletWebRequest(httpServletRequest);
		Method method = FacebookArgumentResolverTest.class.getDeclaredMethod("annotatedMethod", String.class,
				String.class, String.class);

		MethodParameter idParameter = new MethodParameter(method, 0);
		assertEquals("24680", (String) resolver.resolveArgument(idParameter, request));

		MethodParameter tokenParameter = new MethodParameter(method, 1);
		assertEquals("a1b2c3d4|e5f6", (String) resolver.resolveArgument(tokenParameter, request));
	}

	private void annotatedMethod(@FacebookUserId String userId, @FacebookAccessToken String accessToken,
			String someOtherParameter) {

	}
}
