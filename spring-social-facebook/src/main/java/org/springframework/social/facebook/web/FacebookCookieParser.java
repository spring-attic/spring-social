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
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
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
	public static Map<String, String> getFacebookCookieData(Cookie[] cookies, String apiKey, String appSecret) {
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if (cookie.getName().equals("fbs_" + apiKey)) {
					Map<String, String> cookieData = extractDataFromCookie(cookie.getValue().trim());
					String signature = calculateSignature(appSecret, cookieData);
					if (signature.equals(cookieData.get("sig"))) {
						return cookieData;
					}
					System.out.println(signature);
					break;
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

	private static String calculateSignature(String appSecret, Map<String, String> cookieData) {
		String payload = "";
		List<String> keys = new ArrayList<String>(cookieData.keySet());
		Collections.sort(keys);
		for (String key : keys) {
			if (!key.equals("sig")) {
				payload += key + "=" + cookieData.get(key);
			}
		}
		return md5(payload + appSecret);
	}

	private static String md5(String in) {
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");

			byte[] hash = md.digest(in.getBytes("UTF-8"));

			StringBuffer sb = new StringBuffer();
			for (byte b : hash) {
				if (b >= 0 && b < 16)
					sb.append('0');
				sb.append(Integer.toHexString(b & 0xff));
			}

			return sb.toString();
		} catch (NoSuchAlgorithmException wontHappen) {
			return null;
		} catch (UnsupportedEncodingException wontHappen) {
			return null;
		}
	}

	private FacebookCookieParser() {
	}

}
