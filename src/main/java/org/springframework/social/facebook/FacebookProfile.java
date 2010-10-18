package org.springframework.social.facebook;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 * Model class containing a Facebook user's profile information.
 * 
 * @author Craig Walls
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class FacebookProfile {
	@JsonProperty
	private long id;

	@JsonProperty
	private String name;

	@JsonProperty("first_name")
	private String firstName;

	@JsonProperty("last_name")
	private String lastName;

	@JsonProperty
	private String email;

	/**
	 * The user's Facebook ID
	 * 
	 * @return The user's Facebook ID
	 */
	public long getId() {
		return id;
	}

	/**
	 * The user's full name
	 * 
	 * @return The user's full name
	 */
	public String getName() {
		return name;
	}

	/**
	 * The user's first name
	 * 
	 * @return The user's first name
	 */
	public String getFirstName() {
		return firstName;
	}

	/**
	 * The user's last name
	 * 
	 * @return The user's last name
	 */
	public String getLastName() {
		return lastName;
	}

	/**
	 * The user's email address
	 * 
	 * @return The user's email address
	 */
	public String getEmail() {
	    return email;
    }
}
