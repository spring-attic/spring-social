package org.springframework.social.facebook.support.extractors;

import java.util.Map;

import org.springframework.social.facebook.WorkEntry;

public class WorkResponseExtractor extends AbstractResponseExtractor<WorkEntry> {

	public WorkEntry extractObject(Map<String, Object> workEntryMap) {
		return new WorkEntry(extractReferenceFromMap((Map<String, Object>) workEntryMap.get("employer")),
				(String) workEntryMap.get("start_date"), (String) workEntryMap.get("end_date"));
	}

}
