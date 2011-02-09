package org.springframework.social.oauth2;

import static org.junit.Assert.*;
import static org.springframework.http.HttpMethod.*;
import static org.springframework.web.client.test.RequestMatchers.*;

import org.junit.Before;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.test.MockRestServiceServer;
import org.springframework.web.client.test.ResponseCreators;

public class OAuth2TemplateTest {
	
	private static final String ACCESS_TOKEN_URL = "http://www.someprovider.com/oauth/accessToken";

	private OAuth2Template oAuth2Template;
	
	private String accessTokenUrl;

	@Before
	public void setup() {
		String authorizeUrl = "http://www.someprovider.com/oauth/authorize?client_id={client_id}&redirect_uri={redirect_uri}&scope={scope}";
		oAuth2Template = new OAuth2Template("client_id", "client_secret", authorizeUrl, ACCESS_TOKEN_URL);
	}

	@Test
	public void buildAuthorizeUrl() {
		String expected = "http://www.someprovider.com/oauth/authorize?client_id=client_id&redirect_uri=http://www.someclient.com/connect/foo&scope=read,write";
		String actual = oAuth2Template.buildAuthorizeUrl("http://www.someclient.com/connect/foo", "read,write");
		assertEquals(expected, actual);
	}

	@Test
	public void buildAuthorizeUrl_noScopeInParameters() {
		String expected = "http://www.someprovider.com/oauth/authorize?client_id=client_id&redirect_uri=http://www.someclient.com/connect/foo&scope=";
		String actual = oAuth2Template.buildAuthorizeUrl("http://www.someclient.com/connect/foo", null);
		assertEquals(expected, actual);
	}

	@Test
	public void buildAuthorizeUrl_noScopeInUrlTemplate() {
		String authorizeUrl = "http://www.someprovider.com/oauth/authorize?client_id={client_id}&redirect_uri={redirect_uri}";
		oAuth2Template = new OAuth2Template("client_id", "client_secret", authorizeUrl, null);
		String expected = "http://www.someprovider.com/oauth/authorize?client_id=client_id&redirect_uri=http://www.someclient.com/connect/foo";
		String actual = oAuth2Template.buildAuthorizeUrl("http://www.someclient.com/connect/foo", "read");
		assertEquals(expected, actual);
	}

	@Test
	public void exchangeForAccess() {
		HttpHeaders responseHeaders = new HttpHeaders();
		// TODO : Facebook returns the token as URL-encoded with text/plain content type. Test for other providers.
		responseHeaders.setContentType(MediaType.TEXT_PLAIN);
		MockRestServiceServer mockServer = MockRestServiceServer.createServer((RestTemplate) oAuth2Template
				.getRestOperations());
		mockServer.expect(requestTo(ACCESS_TOKEN_URL))
				.andExpect(method(POST))
				.andExpect(body("client_id=client_id&client_secret=client_secret&code=code&" +
								"redirect_uri=http%3A%2F%2Fwww.someclient.com%2Fcallback&grant_type=authorization_code"))
				.andRespond(ResponseCreators.withResponse(new ClassPathResource("accessToken.json", getClass()),
								responseHeaders));
		AccessGrant accessGrant = oAuth2Template.exchangeForAccess("code", "http://www.someclient.com/callback");
		assertEquals("accessToken", accessGrant.getAccessToken());
		assertEquals("refreshToken", accessGrant.getRefreshToken());
	}
	
	@Test
	public void signProtectedResourceRequest() {
		// TODO
	}

}
