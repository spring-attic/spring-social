package org.springframework.social.facebook.support.extractors;

import java.util.Map;

import org.springframework.social.facebook.EducationEntry;

public class EducationResponseExtractor extends AbstractResponseExtractor<EducationEntry> {

	public EducationEntry extractObject(Map<String, Object> educationEntryMap) {
		return new EducationEntry(
				extractReferenceFromMap((Map<String, Object>) educationEntryMap.get("school")), 
				extractReferenceFromMap((Map<String, Object>) educationEntryMap.get("year")), 
				(String) educationEntryMap.get("type"));
	}

}
