package org.springframework.social.twitter.support.json;

import java.util.List;
import java.util.Map;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import org.springframework.social.twitter.types.Trend;

@JsonIgnoreProperties(ignoreUnknown=true)
public class DailyTrendsList extends AbstractTrendsList {

	@JsonCreator
	public DailyTrendsList(@JsonProperty("trends") Map<String, List<Trend>> trends) {
		super(trends, DAILY_TREND_DATE_FORMAT);
	}

}
