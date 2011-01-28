package org.springframework.social.oauth2;

import static org.junit.Assert.*;

import java.net.URI;
import java.net.URISyntaxException;

import org.junit.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.social.intercept.ClientRequest;

public class OAuth2ClientRequestInterceptorTest {
	@Test
	public void currentOAuth2SpecInterceptor() throws Exception {
		OAuth2ClientRequestInterceptor interceptor = new OAuth2ClientRequestInterceptor("access_token");
		assertThatInterceptorWritesAuthorizationHeader(interceptor, "BEARER access_token");
	}
	
	@Test
	public void draft10Interceptor() throws Exception {
		OAuth2ClientRequestInterceptor interceptor = OAuth2ClientRequestInterceptor.draft10("access_token");
		assertThatInterceptorWritesAuthorizationHeader(interceptor, "OAuth access_token");
	}

	@Test
	public void draft8Interceptor() throws Exception {
		OAuth2ClientRequestInterceptor interceptor = OAuth2ClientRequestInterceptor.draft8("access_token");
		assertThatInterceptorWritesAuthorizationHeader(interceptor, "Token token=\"access_token\"");
	}

	private void assertThatInterceptorWritesAuthorizationHeader(OAuth2ClientRequestInterceptor interceptor,
			String expected) throws URISyntaxException {
		HttpHeaders headers = new HttpHeaders();
		ClientRequest request = new ClientRequest(headers, new byte[0], new URI("http://www.someprovider.com/api/resource"), HttpMethod.GET);
		interceptor.beforeExecution(request);
		assertEquals(expected, headers.getFirst("Authorization"));
	}
}
