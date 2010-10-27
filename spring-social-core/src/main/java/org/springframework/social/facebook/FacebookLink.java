package org.springframework.social.facebook;

/**
 * Model class representing a link to be posted to a users Facebook wall.
 * 
 * @author Craig Walls
 */
public class FacebookLink {
	private final String link;
	private final String name;
	private final String caption;
	private final String description;

	/**
	 * Creates a FacebookLink.
	 * 
	 * @param link
	 *            The link's URL
	 * @param name
	 *            The name of the link
	 * @param caption
	 *            A caption to be displayed with the link
	 * @param description
	 *            The description of the link
	 */
	public FacebookLink(String link, String name, String caption, String description) {
		this.link = link;
		this.name = name;
		this.caption = caption;
		this.description = description;	
	}

	public String getLink() {
    	return link;
    }

	public String getName() {
    	return name;
    }

	public String getCaption() {
    	return caption;
    }

	public String getDescription() {
    	return description;
    }
}
