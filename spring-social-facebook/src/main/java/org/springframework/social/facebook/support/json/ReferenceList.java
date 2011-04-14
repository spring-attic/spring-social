package org.springframework.social.facebook.support.json;

import java.util.List;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;
import org.springframework.social.facebook.types.Reference;

public class ReferenceList {
	
	private final List<Reference> list;

	@JsonCreator
	public ReferenceList(@JsonProperty("data") List<Reference> list) {
		this.list = list;
	}

	public List<Reference> getList() {
		return list;
	}
}
