/*
 * Copyright 2015 the original author or authors.
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
package org.springframework.social.support;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.DefaultResponseErrorHandler;

public class LoggingErrorHandler extends DefaultResponseErrorHandler {

	private static final Log LOG = LogFactory.getLog(LoggingErrorHandler.class);
	
	@Override
	public void handleError(ClientHttpResponse response) throws IOException {
		
		BufferingClientHttpResponse bufferedResponse = new BufferingClientHttpResponse(response);
		
		InputStream bodyStream = bufferedResponse.getBody();
		BufferedReader reader = new BufferedReader(new InputStreamReader(bodyStream));
		StringBuffer buffer = new StringBuffer();
		while(reader.ready()) {
			buffer.append(reader.readLine());
		}
		
		if (LOG.isErrorEnabled()) {
			LOG.error("Response body: " + buffer.toString());
		}
		
		
		super.handleError(bufferedResponse);
	}
	
}
