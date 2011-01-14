package org.springframework.social.connect;

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.springframework.web.client.RestOperations;

public class AbstractOAuth2ServiceProviderTest {
	private FakeOAuth2ServiceProvider provider;
	private AccountConnectionRepository connectionRepository;

	@Before
	public void setup() {
		ServiceProviderParameters parameters = new ServiceProviderParameters("oauthProvider", "OAuth Provider",
				"api_key", "api_secret", 12345L, null, "http://www.oauthprovider.com/oauth/authorize",
				"http://www.oauthprovider.com/oauth/accesstoken");
		connectionRepository = mock(AccountConnectionRepository.class);
		provider = new FakeOAuth2ServiceProvider(parameters, connectionRepository);
	}

	@Test(expected = UnsupportedOperationException.class)
	public void fetchNewRequestToken_shouldNotBeSupported() {
		provider.fetchNewRequestToken("http://www.foo.com/oauth/callback");
	}

	@Test(expected = UnsupportedOperationException.class)
	public void connect_withRequestTokenShouldNotBeSupported() {
		AuthorizedRequestToken requestToken = new AuthorizedRequestToken(new OAuthToken("accessToken"), "verifier");
		provider.connect(1L, requestToken);
	}

	@Test
	public void connect() {
		RestOperations mockRest = mock(RestOperations.class);
		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put("client_id", "api_key");
		parameters.put("client_secret", "api_secret");
		parameters.put("code", "authorizationCode");
		parameters.put("redirect_uri", "http://www.foo.com/oauth/callback");
		parameters.put("grant_type", "authorization_code");
		Map<String, String> authorizationResponse = new HashMap<String, String>();
		authorizationResponse.put("access_token", "accessToken");
		authorizationResponse.put("refresh_token", "refreshToken");

		when(mockRest.postForObject(eq("http://www.oauthprovider.com/oauth/accesstoken"), eq(parameters),
						eq(Map.class))).thenReturn(authorizationResponse);

		provider.setRestOperations(mockRest);
		provider.connect(1L, "http://www.foo.com/oauth/callback", "authorizationCode");

		verify(connectionRepository).addConnection(eq(1L), eq("oauthProvider"),
				argThat(new OAuthTokenMatcher("accessToken", "refreshToken")), eq("habuma"),
				eq("http://www.oauthprovider.com/habuma"));
	}

	@Test
	public void refresh() {
		RestOperations mockRest = mock(RestOperations.class);
		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put("client_id", "api_key");
		parameters.put("client_secret", "api_secret");
		parameters.put("refresh_token", "oldRefreshToken");
		parameters.put("grant_type", "refresh_token");
		Map<String, String> refreshResponse = new HashMap<String, String>();
		refreshResponse.put("access_token", "newAccessToken");
		refreshResponse.put("refresh_token", "newRefreshToken");
		when(connectionRepository.getRefreshToken(eq(1L), eq("oauthProvider"), eq("habuma"))).thenReturn(
				"oldRefreshToken");
		when(mockRest.postForObject(eq("http://www.oauthprovider.com/oauth/accesstoken"), eq(parameters),
						eq(Map.class))).thenReturn(refreshResponse);

		provider.setRestOperations(mockRest);
		provider.refreshConnection(1L, "habuma");

		verify(connectionRepository).updateConnection(eq(1L), eq("oauthProvider"),
				argThat(new OAuthTokenMatcher("newAccessToken", "newRefreshToken")), eq("habuma"));
	}
}

class OAuthTokenMatcher extends ArgumentMatcher<OAuthToken> {
	private final String expectedValue;
	private final String expectedRefreshToken;

	public OAuthTokenMatcher(String expectedValue, String expectedRefreshToken) {
		this.expectedValue = expectedValue;
		this.expectedRefreshToken = expectedRefreshToken;
	}

	@Override
	public boolean matches(Object argument) {
		OAuthToken token = (OAuthToken) argument;
		String refreshToken = token.getRefreshToken();
		return token.getValue().equals(expectedValue)
				&& ((refreshToken != null && expectedRefreshToken != null && refreshToken.equals(expectedRefreshToken)) || (refreshToken == null && expectedRefreshToken == null));
	}

}

class FakeOAuth2ServiceProvider extends AbstractOAuth2ServiceProvider<Object> {
	private RestOperations restOperations;

	public FakeOAuth2ServiceProvider(ServiceProviderParameters parameters,
			AccountConnectionRepository connectionRepository) {
		super(parameters, connectionRepository);
	}

	@Override
	protected Object createServiceOperations(OAuthToken accessToken) {
		return null;
	}

	@Override
	protected String fetchProviderAccountId(Object serviceOperations) {
		return "habuma";
	}

	@Override
	protected String buildProviderProfileUrl(String providerAccountId, Object serviceOperations) {
		return "http://www.oauthprovider.com/habuma";
	}

	@Override
	protected RestOperations getRestOperations() {
		return restOperations;
	}

	void setRestOperations(RestOperations restOperations) {
		this.restOperations = restOperations;
	}

	public Serializable getProviderUserProfile(OAuthToken accessToken) {
		return null;
	}
}
