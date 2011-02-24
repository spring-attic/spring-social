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

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebArgumentResolver;
import org.springframework.web.context.request.NativeWebRequest;

/**
 * Web argument resolver that resolves arguments annotated with {@link FacebookCookieValue}. 
 * When using Facebook's JavaScript API, the FB.init() call will set a cookie whose name is "fbs_{appId}" if the user is signed into Facebook and if 
 * they have granted the application permission to access their profile. 
 * This web argument resolver extracts that information from the cookie (if available) and supplies it to a controller handler method as String values. 
 * {@link FacebookCookieValue} is required by default. If the cookie value cannot be resolved and if the annotation is set to be 
 * required, an exception will be thrown indicating an illegal state. If the annotation is set to not be required, null will be returned.
 * @author Craig Walls
 */
public class FacebookWebArgumentResolver implements WebArgumentResolver {

	private final String appId;
	
	private final String appSecret;

	/**
	 * Construct a FacebookWebArgumentResolver given the Facebook app id and secret.
	 * The application secret will be used to verify the cookie signature.
	 */
	public FacebookWebArgumentResolver(String appId, String appSecret) {
		this.appId = appId;
		this.appSecret = appSecret;
	}
	
	public Object resolveArgument(MethodParameter parameter, NativeWebRequest request) throws Exception {
		FacebookCookieValue annotation = parameter.getParameterAnnotation(FacebookCookieValue.class);
		if (annotation == null) {
			return WebArgumentResolver.UNRESOLVED;
		}
		HttpServletRequest nativeRequest = (HttpServletRequest) request.getNativeRequest();
		Map<String, String> cookieData = FacebookCookieParser.getFacebookCookieData(nativeRequest.getCookies(), appId, appSecret);
		String key = annotation.value();
		if (!cookieData.containsKey(key) && annotation.required()) {
			throw new IllegalStateException("Missing Facebook cookie value '" + key + "'");
		}
		return cookieData.get(key);
	}

}