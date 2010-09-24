package org.springframework.social.linkedin;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "connections")
public class LinkedInConnections {
	@XmlElement(name = "person")
	private List<LinkedInProfile> connections;

	public List<LinkedInProfile> getConnections() {
		return connections;
	}
}
