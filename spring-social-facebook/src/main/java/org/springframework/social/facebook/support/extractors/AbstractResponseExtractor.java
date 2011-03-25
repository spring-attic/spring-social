package org.springframework.social.facebook.support.extractors;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.springframework.social.facebook.Reference;

public abstract class AbstractResponseExtractor<T> implements ResponseExtractor<T> {

	public List<T> extractObjects(List<Map<String, Object>> responseList) {
		if (responseList == null) {
			return Collections.emptyList();
		}

		List<T> list = new ArrayList<T>(responseList.size());
		for (Map<String, Object> responseMap : responseList) {
			list.add(extractObject(responseMap));
		}
		return Collections.unmodifiableList(list);
	}

	protected Date toDate(String dateString) {
		if (dateString == null) {
			return null;
		}

		try {
			return FB_DATE_FORMAT.parse(dateString);
		} catch (ParseException e) {
			return null;
		}
	}

	protected Reference extractReferenceFromMap(Map<String, Object> referenceMap) {
		return ResponseExtractors.REFERENCE_EXTRACTOR.extractObject(referenceMap);
	}

	protected List<Reference> extractReferences(Map<String, Object> referencesMap) {
		if (referencesMap == null) {
			return null;
		}
		return ResponseExtractors.REFERENCE_EXTRACTOR.extractObjects((List<Map<String, Object>>) referencesMap.get("data"));
	}

	private static final DateFormat FB_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.ENGLISH);

}
