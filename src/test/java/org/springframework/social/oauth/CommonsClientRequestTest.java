package org.springframework.social.oauth;

import static org.junit.Assert.*;

import java.net.URI;
import java.util.Map;

import org.apache.commons.httpclient.HttpMethodBase;
import org.apache.commons.httpclient.methods.DeleteMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.PutMethod;
import org.junit.Test;
import org.springframework.http.HttpMethod;

public class CommonsClientRequestTest {
	@Test
	public void getURI() {
		HttpMethodBase methodBase = new GetMethod("http://foo.com/bar");
		CommonsClientRequest request = new CommonsClientRequest(methodBase);
		URI uri = request.getURI();
		assertEquals("http://foo.com/bar", uri.toString());
	}

	@Test
	public void setParameter() {
		HttpMethodBase methodBase = new GetMethod("http://foo.com/bar");
		CommonsClientRequest request = new CommonsClientRequest(methodBase);
		request.setParameter("abc", "xyz");
		request.setParameter("access_token", "someToken");
		assertEquals("http://foo.com/bar?abc=xyz&access_token=someToken", request.getURI().toString());
	}

	@Test
	public void addHeader() {
		HttpMethodBase methodBase = new GetMethod("http://foo.com/bar");
		CommonsClientRequest request = new CommonsClientRequest(methodBase);
		assertNull(request.getMethodBase().getRequestHeader("Authorization"));
		request.addHeader("Authorization", "authorization header value");
		assertEquals("authorization header value", request.getMethodBase().getRequestHeader("Authorization").getValue());
	}

	@Test
	public void getQueryParameters() {
		HttpMethodBase methodBase = new GetMethod("http://foo.com/bar?abc=123");
		CommonsClientRequest request = new CommonsClientRequest(methodBase);
		request.setParameter("xyz", "789");
		request.setParameter("access_token", "pipe|delimited|token");
		Map<String, String> queryParameters = request.getQueryParameters();
		assertEquals("123", queryParameters.get("abc"));
		assertEquals("789", queryParameters.get("xyz"));
	}

	@Test
	public void getMethod() {
		assertEquals(HttpMethod.GET, new CommonsClientRequest(new GetMethod()).getHttpMethod());
		assertEquals(HttpMethod.POST, new CommonsClientRequest(new PostMethod()).getHttpMethod());
		assertEquals(HttpMethod.PUT, new CommonsClientRequest(new PutMethod()).getHttpMethod());
		assertEquals(HttpMethod.DELETE, new CommonsClientRequest(new DeleteMethod()).getHttpMethod());
	}
}
