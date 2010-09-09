package org.springframework.social.twitter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.DefaultResponseErrorHandler;

public class TwitterErrorHandler extends DefaultResponseErrorHandler {
	private static List<HttpStatus> NOT_ERRORS = Arrays.asList(HttpStatus.FORBIDDEN, HttpStatus.UNAUTHORIZED);
	
	// TODO: This is targeting a specific issue at the moment. I will probably
	// want to revisit this error handler to better address other Twitter errors
	// or even create a more generic error handler that can be used with the
	// other social networks.
	public boolean hasError(ClientHttpResponse response) throws IOException {
		if (NOT_ERRORS.contains(response.getStatusCode())) {
			return false;
		}

		return super.hasError(response);
	}

}
