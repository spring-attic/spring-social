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
package org.springframework.social.facebook.web;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.Cookie;

/**
 * Helper class that helps extract data from a Facebook cookie.
 * @author Craig Walls
 */
class FacebookCookieParser {

	/**
	 * Looks for a Facebook cookie for the given API Key and returns its data as key/value pairs in a Map.
	 */
	public static Map<String, String> getFacebookCookieData(Cookie[] cookies, String apiKey) {
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if (cookie.getName().equals("fbs_" + apiKey)) {
					return extractDataFromCookie(cookie.getValue());
				}
			}
		}

		return Collections.<String, String> emptyMap();
	}

	private static Map<String, String> extractDataFromCookie(String cookieValue) {
		HashMap<String, String> data = new HashMap<String, String>();
		String[] fields = cookieValue.split("\\&");
		for (String field : fields) {
			String[] keyValue = field.split("\\=");
			try {
				data.put(keyValue[0], URLDecoder.decode(keyValue[1], "UTF-8"));
			} catch (UnsupportedEncodingException wontHappen) {
			}
		}
		return data;
	}

	private FacebookCookieParser() {
	}

}
