package org.springframework.social.linkedin;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * A model class containing a list of a user's connections on LinkedIn.
 * 
 * @author Craig Walls
 */
@XmlRootElement(name = "connections")
public class LinkedInConnections {
	@XmlElement(name = "person")
	List<LinkedInProfile> connections;

	/**
	 * Retrieves the list of connected profiles.
	 * 
	 * @return a list of connected profiles
	 */
	public List<LinkedInProfile> getConnections() {
		return connections;
	}
}
