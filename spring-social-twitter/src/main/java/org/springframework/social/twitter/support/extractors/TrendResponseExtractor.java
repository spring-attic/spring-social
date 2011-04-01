package org.springframework.social.twitter.support.extractors;

import java.util.Map;

import org.springframework.social.twitter.types.Trend;

public class TrendResponseExtractor extends AbstractResponseExtractor<Trend> {
	
	public Trend extractObject(Map<String, Object> trendMap) {
		return new Trend((String) trendMap.get("name"), (String) trendMap.get("query"));
	}
	
}
