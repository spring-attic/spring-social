package org.springframework.social.twitter;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.DefaultResponseErrorHandler;

public class TwitterErrorHandler extends DefaultResponseErrorHandler {

	// TODO: This is targeting a specific issue at the moment. I will probably
	// want to revisit this error handler to better address other Twitter errors
	// or even create a more generic error handler that can be used with the
	// other social networks.
	public boolean hasError(ClientHttpResponse response) throws IOException {
		if (response.getStatusCode().equals(HttpStatus.FORBIDDEN)) {
			return false;
		}
		return super.hasError(response);
	}

}
