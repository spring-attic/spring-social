package org.springframework.social.provider.oauth2;

import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.springframework.web.client.RestOperations;

public class OAuth2TemplateTest {
	private OAuth2Template oAuth2Template;
	private String accessTokenUrl;

	@Before
	public void setup() {
		String authorizeUrl = "http://www.someprovider.com/oauth/authorize?client_id={client_id}&redirect_uri={redirect_uri}&scope={scope}";
		accessTokenUrl = "http://www.someprovider.com/oauth/accessToken";
		oAuth2Template = new OAuth2Template("client_id", "client_secret", authorizeUrl, accessTokenUrl);
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
	public void exchangeForAccessToken() {
		final RestOperations rest = mock(RestOperations.class);
		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put("client_id", "client_id");
		parameters.put("client_secret", "client_secret");
		parameters.put("code", "authCode");
		parameters.put("redirect_uri", "http://www.someclient.com/connect/foo");
		parameters.put("grant_type", "authorization_code");
		Map<String, String> result = new HashMap<String, String>();
		result.put("access_token", "ACCESS_TOKEN");
		result.put("refresh_token", "REFRESH_TOKEN");
		when(rest.postForObject(eq(accessTokenUrl), eq(parameters), eq(Map.class))).thenReturn(result);
		
		String authorizeUrl = "http://www.someprovider.com/oauth/authorize?client_id={client_id}&redirect_uri={redirect_uri}";
		OAuth2Template oauth2Template = new OAuth2Template("client_id", "client_secret", authorizeUrl, accessTokenUrl) {
			protected RestOperations getRestOperations() {
				return rest;
			};
		};

		AccessToken accessToken = oauth2Template.exchangeForAccessToken("http://www.someclient.com/connect/foo",
				"authCode");
		assertEquals("ACCESS_TOKEN", accessToken.getValue());
		assertEquals("REFRESH_TOKEN", accessToken.getRefreshToken());
	}
}
