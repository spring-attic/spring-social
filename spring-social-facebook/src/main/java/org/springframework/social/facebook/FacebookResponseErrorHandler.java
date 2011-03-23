package org.springframework.social.facebook;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.DefaultResponseErrorHandler;

public class FacebookResponseErrorHandler extends DefaultResponseErrorHandler {

	public boolean hasError(ClientHttpResponse response) throws IOException {
		boolean hasError = super.hasError(response);

		// TODO: These sysouts are here for debugging purposes while I build up the API and figure out
		//       what different kinds of errors look like from FB. They should (and will) go away.
		if (hasError) {
			System.out.println(response.getStatusCode() + " : " + response.getStatusText());

			BufferedReader reader = new BufferedReader(new InputStreamReader(response.getBody()));
			while (reader.ready()) {
				System.out.println("    " + reader.readLine());
			}
		}

		return hasError;
	}

	public void handleError(ClientHttpResponse response) throws IOException {
		super.handleError(response);
	}

}
