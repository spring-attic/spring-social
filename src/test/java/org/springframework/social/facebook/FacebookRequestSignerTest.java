package org.springframework.social.facebook;

import java.util.HashMap;

import org.junit.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;

public class FacebookRequestSignerTest {
	@Test
	public void sign() throws Exception {
		FacebookRequestSigner signer = new FacebookRequestSigner("SOME_ACCESS_TOKEN");
		signer.sign(HttpMethod.GET, new HttpHeaders(), "http://foo.com/bar", new HashMap<String, String>());

		// FacebookRequestSigner signer = new
		// FacebookRequestSigner("SOME_ACCESS_TOKEN");
		// Map<String, String> params = new HashMap<String, String>();
		// FakeClientRequest request = new FakeClientRequest(new
		// URI("http://foo.bar/com"), HttpMethod.GET, params);
		// // TODO: FIX THIS
		// // signer.sign(request);
		// assertEquals("SOME_ACCESS_TOKEN",
		// request.getQueryParameters().get("access_token"));
	}
}
