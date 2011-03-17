package org.springframework.social.twitter;

public class SuggestionCategory {
	private final String name;
	private final String slug;
	private final int size;

	public SuggestionCategory(String name, String slug, int size) {
		this.name = name;
		this.slug = slug;
		this.size = size;
	}

	public String getName() {
		return name;
	}

	public String getSlug() {
		return slug;
	}

	public int getSize() {
		return size;
	}

}
