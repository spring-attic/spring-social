package org.springframework.social.greenhouse;

import org.codehaus.jackson.annotate.JsonProperty;

/**
 * A model class containing a Greenhouse user's profile information.
 * 
 * @author Craig Walls
 */
public class GreenhouseProfile {
	@JsonProperty
	private long accountId;

	@JsonProperty
	private String displayName;

	@JsonProperty
	private String pictureUrl;

	/**
	 * The user's Greenhouse account ID.
	 * 
	 * @return The user's Greenhouse account ID.
	 */
	public long getAccountId() {
		return accountId;
	}

	/**
	 * The user's display name.
	 * 
	 * @return The user's display name.
	 */
	public String getDisplayName() {
		return displayName;
	}

	/**
	 * The URL of the user's profile picture.
	 * 
	 * @return The URL of the user's profile picture.
	 */
	public String getPictureUrl() {
		return pictureUrl;
	}
}
