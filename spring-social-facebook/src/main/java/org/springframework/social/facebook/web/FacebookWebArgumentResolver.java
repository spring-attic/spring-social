/*
 * Copyright 2010 the original author or authors.
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
package org.springframework.social.facebook.web;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebArgumentResolver;
import org.springframework.web.context.request.NativeWebRequest;

/**
 * <p>
 * Web argument resolver that resolves arguments annotated with
 * {@link FacebookAccessToken} or {@link FacebookUserId}.
 * </p>
 * 
 * <p>
 * After a user has authenticated with Facebook via the XFBML
 * &lt;fb:login-button&gt; tag, their user ID and an access token are stored in
 * a cookie whose name is "fbs_{application key}". This web argument resolver
 * extracts that information from the cookie (if available) and supplies it to a
 * controller handler method as String values.
 * </p>
 * 
 * <p>
 * Both {@link FacebookAccessToken} and {@link FacebookUserId} are required by
 * default. If the access token or user ID cannot be resolved and if the
 * annotation is set to be required, an exception will be thrown indicating an
 * illegal state. If the annotation is set to not be required, a null will be
 * returned.
 * </p>
 * 
 * @author Craig Walls
 */
public class FacebookWebArgumentResolver implements WebArgumentResolver {

	private final String apiKey;

	public FacebookWebArgumentResolver(String apiKey) {
		this.apiKey = apiKey;
	}
	
	public Object resolveArgument(MethodParameter parameter, NativeWebRequest request) throws Exception {
		HttpServletRequest nativeRequest = (HttpServletRequest) request.getNativeRequest();
		return processParameterAnnotation(parameter, getFacebookCookieData(nativeRequest.getCookies(), apiKey));
	}

	private Map<String, String> getFacebookCookieData(Cookie[] cookies, String apiKey) {
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if (cookie.getName().equals("fbs_" + apiKey)) {
					return extractDataFromCookie(cookie.getValue());
				}
			}
		}

		return Collections.<String, String> emptyMap();
	}

	private Object processParameterAnnotation(MethodParameter parameter, Map<String, String> cookieData) {
		FacebookUserId userIdAnnotation = parameter.getParameterAnnotation(FacebookUserId.class);
		if (userIdAnnotation != null) {
			return resolveUserIdValue(parameter, cookieData, userIdAnnotation);
		}

		FacebookAccessToken accessTokenAnnotation = parameter.getParameterAnnotation(FacebookAccessToken.class);
		if (accessTokenAnnotation != null) {
			return resolveAccessTokenValue(parameter, cookieData, accessTokenAnnotation);
	    }

		return WebArgumentResolver.UNRESOLVED;
    }

	private Object resolveAccessTokenValue(MethodParameter parameter, Map<String, String> cookieData,
			FacebookAccessToken accessTokenAnnotation) {
		String accessToken = cookieData.get("access_token");
		if (accessToken != null) {
			accessToken = accessToken.replaceAll("\\%7C", "|");
		}

		if (accessToken != null || !accessTokenAnnotation.required()) {
			return accessToken;
		}

		throw new IllegalStateException("Parameter " + parameter.getParameterName()
				+ " is annotated with @FacebookAccessToken, but the data cannot be found in the Facebook cookie. "
				+ "Either ensure that Facebook authentication has taken place before arriving at this "
				+ "state or consider setting @FacebookAccessToken's required attribute to false.");
	}

	private Object resolveUserIdValue(MethodParameter parameter, Map<String, String> cookieData,
			FacebookUserId userIdAnnotation) {
		String uid = cookieData.get("uid");
		if (uid != null || !userIdAnnotation.required()) {
			return uid;
		}

		throw new IllegalStateException(
				"Parameter " + parameter.getParameterName()
				+ " is annotated with @FacebookUserId, but the data cannot be found in the Facebook cookie. "
				+ "Either ensure that Facebook authentication has taken place before arriving at this "
				+ "state or consider setting @FacebookUserId's required attribute to false.");
	}
	
	/*
	 * Stuff you should expect from this cookie:
	 *   access_token
	 *   expires
	 *   secret
	 *   session_key
	 *   sig
	 *   uid
	 */
	private Map<String, String> extractDataFromCookie(String cookieValue) {
		HashMap<String, String> data = new HashMap<String, String>();
		String[] fields = cookieValue.split("\\&");
		for (String field : fields) {
	        String[] keyValue = field.split("\\=");
	        data.put(keyValue[0], keyValue[1]);
        }
		return data;
	}
	
}