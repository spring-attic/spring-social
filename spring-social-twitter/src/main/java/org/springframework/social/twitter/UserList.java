package org.springframework.social.twitter;

public class UserList {
	private final long id;
	private final String name;
	private final String fullName;
	private final String uriPath;
	private final String description;
	private final String slug;
	private final boolean isPublic;
	private final boolean isFollowing;
	private final int memberCount;
	private final int subscriberCount;

	public UserList(long id, String name, String fullName, String uriPath, String description, String slug, 
			boolean isPublic, boolean isFollowing, int memberCount, int subscriberCount) {
		this.id = id;
		this.name = name;
		this.fullName = fullName;
		this.uriPath = uriPath;
		this.description = description;
		this.slug = slug;
		this.isPublic = isPublic;
		this.isFollowing = isFollowing;
		this.memberCount = memberCount;
		this.subscriberCount = subscriberCount;
	}

	public long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getFullName() {
		return fullName;
	}

	public String getUriPath() {
		return uriPath;
	}

	public String getDescription() {
		return description;
	}

	public String getSlug() {
		return slug;
	}

	public boolean isPublic() {
		return isPublic;
	}

	public boolean isFollowing() {
		return isFollowing;
	}

	public int getMemberCount() {
		return memberCount;
	}

	public int getSubscriberCount() {
		return subscriberCount;
	}

}


