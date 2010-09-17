package org.springframework.social.facebook;

import static junit.framework.Assert.*;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.springframework.http.HttpMethod;
import org.springframework.social.oauth.FakeClientRequest;

public class FacebookRequestSignerTest {
	@Test
	public void sign() throws Exception {
		FacebookRequestSigner signer = new FacebookRequestSigner("SOME_ACCESS_TOKEN");
		Map<String, String> params = new HashMap<String, String>();
		FakeClientRequest request = new FakeClientRequest(new URI("http://foo.bar/com"), HttpMethod.GET, params);
		signer.sign(request);
		assertEquals("SOME_ACCESS_TOKEN", request.getQueryParameters().get("access_token"));
	}
}
