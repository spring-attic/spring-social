package org.springframework.social.greenhouse;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 * Model class representing a Greenhouse group.
 * 
 * @author Craig Walls
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Group {
	@JsonProperty
	private String id;

	@JsonProperty
	private String label;

	/**
	 * The group ID.
	 * 
	 * @return the group ID.
	 */
	public String getId() {
		return id;
	}

	/**
	 * The group's label.
	 * 
	 * @return the group's label.
	 */
	public String getLabel() {
		return label;
	}
}
