/*
 * Copyright 2011 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
		return REFERENCE_EXTRACTOR.extractObject(referenceMap);
	}

	protected List<Reference> extractReferences(Map<String, Object> referencesMap) {
		if (referencesMap == null) {
			return null;
		}
		return REFERENCE_EXTRACTOR.extractObjects((List<Map<String, Object>>) referencesMap.get("data"));
	}

	private static final DateFormat FB_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.ENGLISH);

	private static final ReferenceResponseExtractor REFERENCE_EXTRACTOR = new ReferenceResponseExtractor();

}
