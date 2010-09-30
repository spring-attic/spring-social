package org.springframework.social.oauth;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.client.AbstractClientHttpRequest;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.CommonsClientHttpRequestFactory;

public class OAuthSigningClientHttpRequestFactory extends CommonsClientHttpRequestFactory {
	private final CommonsClientHttpRequestFactory delegate;
	private final OAuthClientRequestSigner signer;

	public OAuthSigningClientHttpRequestFactory(OAuthClientRequestSigner signer) {
		this.signer = signer;
		this.delegate = new CommonsClientHttpRequestFactory();
	}

	public ClientHttpRequest createRequest(URI uri, HttpMethod httpMethod) throws IOException {
		ClientHttpRequest originalRequest = new SignableClientHttpRequest(delegate.createRequest(uri, httpMethod));
		return originalRequest;
	}


	private class SignableClientHttpRequest extends AbstractClientHttpRequest {
		private final AbstractClientHttpRequest original;

		public SignableClientHttpRequest(ClientHttpRequest original) {
			this.original = (AbstractClientHttpRequest) original;
		}

		protected ClientHttpResponse executeInternal(HttpHeaders headers, byte[] bufferedOutput) throws IOException {
			Map<String, String> bodyParameters = extractBodyParameters(headers.getContentType(), bufferedOutput);
			signer.sign(getMethod(), original.getHeaders(), getURI().toString(), bodyParameters);
			original.getBody().write(bufferedOutput);
			return original.execute();
		}

		private Map<String, String> extractBodyParameters(MediaType bodyType, byte[] bodyBytes) {
			Map<String, String> params = new HashMap<String, String>();

			if (bodyType != null && bodyType.equals(MediaType.APPLICATION_FORM_URLENCODED)) {
				String[] paramPairs = new String(bodyBytes).split("&");
				for (String pair : paramPairs) {
					String[] keyValue = pair.split("=");
					if (keyValue.length == 2) {
						params.put(keyValue[0], decode(keyValue[1]));
					}
				}
			}
			return params;
		}

		public URI getURI() {
			return original.getURI();
		}

		public HttpMethod getMethod() {
			return original.getMethod();
		}
	}

	private String decode(String in) {
		try {
			return URLDecoder.decode(in, "UTF-8");
		} catch (UnsupportedEncodingException wontHappen) {
			return in;
		}
	}
}
