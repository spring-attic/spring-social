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
package org.springframework.social.twitter.support.extractors;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.springframework.social.twitter.types.Trend;
import org.springframework.social.twitter.types.Trends;

public class TrendsListResponseExtractor extends AbstractResponseExtractor<List<Trends>> {
	private TrendResponseExtractor trendExtractor;

	private final DateFormat dateFormat;
	
	public TrendsListResponseExtractor(DateFormat dateFormat) {
		this.dateFormat = dateFormat;
		this.trendExtractor = new TrendResponseExtractor();
	}

	@SuppressWarnings("unchecked")
	public List<Trends> extractObject(Map<String, Object> responseMap) {
		Map<String, Object> trendsMap = (Map<String, Object>) responseMap.get("trends");
		List<Trends> trendsList = new ArrayList<Trends>(trendsMap.keySet().size());
		for (String trendDate : trendsMap.keySet()) {
			List<Map<String, Object>> trendsMapList = (List<Map<String, Object>>) trendsMap.get(trendDate);
			List<Trend> trendList = trendExtractor.extractObjects(trendsMapList);
			trendsList.add(new Trends(toDate(trendDate, dateFormat), trendList));
		}
		Collections.sort(trendsList, new Comparator<Trends>() {
			public int compare(Trends t1, Trends t2) {
				return t1.getTime().getTime() > t2.getTime().getTime() ? -1 : 1;
			}
		});
		return trendsList;
	}

	public static final DateFormat SIMPLE_TREND_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

	public static final DateFormat LONG_TREND_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	public static final DateFormat LOCAL_TREND_DATE_FORMAT = new SimpleDateFormat("yyyy-mm-dd'T'HH:mm:ss'Z'");

}
