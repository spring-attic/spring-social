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

import java.io.IOException;
import java.util.Map;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.DefaultResponseErrorHandler;

public class FacebookErrorHandler extends DefaultResponseErrorHandler {

	public void handleError(ClientHttpResponse response) throws IOException {
		// Can't trust the status code to be helpful. Facebook errors are inconsistent in their choice of status code.		
		Map<String, String> errorDetails = extractErrorDetailsFromResponse(response);
		handleFacebookError(errorDetails);
	}

	/**
	 * Examines the error data returned from Facebook and throws the most applicable exception.
	 * @param errorDetails a Map containing a "type" and a "message" corresponding to the Graph API's error response structure.
	 */
	void handleFacebookError(Map<String, String> errorDetails) {
		String message = errorDetails.get("message");
		// Can't trust the type to be useful. It's often OAuthException, even for things not OAuth-related. Ignore it
		 
		if(message.contains("Requires extended permission")) {
			String requiredPermission = message.split(": ")[1];
			throw new InsufficientPermissionException(message, requiredPermission);
		} else if(message.equals("The member must be a friend of the current user.")) {
			throw new NotAFriendException(message);
		} else if(message.contains("Unknown path components")) {
			throw new GraphAPIException(message);
		} else if(message.equals("User must be an owner of the friendlist")) { // watch for pattern in similar message in other resources
			throw new OwnershipException(message);
		}
		
	}

	@SuppressWarnings("unchecked")
	private Map<String, String> extractErrorDetailsFromResponse(ClientHttpResponse response) throws IOException {
		ObjectMapper mapper = new ObjectMapper(new JsonFactory());
	    Map<String, Object> responseMap = mapper.<Map<String, Object>>readValue(response.getBody(), new TypeReference<Map<String, Object>>() {});
	    if(responseMap.containsKey("error")) {
	    	return (Map<String, String>) responseMap.get("error");
	    }
	    return null; // need to deal with this better...if it happens at all
	}
}
