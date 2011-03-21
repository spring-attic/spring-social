package org.springframework.social.twitter.support;

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
