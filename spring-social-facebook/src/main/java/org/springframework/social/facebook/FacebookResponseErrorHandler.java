/*
 * Copyright 2011 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
