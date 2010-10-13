package org.springframework.social.greenhouse;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Group {
	@JsonProperty
	private String id;

	@JsonProperty
	private String label;

	public String getId() {
		return id;
	}

	public String getLabel() {
		return label;
	}
}
