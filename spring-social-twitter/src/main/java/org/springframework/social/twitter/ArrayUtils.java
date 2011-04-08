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
package org.springframework.social.twitter;

class ArrayUtils {
	private ArrayUtils() {
	}

	public static String join(long[] items) {
		if (items.length == 0)
			return "";
		StringBuffer sb = new StringBuffer();
		sb.append(items[0]);
		for (int i = 1; i < items.length; i++) {
			sb.append(',').append(items[i]);
		}
		return sb.toString();
	}

	public static String join(Object[] items) {
		if (items.length == 0)
			return "";
		StringBuffer sb = new StringBuffer();
		sb.append(items[0]);
		for (int i = 1; i < items.length; i++) {
			sb.append(',').append(items[i]);
		}
		return sb.toString();
	}

}
