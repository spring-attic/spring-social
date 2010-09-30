package org.springframework.social.oauth2;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class OAuth2ParameterClientRequestSignerTest {
	private OAuth2ParameterClientRequestSigner signer;

	@Before
	public void setup() throws Exception {
		signer = new OAuth2ParameterClientRequestSigner() {
			// stub, since this isn't what's being tested here anyway
			public String resolveAccessToken() {
				return "157702280911244|48044afccb06d771931a81f8-100001387295207|_w2_8EBi6Z2NdZiDLzKzE3uTmLA";
			}
		};
	}

	@Test
	@Ignore("FIX THIS TO USE THE NEW SIGNER")
	public void sign() throws Exception {
		// Map<String, String> queryParameters = new HashMap<String, String>();
		// queryParameters.put("a", "b");
		// queryParameters.put("c", "1");
		// FakeClientRequest request = new FakeClientRequest(new
		// URI("http://foo.com/bar?a=b&c=1"), HttpMethod.GET,
		// queryParameters);
		//
		// signer.sign(request);
		// assertEquals("157702280911244|48044afccb06d771931a81f8-100001387295207|_w2_8EBi6Z2NdZiDLzKzE3uTmLA",
		// request
		// .getQueryParameters().get("oauth_token"));
	}

	@Test
	@Ignore("FIX THIS TO USE THE NEW SIGNER")
	public void sign_customParameter() throws Exception {
		// Map<String, String> queryParameters = new HashMap<String, String>();
		// queryParameters.put("a", "b");
		// queryParameters.put("c", "1");
		// FakeClientRequest request = new FakeClientRequest(new
		// URI("http://foo.com/bar?a=b&c=1"), HttpMethod.GET,
		// queryParameters);
		//
		// signer.setParameterName("access_token");
		// signer.sign(request);
		// assertEquals("157702280911244|48044afccb06d771931a81f8-100001387295207|_w2_8EBi6Z2NdZiDLzKzE3uTmLA",
		// request
		// .getQueryParameters().get("access_token"));
	}
	
}
