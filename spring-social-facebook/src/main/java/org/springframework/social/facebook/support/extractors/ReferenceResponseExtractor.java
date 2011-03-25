package org.springframework.social.facebook.support.extractors;

import java.util.Map;

import org.springframework.social.facebook.Reference;

public class ReferenceResponseExtractor extends AbstractResponseExtractor<Reference> {

	public Reference extractObject(Map<String, Object> referenceMap) {
		if (referenceMap == null) {
			return null;
		}
		return new Reference((String) referenceMap.get("id"), (String) referenceMap.get("name"));
	}

}
